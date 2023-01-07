package game;
import java.io.*;
import controller.*;
import io.*;
import io.IO.End.Holders;
import model.Model;
import server.NamedThreadGroup;
import sgf.HexAscii;
public class Game {
    public static GameFixture setUpStandaloneLocalGame(int port) {
        Holders holders=Holders.holders(port);
        Model recorder=new Model("recorder");
        GameFixture game=new GameFixture(recorder);
        game.setupFrontEnds(holders.first.front,holders.second.front);
        // normally the back ends may be started first?
        game.blackFixture.setupBackEnd(holders.first.back,game.blackName(),game.id);
        game.whiteFixture.setupBackEnd(holders.second.back,game.whiteName(),game.id);
        return game;
    }
    public static GameFixture setupLocalGameForShove(Model recorder) {
        Holders holders=Holders.holders(IO.anyPort);
        GameFixture game=new GameFixture(recorder);
        game.setupFrontEnds(holders.first.front,holders.second.front);
        game.blackFixture.setupBackEnd(holders.first.back,game.blackName(),game.id);
        game.whiteFixture.setupBackEnd(holders.second.back,game.whiteName(),game.id);
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
        // how do we go to a particular position?
        // how do we let one person drive this?
    }
    public static void run(int port) throws Exception {
        GameFixture game=Game.setUpStandaloneLocalGame(port);
        game.startGameThread();
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
