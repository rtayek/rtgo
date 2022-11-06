package equipment;
import static org.junit.Assert.assertNotNull;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import io.Logging;
import utilities.MyTestWatcher;
// lets test all the strange cases here
@RunWith(Parameterized.class) public class ParameterizedBoardShapeAndTopologyTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
    }
    @After public void tearDown() throws Exception {}
    public ParameterizedBoardShapeAndTopologyTestCase(Board board) {
        assertNotNull(board);
        this.board=board;
    }
    //static int[] sizes=new int[] {3,9,13,19,21,4,};
    static int[] sizes=new int[] {19};
    public static List<Object[]> standardBoards(Board.Topology type) {
        List<Object[]> boards=new ArrayList<>();
        for(int w:sizes) for(int d:sizes) boards.add(new Object[] {Board.factory.create(w,d,type)});
        return boards;
    }
    public static List<Object[]> boardsWithHoles(Board.Topology type) {
        // don't see any holes here
        // yes, no holes.
        // maybe need shape here?
        List<Object[]> boards=new ArrayList<>();
        for(int n:sizes) { boards.add(new Object[] {Board.factory.create(n,type)}); }
        return boards;
    }
    @Parameters public static Collection<Object[]> data() {
        // see where this is used.
        // maybe a routine that takes any collection and returns a pa?
        List<Object[]> list=new ArrayList<Object[]>();
        for(Board.Topology type:Board.Topology.values()) list.addAll(standardBoards(type));
        for(Board.Topology type:Board.Topology.values()) list.addAll(boardsWithHoles(type));
        Logging.mainLogger.info(list.size()+" boards.");
        return list;
    }
    @Test public void testBoard() { assertNotNull(board); }
    final Board board;
}
