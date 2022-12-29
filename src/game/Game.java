package game;
import java.util.logging.Level;
import controller.GameFixture;
import io.*;
import server.*;
public class Game {
    public static void run(int port) throws Exception {
        GameFixture game=GoServer.setUpStandaloneGame(port);
        game.startPlayerBackends(); // assuming they are local
        GameFixture.playSillyGame(game,1);
        game.stop();
        //GTPBackEnd.sleep2(GTPBackEnd.yield);
        NamedThreadGroup.printThraedsAtEnd();
    }
    public static void main(String[] args) throws Exception {
        System.out.println(Init.first);
        Logging.setLevels(Level.SEVERE);
        for(int i=0;i<1;++i) { //
            for(int port:IO.ports) try {
                run(port);
            } catch(Exception e) {
                e.printStackTrace();
                if(port==IO.defaultPort) System.out.println("default port, probably ok.");
            }
        }
        NamedThreadGroup.printThraedsAtEnd();
    }
}
