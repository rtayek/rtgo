package equipment;
import utilities.MyTestWatcher;
import org.junit.*;
import equipment.Board.*;
import io.Logging;
public class ToStringTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testToStringImpl() {
        // no tests here. add some!
        int width=3,depth=3;
        for(;width<5;width++) {
            Logging.mainLogger.info(width+" "+depth);
            BoardImpl boardImpl=new BoardImpl(width,depth,Topology.normal,Shape.normal,0);
            boardImpl.setAt(0,Stone.black);
            boardImpl.setAt(width/2,depth/2,Stone.edge);
            boardImpl.setAt(width-1,depth-1,Stone.white);
            Logging.mainLogger.info(""+boardImpl);
        }
    }
}

