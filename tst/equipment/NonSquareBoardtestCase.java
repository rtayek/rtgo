package equipment;
import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import equipment.Board.*;
import utilities.MyTestWatcher;
public class NonSquareBoardtestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testBoardImplIntInt() {
        //fail("Not yet implemented");
    }
    @Test public void testBoardImplIntIntTypeInt() {
        //fail("Not yet implemented");
    }
    @Test public void testStones() {
        //fail("Not yet implemented");
    }
    @Test public void testSetAt() {
        for(int y=0;y<b.depth;y++) for(int x=0;x<b.width;x++) {
            b.setAt(x,y,Stone.black);
            assertEquals(Stone.black,b.at(x,y));
        }
    }
    BoardImpl b=new BoardImpl(Board.standard,Board.standard/2,Topology.normal,Shape.normal,0);
}
