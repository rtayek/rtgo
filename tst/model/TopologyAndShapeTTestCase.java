package model;
import io.Logging;
import static org.junit.Assert.*;
import org.junit.*;
import equipment.*;
import equipment.Board.*;
import utilities.MyTestWatcher;
public class TopologyAndShapeTTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Ignore @Test public void testFactory() {
        // this is failing. looks like board does not
        // put the holes in!
        // maybe because we turned off the add node switch in save?
        // no, fails either way.
        // perhaps we should not be doing this (i.e. let the sgf do it)
        assertEquals(Stone.edge,board.at(board.center()));
    }
    private void saveAndRestore(Model model,Stone center) {
        Logging.mainLogger.info(String.valueOf(model.board().at(model.board().center())));
        Logging.mainLogger.info("topology: "+model.boardTopology());
        Logging.mainLogger.info("shape: "+model.boardShape());
        //model.up(); // getting: restored root: ;(5)RT[Tgo root]
        final String expected=ModelTestIo.save(model);
        Model m=new Model();
        ModelTestIo.restore(m,expected);
        m.ensureBoard();
        m.down(0); // need to execute the sgf
        assertNotNull(m.board());
        final String actual=ModelTestIo.save(m);
        assertEquals(expected,actual);
    }
    @Test public void testsetRoot() {
        Model model=new Model();
        Topology expectedTopology=Topology.normal;
        Shape expectedShape=Shape.normal;
        model.setRoot(n,n,expectedTopology,expectedShape);
        assertEquals(expectedTopology,model.boardTopology());
        assertEquals(expectedShape,model.boardShape());
        Board b=model.board();
        if(b==null) { // this creat may not wor real well
            // board topology may not be set yet.
            b=Board.factory.create(n,n,model.boardTopology(),model.boardShape());
            model.setBoard(b); // 1/25/23 experiment
        }
        assertEquals(Stone.vacant,b.at(b.center()));
        model.move(Move2.blackMoveAtA1);
        // failing because we added a new root in save
        saveAndRestore(model,Stone.vacant);
    }
    @Test public void testsetRootWithHole1() {
        Model model=new Model();
        Topology expectedTopology=Topology.normal;
        Shape expectedShape=Shape.hole1;
        model.setRoot(n,n,expectedTopology,expectedShape);
        assertEquals(expectedTopology,model.boardTopology());
        assertEquals(expectedShape,model.boardShape());
        Board b=model.board();
        if(b==null) Logging.mainLogger.info("b: "+b);
        assertEquals(Stone.edge,b.at(b.center()));
        model.move(Move2.blackMoveAtA1);
        saveAndRestore(model,Stone.edge);
    }
    @Test public void testsetRootWithTorus() {
        Model model=new Model();
        Topology expectedTopology=Topology.torus;
        Shape expectedShape=Shape.normal;
        model.setRoot(n,n,expectedTopology,expectedShape);
        assertEquals(expectedTopology,model.boardTopology());
        assertEquals(expectedShape,model.boardShape());
        model.ensureBoard();
        Board board=model.board();
        assertEquals(Stone.vacant,board.at(board.center()));
        model.move(Move2.blackMoveAtA1);
        saveAndRestore(model,Stone.edge);
    }
    @Test public void testsetRootWithHoleAndTorus() {
        Model model=new Model();
        Topology expectedTopology=Topology.torus;
        Shape expectedShape=Shape.hole1;
        model.setRoot(n,n,expectedTopology,expectedShape);
        assertEquals(expectedTopology,model.boardTopology());
        assertEquals(expectedShape,model.boardShape());
        Board b=model.board();
        if(b==null) Logging.mainLogger.info("b: "+b);
        assertEquals(Stone.edge,b.at(b.center()));
        model.move(Move2.blackMoveAtA1);
        saveAndRestore(model,Stone.edge);
    }
    final int n=19;
    final Board board=Board.factory.create(n,n,Topology.normal,Shape.hole1);
}
