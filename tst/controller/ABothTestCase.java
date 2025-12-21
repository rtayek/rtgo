package controller;
import static io.Init.first;
import static org.junit.Assert.assertTrue;
import org.junit.*;
import equipment.Board;
import io.IOs.End.Holder;
import io.Logging;
import utilities.MyTestWatcher;
public class ABothTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());;
    @Before public void setUp() throws Exception {
        both=new BothEnds();
        Holder holder=Holder.duplex();
        both.setupBoth(holder,"black",null); // "test" was bad name!
    }
    @After public void tearDown() throws Exception {
        Logging.mainLogger.info("enter tearDown() "+watcher.ets());
        both.stop();
    }
    @Test() public void testBothNamee() throws Exception {
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("6.11 can not run backend!");
        }
        both.frontEnd.sendString(Command.name.toString());
        Response response=both.frontEnd.receive();
        assertTrue(response.isOk());
    }
    @Test(timeout=100) public void testBothGenmoveTrue() throws Exception {
        // try to make the false version
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 6 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("6 can not run backend!");
        }
        Logging.mainLogger.info("gtp thread started at "+first.et);
        both.backEnd.setGenerateMove(true);
        boolean sendBoardSize=false;
        if(sendBoardSize) {
            both.frontEnd.sendString(Command.boardsize.name()+" "+Board.standard);
            Logging.mainLogger.info("before first receive "+first.et);
            Response response=both.frontEnd.receive();
            assertTrue(response.isOk());
            Logging.mainLogger.info("response is ok "+first.et);
        }
        both.frontEnd.sendString(Command.genmove.name()+" black");
        //both.frontEnd.out.close(); // lets try this
        Logging.mainLogger.info("wait "+first.et);
        boolean waitUntilItIsTmeToMove=false; // hangs as expected when true
        // 4 cases. gebmove and wait.
        if(waitUntilItIsTmeToMove) both.backEnd.waitUntilItIsTmeToMove();
        Logging.mainLogger.info("end of wait");
        Response response2=both.frontEnd.receive();
        assertTrue(response2.isOk());
        Logging.mainLogger.fine("response2: "+response2);
        assertTrue(response2.response.startsWith("A1"));
    }
    BothEnds both;
    static {
        System.out.println("static init a both test case.");
        System.out.println(first);
    }
    static final int timeout=0; // maybe get rid of this eventually?
}
