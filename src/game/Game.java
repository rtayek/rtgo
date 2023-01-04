package game;
import java.io.*;
import controller.*;
import io.*;
import io.IO.End.Holder;
import model.Model;
import server.NamedThreadGroup;
import sgf.HexAscii;
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
    public static void loadExistinGame(Model recorder,GameFixture game) {
        File file=new File("serverGames/game1.sgf");
        if(!file.exists()) Logging.mainLogger.warning(file+" does not exist!");
        recorder.restore(IO.toReader(file));
        StringWriter stringWriter=new StringWriter();
        recorder.save(stringWriter);
        String sgf=stringWriter.toString();
        System.out.println("sending sgf: "+sgf);
        if(true) sgf=HexAscii.encode(sgf.getBytes());
        String fromCommand=Command.tgo_receive_sgf.name()+" "+sgf;
        Response response=game.blackFixture.frontEnd.sendAndReceive(fromCommand);
        if(!response.isOk()) Logging.mainLogger.warning(Command.tgo_receive_sgf+" fails!");
        System.out.println("sent sgf to black: "+sgf);
        response=game.whiteFixture.frontEnd.sendAndReceive(fromCommand);
        if(!response.isOk()) Logging.mainLogger.warning(Command.tgo_receive_sgf+" fails!");
        System.out.println("sent sgf to white: "+sgf);
        // go to the end of the main line!
        String bottomCommand=Command.tgo_bottom.name();
        response=game.recorderFixture.frontEnd.sendAndReceive(bottomCommand);
        if(!response.isOk()) Logging.mainLogger.warning(bottomCommand+" fails!");
        response=game.blackFixture.frontEnd.sendAndReceive(bottomCommand);
        if(!response.isOk()) Logging.mainLogger.warning(bottomCommand+" fails!");
        response=game.whiteFixture.frontEnd.sendAndReceive(bottomCommand);
        if(!response.isOk()) Logging.mainLogger.warning(bottomCommand+" fails!");
        // how do we let one person drive this?
    }
    public static void run(int port) throws Exception {
        GameFixture game=Game.setUpStandaloneLocalGame(port);
        game.startPlayerBackends(); // assuming they are local
        if(game.doInit) { // turning this on made stuff work?
            Response initializeResponse=game.initializeGame();
            if(!initializeResponse.isOk()) Logging.mainLogger.warning("initialize game is not ok!");
        }
        game.startGame();
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
