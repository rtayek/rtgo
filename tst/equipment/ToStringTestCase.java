package equipment;
import org.junit.*;
import equipment.Board.Topology;
import io.Logging;
import utilities.MyTestWatcher;
public class ToStringTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testToStringImpl() {
        // no tests here. add some!
        int width=3,depth=3;
        for(;width<5;width++) {
            Logging.mainLogger.info(width+" "+depth);
            BoardImpl boardImpl=new BoardImpl(width,depth,Topology.normal,0);
            boardImpl.setAt(0,Stone.black);
            boardImpl.setAt(width/2,depth/2,Stone.edge);
            boardImpl.setAt(width-1,depth-1,Stone.white);
            Logging.mainLogger.info(""+boardImpl);
        }
    }
}
