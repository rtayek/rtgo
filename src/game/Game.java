package game;
import java.io.*;
import controller.*;
import io.*;
import io.IOs;
import com.tayek.util.io.End.Holders;
import model.Model;
import model.ModelIo;
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
        Holders holders=Holders.holders(IOs.anyPort);
        GameFixture game=new GameFixture(recorder);
        game.setupFrontEnds(holders.first.front,holders.second.front);
        game.blackFixture.setupBackEnd(holders.first.back,game.blackName(),game.id);
        game.whiteFixture.setupBackEnd(holders.second.back,game.whiteName(),game.id);
        return game;
    }
    public static void loadExistinGame(File file,Model recorder,GameFixture game) {
        // no references to any back end stuff here.
        if(game.namedThread!=null) throw new RuntimeException("game already started!");
        if(!file.exists()) Logging.mainLogger.warning(file+" does not exist!");
        ModelIo.restore(recorder,file);
        String sgf=recorder.save();
        sgf=HexAscii.encode(sgf.getBytes());
        String receiveCommand=Command.tgo_receive_sgf.name()+" "+sgf;
        Response response=game.recorderFixture.frontEnd.sendAndReceive(receiveCommand);
        if(!response.isOk()) Logging.mainLogger.warning(Command.tgo_receive_sgf+" fails!");
        response=game.blackFixture.frontEnd.sendAndReceive(receiveCommand);
        if(!response.isOk()) Logging.mainLogger.warning(Command.tgo_receive_sgf+" fails!");
        response=game.whiteFixture.frontEnd.sendAndReceive(receiveCommand);
        if(!response.isOk()) Logging.mainLogger.warning(Command.tgo_receive_sgf+" fails!");
        game.bottom();
    }
    public static void run(int port) throws Exception {
        GameFixture game=Game.setUpStandaloneLocalGame(port);
        game.startGameThread();
        GameFixture.playSillyGame(game,100);
        game.stop();
        //GTPBackEnd.sleep2(GTPBackEnd.yield);
        NamedThreadGroup.printThraedsAtEnd();
    }
    public static void main(String[] args) throws Exception {
        Logging.mainLogger.info(String.valueOf(Init.first));
        for(int i=0;i<1;++i) { //
            for(int port:IOs.ports) try {
                run(port);
            } catch(Exception e) {
                e.printStackTrace();
                if(port==IOs.defaultPort) Logging.mainLogger.info("default port, probably ok.");
            }
        }
        NamedThreadGroup.printThraedsAtEnd();
    }
}
