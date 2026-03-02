package model;
import utilities.MyTestWatcher;
import static org.junit.Assert.*;
import java.io.*;
import java.util.Random;
import com.tayek.util.io.FileIO;
import org.junit.*;
import equipment.*;
import equipment.Board.Shape;
import equipment.Board.Topology;
import model.Model.MoveResult;
import model.Move2.MoveType;
public class ColorBugTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception { ModelTreeOps.setRoot(model,Board.standard,Board.standard,Topology.normal,Shape.normal); }
    void randomMove2() {
        int width=model.board().width();
        int depth=model.board().depth();
        Point point;
        do {
            point=new Point(random.nextInt(width),random.nextInt(depth));
            Move2 move=new Move2(MoveType.move,model.turn(),point);
            if(model.addMoveNodeAndExecute(move)==MoveResult.legal) break;
        } while(model.board().at(point).equals(Stone.vacant));
        // what should we test for here?
    }
    void randomStep() {
        Navigate navigate=Navigate.values()[random.nextInt(directions)];
        for(;navigate.canDo(model);navigate=Navigate.values()[random.nextInt(directions)]) navigate.do_(model);
        // this looks broken. may not do anything
    }
    @Ignore @Test public void testRandom() throws IOException {
        for(int i=0;i<10;i++) {
            model.generateRandomMove();
            randomStep();
        }
        File temporaryFile=File.createTempFile("tgo-","sgf");
        temporaryFile.deleteOnExit();
        boolean ok=ModelIo.saveModel(model,FileIO.toWriter(temporaryFile));
        assertTrue(ok);
        Model actual=new Model();
        ModelIo.restoreModel(model,FileIO.toReader(""));
        fail("nyi");
        // go to the right move
        // how to test this?
        // maybe restore and compare boards?
        // yes, or use model.listMoves();
        // or round trip the files? maybe not, could be big files like kogo's?
    }
    @Test public void test1() {
        ModelTreeOps.setRoot(model,3,3,Topology.normal,Shape.normal);
        model.ensureBoard();
        model.moveAndPlaySound(model.turn(),"A1",model.board().width());
        Navigate.up.do_(model);
        model.moveAndPlaySound(model.turn(),"A2",model.board().width());
        Navigate.up.do_(model);
        Navigate.down.do_(model);
    }
    @Test public void testUpAndDown() {
        ModelTreeOps.setRoot(model,3,3,Topology.normal,Shape.normal);
        model.ensureBoard();
        model.moveAndPlaySound(model.turn(),"A1",model.board().width());
        Navigate.up.do_(model);
        Navigate.down.do_(model);
    }
    Model model=new Model();
    Random random=new Random();
    final int directions=Navigate.values().length;
}



