package core.cli;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import core.api.*;
import core.session.BasicGameSession;
import games.go.*;
import games.ttt.*;

public final class CliMain {

    public static void main(String[] args) throws Exception {
        CliMain cli = new CliMain(new BufferedReader(new InputStreamReader(System.in)), new PrintWriter(System.out, true));
        cli.run(args);
    }

    /**
     * Entry point that accepts an explicit reader, making this class testable.
     */
    public void run(String[] args) throws Exception {
        String gameId = argValue(args, "--game");
        if (gameId == null) gameId = argValue(args, "-g");
        if (gameId == null) gameId = "go";
        //if (gameId == null) gameId = "ttt";

        switch (gameId) {
            case "go" -> runGo();
            case "ttt" -> runTtt();
            default -> {
                out.println("unknown game id: " + gameId);
                out.println("available games: go, ttt");
                out.println("usage: CliMain --game <id>");
                out.flush();
                System.exit(2);
            }
        }
    }

    private void runGo() throws Exception {
        GoPlugin goPlugin = new GoPlugin();
        this.plugin = goPlugin;
        GoSpec spec = goPlugin.defaultSpec();

        GameSession<GoState, GoMove> goSession =
                new BasicGameSession<GoState, GoMove, GoSpec>("local", goPlugin, spec);
        this.session = goSession;

        goSession.setRole("p1", Role.playBlack);
        goSession.setRole("p2", Role.playWhite);

        runLoop(goPlugin, goSession);
    }

    private void runTtt() throws Exception {
        TttPlugin tttPlugin = new TttPlugin();
        this.plugin = tttPlugin;
        TttSpec spec = tttPlugin.defaultSpec();

        GameSession<TttState, TttMove> tttSession =
                new BasicGameSession<TttState, TttMove, TttSpec>("local", tttPlugin, spec);
        this.session = tttSession;

        tttSession.setRole("p1", Role.playBlack); // X
        tttSession.setRole("p2", Role.playWhite); // O

        runLoop(tttPlugin, tttSession);
    }

    private <S extends GameState, M extends Move> void runLoop(
            GamePlugin<S, M, ?> plugin, GameSession<S, M> session)
            throws Exception {
        BufferedReader in = reader;
        Actor actor = new Actor("p1", Who.commandLine);

        while (true) {
            //Logging.mainLogger.info("run loop");
            out.println(plugin.renderer().render(session.state()));
            out.print("> ");
            out.flush();
            String line = in.readLine();
            if (line == null) return;

            String trimmed = line.trim();
            if (trimmed.equals("quit")) return;

            if (trimmed.equals("help") || trimmed.equals("?")) {
                out.println("commands:");
                out.println("  help | ?    show this help");
                out.println("  quit        exit");
                out.println("moves:");
                out.println("  parsed by the current game's MoveCodec");
                out.flush();
                continue;
            }

            M move;
            try {
                move = plugin.moveCodec().parse(line);
            } catch (RuntimeException e) {
                out.println("parse error: " + e.getMessage());
                out.flush();
                continue;
            }

            ApplyResult<S> result = session.submit(actor, move);
            if (!result.accepted) {
                out.println("rejected: " + result.message);
                out.flush();
            }

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

    public CliMain(BufferedReader reader, PrintWriter out) {
        this.reader = reader;
        this.out = out;
    }

    private final BufferedReader reader;
    private final PrintWriter out;
    private GamePlugin<? extends GameState, ? extends Move, ?> plugin;
    private GameSession<? extends GameState, ? extends Move> session;
}
