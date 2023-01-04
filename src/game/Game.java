package game;
import controller.GameFixture;
import io.*;
import io.IO.End.Holder;
import model.Model;
import server.NamedThreadGroup;
public class Game {
    public static GameFixture setUpStandaloneLocalGame(int port) {
        Holder blackHolder=null;
        Holder whiteHolder=null;
        if(port==IO.noPort) {
            blackHolder=Holder.duplex();
            whiteHolder=Holder.duplex();
        } else if(port==IO.anyPort) {
            blackHolder=Holder.trick(port);
            whiteHolder=Holder.trick(port);
        } else { // a real port, might be in use
            // if server is running maybe do not use trick?
            // this may help us consolidate.
            blackHolder=Holder.trick(port);
            whiteHolder=Holder.trick(port);
        }
        Model recorder=new Model("recorder");
        GameFixture game=new GameFixture(recorder);
        game.setupServerSide(blackHolder.front,whiteHolder.front);
        game.startGame();
        // normally the back ends may be started first?
        game.blackFixture.setupBackEnd(blackHolder.back,game.blackName(),game.id);
        game.whiteFixture.setupBackEnd(whiteHolder.back,game.whiteName(),game.id);
        System.out.println("black thread: "+IO.toString(game.blackFixture.backEnd.namedThread));
        System.out.println("white thread: "+IO.toString(game.whiteFixture.backEnd.namedThread));
        return game;
    }
    public static GameFixture setupLocalGameForShove(Model recorder) {
        Holder blackHolder=null;
        Holder whiteHolder=null;
        blackHolder=Holder.trick(IO.anyPort);
        whiteHolder=Holder.trick(IO.anyPort);
        GameFixture game=new GameFixture(recorder);
        game.setupServerSide(blackHolder.front,whiteHolder.front);
        // game thread is not started!
        // let's try starting it. not a good idea.
        // 12/29/22 let's try again ... server tests are hanging.
        // try again later
        //game.startGame();
        game.blackFixture.setupBackEnd(blackHolder.back,game.blackName(),game.id);
        game.whiteFixture.setupBackEnd(whiteHolder.back,game.whiteName(),game.id);
        game.startPlayerBackends();
        return game;
    }
    public static void run(int port) throws Exception {
        GameFixture game=Game.setUpStandaloneLocalGame(port);
        game.startPlayerBackends(); // assuming they are local
        GameFixture.playSillyGame(game,1);
        game.stop();
        //GTPBackEnd.sleep2(GTPBackEnd.yield);
        NamedThreadGroup.printThraedsAtEnd();
    }
    public static void main(String[] args) throws Exception {
        System.out.println(Init.first);
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
