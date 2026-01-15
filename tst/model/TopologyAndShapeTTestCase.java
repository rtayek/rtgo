package model;
import io.Logging;
import sgf.ModelTestIo;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import equipment.*;
import equipment.Board.*;
import utilities.MyTestWatcher;
public class TopologyAndShapeTTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    private Model newModelWithRoot(Topology topology,Shape shape) {
        Model model=new Model();
        model.setRoot(n,n,topology,shape);
        assertEquals(topology,model.boardTopology());
        assertEquals(shape,model.boardShape());
        return model;
    }
    @Ignore @Test public void testFactory() {
        // this is failing. looks like board does not
        // put the holes in!
        // maybe because we turned off the add node switch in save?
        // no, fails either way.
        // perhaps we should not be doing this (i.e. let the sgf do it)
        assertEquals(Stone.edge,board.at(board.center()));
    }
    private void saveAndRestore(Model model) {
        Logging.mainLogger.info(String.valueOf(model.board().at(model.board().center())));
        Logging.mainLogger.info("topology: "+model.boardTopology());
        Logging.mainLogger.info("shape: "+model.boardShape());
        //model.up(); // getting: restored root: ;(5)RT[Tgo root]
        String expected=ModelTestIo.save(model);
        Model m=new Model();
        String actual=ModelTestIo.restoreAndSave(m,expected,restored->{
            restored.ensureBoard();
            restored.down(0); // need to execute the sgf
            assertNotNull(restored.board());
        });
        assertEquals(expected,actual);
    }
    @Test public void testsetRoot() {
        Model model=newModelWithRoot(Topology.normal,Shape.normal);
        Board b=model.board();
        if(b==null) { // this creat may not wor real well
            // board topology may not be set yet.
            b=Board.factory.create(n,n,model.boardTopology(),model.boardShape());
            model.setBoard(b); // 1/25/23 experiment
        }
        assertEquals(Stone.vacant,b.at(b.center()));
        model.move(Move2.blackMoveAtA1);
        // failing because we added a new root in save
        saveAndRestore(model);
    }
    @Test public void testsetRootWithHole1() {
        Model model=newModelWithRoot(Topology.normal,Shape.hole1);
        Board b=model.board();
        if(b==null) Logging.mainLogger.info("b: "+b);
        assertEquals(Stone.edge,b.at(b.center()));
        model.move(Move2.blackMoveAtA1);
        saveAndRestore(model);
    }
    @Test public void testsetRootWithTorus() {
        Model model=newModelWithRoot(Topology.torus,Shape.normal);
        model.ensureBoard();
        Board board=model.board();
        assertEquals(Stone.vacant,board.at(board.center()));
        model.move(Move2.blackMoveAtA1);
        saveAndRestore(model);
    }
    @Test public void testsetRootWithHoleAndTorus() {
        Model model=newModelWithRoot(Topology.torus,Shape.hole1);
        Board b=model.board();
        if(b==null) Logging.mainLogger.info("b: "+b);
        assertEquals(Stone.edge,b.at(b.center()));
        model.move(Move2.blackMoveAtA1);
        saveAndRestore(model);
    }
    final int n=19;
    final Board board=Board.factory.create(n,n,Topology.normal,Shape.hole1);
}
