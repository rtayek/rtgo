package core.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import core.api.*;
import core.session.*;
import games.go.*;

final class OldCliMain {
    static void main(String[] args) throws Exception {
        GoPlugin plugin = new GoPlugin();
        GoSpec spec = plugin.defaultSpec();

        GameSession<GoState, GoMove> session = new BasicGameSession<GoState, GoMove, GoSpec>("local", plugin, spec);

        
        session.setRole("p1", Role.playBlack);
        session.setRole("p2", Role.playWhite);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Actor actor = new Actor("p1", Who.commandLine);

        while(true) {
            System.out.println(plugin.renderer().render(session.state()));
            System.out.print("> ");
            String line = in.readLine();
            if(line == null) return;
            if(line.trim().equals("quit")) return;

            GoMove move = plugin.moveCodec().parse(line);
            ApplyResult<GoState> result = session.submit(actor, move);
            if(!result.accepted) System.out.println("rejected: " + result.message);

            // flip actor for quick local 2-player (temporary)
            actor = actor.participantId().equals("p1") ? new Actor("p2", Who.commandLine) : new Actor("p1", Who.commandLine);
        }
    }
}
