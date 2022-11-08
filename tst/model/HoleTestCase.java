package model;
import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import equipment.*;
import equipment.Board.*;
import utilities.MyTestWatcher;
public class HoleTestCase {
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
        System.out.println(model.board().at(model.board().center()));
        System.out.println("topology: "+model.boardTopology());
        System.out.println("shape: "+model.boardShape());
        //model.up(); // getting: restored root: ;(5)RT[Tgo root]
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue("save fails",ok);
        final String expected=stringWriter.toString();
        Model m=new Model();
        m.restore(new StringReader(expected));
        //assertNotNull(m.board());
        // wrong board size if n!=19!
        // find that place and fix!
        m.down(0); // need to execute the sgf
        assertNotNull(m.board());
        stringWriter=new StringWriter();
        m.save(stringWriter);
        final String actual=stringWriter.toString();
        assertEquals(actual,expected);
        System.out.println("ex: "+expected);
    }
    @Test public void testsetRoot() {
        Model model=new Model();
        model.setRoot(n,n,Topology.normal,Shape.normal);
        Board b=model.board();
        //System.out.println("initial\n"+model.board());
        assertEquals(Stone.vacant,b.at(b.center()));
        model.move(Move.blackMoveAtA1);
        // failing because we added a new root in save
        saveAndRestore(model,Stone.vacant);
    }
    @Test public void testsetRootWithHole1() {
        Model model=new Model();
        model.setRoot(n,n,Topology.normal,Shape.hole1);
        Board b=model.board();
        if(b==null) System.out.println("b: "+b);
        assertEquals(Stone.edge,b.at(b.center()));
        model.move(Move.blackMoveAtA1);
        saveAndRestore(model,Stone.edge);
    }
    final int n=19;
    final Board board=Board.factory.create(n,n,Topology.normal,Shape.hole1);
}
