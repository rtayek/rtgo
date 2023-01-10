package controller;
import static io.Init.first;
import static org.junit.Assert.*;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite.SuiteClasses;
import equipment.*;
import io.*;
import io.IO.End.Holder;
import model.Model;
import utilities.*;
public abstract class AbstractBothTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // currently does NOT extend any test case.
    // these setup a front end and a back end (Both).
    // most do start gtp backend threads. maybe not all use/need them.
    // these use the front end to send gtp commands
    // and receive the response.
    public static class DuplexTestCase extends AbstractBothTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());;
        @Override @Before public void setUp() throws Exception { holder=Holder.duplex(); super.setUp(); }
        @Override @After public void tearDown() throws Exception { super.tearDown(); }
    }
    public static class SocketTestCase extends AbstractBothTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Override @Before public void setUp() throws Exception { holder=Holder.trick(IO.anyPort); super.setUp(); }
        @Override @After public void tearDown() throws Exception { super.tearDown(); }
    }
    @RunWith(Suite.class) @SuiteClasses({SocketTestCase.class,
        DuplexTestCase.class,}) public static class BothTestSuite {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @BeforeClass public static void setUpClass() {
            System.out.println(Init.first);
            System.out.println("set up suite class");
            // do not bump wrapup counter
            first.suiteControls=true;
        }
        @AfterClass public static void tearDownClass() {
            System.out.println("tear down suite class");
            // use a map of thread to ?
            first.wrapupTests_();
        }
    }
    @Ignore @RunWith(Parameterized.class) public static class ParameterizedBothTestSuitee extends BothTestSuite {
        // this does not work.
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        public ParameterizedBothTestSuitee(int i) { this.i=i; }
        @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
        final int i;
        static final int n=2;
    }
    @Before public void setUp() throws Exception {
        Logging.mainLogger.info("enter setup "+watcher.ets());
        //NamedThreadGroup.removeTerminated(); // experiment
        // no remove terminated without this!
        // why doesn't the call in my test watcher work!
        //Logging.mainLogger.info("after remove terminated at "+first.et);
        both=new BothEnds();
        Logging.mainLogger.info("after create holder at "+first.et);
        both.setupBoth(holder,"test",null);
    }
    @After public void tearDown() throws Exception {
        Logging.mainLogger.info("enter tearDown() "+watcher.ets());
        both.stop();
        //int n=NamedThreadGroup.printAllNamedThreads("teardown both atc");
    }
    @Test() public void testBothConnected() throws Exception {
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 6.3 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("6.3 can not run backend!");
        }
    }
    @Test() public void testBothConnectedSend() throws Exception {
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 6.5 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("6.5 can not run backend!");
        }
        both.frontEnd.sendString("notACommand");
        Response response=both.frontEnd.receive();
        assertNotNull(response);
        assertTrue(!response.isOk());
    }
    @Test() public void testBothConnectedNameCommand() {
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 7 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("7 can not run backend!");
        }
        //both.backEnd.waitForDone();
        // different code than test connected tgo stop
        // does a send, wait, receive
        Response response=both.frontEnd.sendAndReceive(Command.name.name()+'\n');
        assertTrue(response.isOk());
        assertTrue(response.response.startsWith(Model.sgfApplicationName));
    }
    // make all of these a final variable!
    @Test() public void testBothConnectedGenmoveTrue() throws Exception {
        // there are two if these. refactor!
        Logging.mainLogger.info("enter test at "+first.et);
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 8 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("8 can not run backend!");
        }
        Logging.mainLogger.info("gtp thread started at "+first.et);
        both.backEnd.setGenerateMove(true);
        both.frontEnd.sendString(Command.boardsize.name()+" "+Board.standard);
        Response response=both.frontEnd.receive();
        assertTrue(response.isOk());
        Logging.mainLogger.info("wait "+first.et);
        boolean waitWithGenmoveTrue=false;
        if(waitWithGenmoveTrue) {
            Logging.mainLogger.info(""+both.backEnd.isWaitingForMove());
            both.backEnd.waitUntilItIsTmeToMove();
            Logging.mainLogger.info("end of wait");
        }
        both.frontEnd.sendString(Command.genmove.name()+" black");
        //both.frontEnd.out.close(); // lets try this
        Response response2=both.frontEnd.receive();
        assertTrue(response2.isOk());
        Logging.mainLogger.fine("response2: "+response2);
        assertTrue(response2.response.startsWith("A1"));
    }
    @Test() public void testBothConnectedGenmoveFalse() throws Exception {
        Model model=both.backEnd.model;
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 9 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("9 can not run backend!");
        }
        both.backEnd.model.setRole(Model.Role.anything);
        both.frontEnd.name="testConnectGenmoveAndCheckResponse";
        both.backEnd.setGenerateMove(false);
        both.frontEnd.sendString(Command.boardsize.name()+" "+Board.standard);
        Response response=both.frontEnd.receive();
        assertTrue(response.isOk());
        both.frontEnd.sendString(Command.genmove.name()+" black");
        both.backEnd.waitUntilItIsTmeToMove();
        Point point=new Point(1,1); // just so it's different.
        model.move(model.turn(),point);
        Response response2=both.frontEnd.receive();
        assertTrue(response2.isOk());
        Logging.mainLogger.fine("response2: "+response2);
        assertTrue(response2.response.startsWith("B2"));
    }
    @Test() public void testBothConnectedTgo_stop() throws Exception {
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 10 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("10 can not run backend!");
        }
        both.frontEnd.sendString(Command.tgo_stop.name());
        //both.backEnd.waitForDone(); // not needed?
        Response response=both.frontEnd.receive();
        assertTrue(response.isOk());
        assertTrue(response.response.startsWith("true"));
        // how do we know that it did anything?
    }
    BothEnds both;
    Integer serverPort;
    Holder holder;
    static final int timeout=0;;
}
