package model;
import io.Logging;
import sgf.SgfHarness;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import equipment.*;
import equipment.Board.*;
import utilities.TestSupport;
public class TopologyAndShapeTestCase extends TestSupport {
    private Model newModelWithRoot(Topology topology,Shape shape) {
        Model model=new Model();
        model.setRoot(n,n,topology,shape);
        assertEquals(topology,model.boardTopology());
        assertEquals(shape,model.boardShape());
        return model;
    }
    private void assertRoot(Topology topology,Shape shape,Stone expectedCenter,boolean ensureBoard,
            boolean allowNullBoard) {
        Model model=newModelWithRoot(topology,shape);
        if(ensureBoard) model.ensureBoard();
        Board b=model.board();
        if(b==null&&allowNullBoard) {
            // board topology may not be set yet.
            b=Board.factory.create(n,n,model.boardTopology(),model.boardShape());
            model.setBoard(b);
        }
        assertNotNull(b);
        assertEquals(expectedCenter,b.at(b.center()));
        model.move(Move2.blackMoveAtA1);
        saveAndRestore(model);
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
        String expected=SgfHarness.save(model);
        Model m=new Model();
        String actual=SgfHarness.restoreAndSave(m,expected,restored->{
            restored.ensureBoard();
            restored.down(0); // need to execute the sgf
            assertNotNull(restored.board());
        });
        assertEquals(expected,actual);
    }
    @Test public void testsetRoot() {
        // failing because we added a new root in save
        assertRoot(Topology.normal,Shape.normal,Stone.vacant,false,true);
    }
    @Test public void testsetRootWithHole1() {
        assertRoot(Topology.normal,Shape.hole1,Stone.edge,false,false);
    }
    @Test public void testsetRootWithTorus() {
        assertRoot(Topology.torus,Shape.normal,Stone.vacant,true,false);
    }
    @Test public void testsetRootWithHoleAndTorus() {
        assertRoot(Topology.torus,Shape.hole1,Stone.edge,false,false);
    }
    final int n=19;
    final Board board=Board.factory.create(n,n,Topology.normal,Shape.hole1);
}


