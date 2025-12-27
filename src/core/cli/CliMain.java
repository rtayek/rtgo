package core.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import core.api.*;
import core.session.BasicGameSession;
import games.go.*;
import games.ttt.*;

public final class CliMain {

    public static void main(String[] args) throws Exception {
        String gameId = argValue(args, "--game");
        if (gameId == null) gameId = argValue(args, "-g");
        //if (gameId == null) gameId = "go";
        if (gameId == null) gameId = "ttt";

        if (gameId.equals("go")) {
            runGo();
            return;
        }

        if (gameId.equals("ttt")) {
            runTtt();
            return;
        }

        System.out.println("unknown game id: " + gameId);
        System.out.println("available games: go, ttt");
        System.out.println("usage: CliMain --game <id>");
        System.exit(2);
    }

    private static void runGo() throws Exception {
        GoPlugin plugin = new GoPlugin();
        GoSpec spec = plugin.defaultSpec();

        GameSession<GoState, GoMove> session =
                new BasicGameSession<GoState, GoMove, GoSpec>("local", plugin, spec);

        session.setRole("p1", Role.playBlack);
        session.setRole("p2", Role.playWhite);

        runLoop(plugin, session);
    }

    private static void runTtt() throws Exception {
        TttPlugin plugin = new TttPlugin();
        TttSpec spec = plugin.defaultSpec();

        GameSession<TttState, TttMove> session =
                new BasicGameSession<TttState, TttMove, TttSpec>("local", plugin, spec);

        session.setRole("p1", Role.playBlack); // X
        session.setRole("p2", Role.playWhite); // O

        runLoop(plugin, session);
    }

    private static <S extends GameState, M extends Move> void runLoop(GamePlugin<S, M, ?> plugin, GameSession<S, M> session)
            throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Actor actor = new Actor("p1", Who.commandLine);

        while (true) {
            System.out.println(plugin.renderer().render(session.state()));
            System.out.print("> ");
            String line = in.readLine();
            if (line == null) return;

            String trimmed = line.trim();
            if (trimmed.equals("quit")) return;

            if (trimmed.equals("help") || trimmed.equals("?")) {
                System.out.println("commands:");
                System.out.println("  help | ?    show this help");
                System.out.println("  quit        exit");
                System.out.println("moves:");
                System.out.println("  parsed by the current game's MoveCodec");
                continue;
            }

            M move;
            try {
                move = plugin.moveCodec().parse(line);
            } catch (RuntimeException e) {
                System.out.println("parse error: " + e.getMessage());
                continue;
            }

            ApplyResult<S> result = session.submit(actor, move);
            if (!result.accepted) System.out.println("rejected: " + result.message);

            // quick local 2-player (temporary)
            actor = actor.participantId().equals("p1")
                    ? new Actor("p2", Who.commandLine)
                    : new Actor("p1", Who.commandLine);
        }
    }

    private static String argValue(String[] args, String key) {
        for (int i = 0; i + 1 < args.length; i++) {
            if (args[i].equals(key)) return args[i + 1];
        }
        return null;
    }

    private CliMain() {
    }
}
