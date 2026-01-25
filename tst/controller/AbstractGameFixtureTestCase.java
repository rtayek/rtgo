package controller;
import static io.Init.first;
import static org.junit.Assert.*;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite.SuiteClasses;
import equipment.Stone;
import io.*;
import io.IOs.End.Holder;
import model.*;
import utilities.*;
public abstract class AbstractGameFixtureTestCase extends ControllerHolderTestSupport {
    // no threads are started by this class except for recorder thread
    // maybe this belongs in some both test case?
    // maybe
    // maybe these being called from a real game can causes hang problems?
    // maybe, but we are not calling these from a running game.
    // looks like no threads are started by this setup except for the recorder thread.
    // these tests just call the model. no use of pipe or socket for gtp
    // they only makes moves on one board!
    // but they do use the game fixture.
    // yes, some of them just make a move and test that it got made.
    // so maybe they belong in the next level up
    // these are all failing now when called from subclasses?
    // it looks that way, we have broken them again.
    // 7/2/22 this test case id weird. fix it!
    public static class DuplexTestCase extends AbstractGameFixtureTestCase {
        @RunWith(Parameterized.class) public static class ParameterizedTestCase extends DuplexTestCase {
            public ParameterizedTestCase(int i) { this.i=i; }
            @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
            final int i;
            static final int n=2;
        }
        @Override protected Holder createHolder() throws Exception { return Holder.duplex(); }
    }
    public static class SocketTestCase extends AbstractGameFixtureTestCase {
        @RunWith(Parameterized.class) public static class ParameterizedTestCase extends SocketTestCase {
            public ParameterizedTestCase(int i) { this.i=i; }
            @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
            final int i;
            static final int n=2;
        }
        @Override protected Holder createHolder() throws Exception { return Holder.trick(IOs.anyPort); }
    }
    @RunWith(Suite.class) @SuiteClasses({DuplexTestCase.class,SocketTestCase.class}) public static class GameFixtureTestSuite {
    }
    @RunWith(Suite.class) @SuiteClasses({DuplexTestCase.ParameterizedTestCase.class,
            SocketTestCase.ParameterizedTestCase.class}) public static class ParameterizedTestSuite {
        // also should have been found by grep - add my test watcher !
    }
    @BeforeClass public static void setUpClass() { Logging.mainLogger.info("static start: "+staticEt); }
    static void printHistograms() {
        Logging.mainLogger.info("static end "+staticEt);
        Logging.mainLogger.info(" game: "+gameFixture);
        Logging.mainLogger.info("setup: "+setUp);
        Logging.mainLogger.info(" down: "+tearDown);
        Logging.mainLogger.info("total: "+tearDown);
    }
    @AfterClass public static void tearDownClass() {
        //printHistograms();
    }
    @Override protected void onHolderCreated(Holder holder) {
        blackHolder = holder;
    }

    @Before public void setUp() throws Exception {
        // recorder thread is started.
        // set up just one both fixture for black.
        Et et=new Et();
        game=new GameFixture(new Model("recorder"));
        gameFixture.add(et.etms());
        assertTrue("game fixture",game!=null);
        assertTrue("game thread",game.namedThread==null);
        // game is created, but never started at this level.
        // move to instance variable
        assertNotNull(blackHolder.back);
        game.blackFixture.setupBoth(blackHolder,game.blackName(),new Model(game.blackName()));
        assertNotNull(game.blackFixture.backEnd);
        // just sets up black.
        // no threads are started (yet)
        setUp.add(et.etms());
        assertTrue(game.namedThread==null);
    }
    @After public void tearDown() throws Exception {
        Et et=new Et();
        if(game!=null) game.stop();
        tearDown.add(et.etms());
        total.add(this.et.etms());
    }
    @Test() public void testSetupAndTeardown() throws Exception {}
    @Test() public void testResign() throws Exception {
        playMovesAlternating(Move2.blackResign.name());
    }
    @Test() public void testPass() throws Exception {
        playMovesAlternating(Move2.blackPass.name());
    }
    @Test() public void testA1() throws Exception {
        playMovesAlternating("A1");
    }
    @Test() public void testA1A2() throws Exception {
        playMovesAlternating("A1","A2");
    }
    @Test() public void testA1A2A3() throws Exception {
        playMovesAlternating("A1","A2","A3");
    }
    public static void main(String[] args) {
        Logging.mainLogger.info(String.valueOf(Init.first));
        first.suiteControls=true;
        JUnitCore jUnitCore=new JUnitCore();
        jUnitCore.run(AbstractGameFixtureTestCase.ParameterizedTestSuite.class);
        Logging.mainLogger.info("exit main");
    }
    private void playMovesAlternating(String... moves) throws Exception {
        Model model=game.blackFixture.backEnd.model;
        Stone stone=Stone.black;
        for(String move:moves) {
            playOneMove(model,stone,move);
            stone=stone.otherColor();
        }
    }
    private void playOneMove(Model model,Stone stone,String move) throws Exception {
        int moves=model.moves();
        model.playOneMove(stone,move);
        model.waitForMoveCompleteOnBoard(moves);
        assertEquals(move,model.lastMoveGTP());
    }
    static Histogram gameFixture=new Histogram(10,0,100);
    static Histogram setUp=new Histogram(10,0,100);
    static Histogram tearDown=new Histogram(10,0,100);
    static Histogram total=new Histogram(10,0,100);
    Holder blackHolder;
    public GameFixture game;
    public int serverPort;
    public int width,depth;
    final Et et=new Et();
    static final int timeout=0;;
    static final Et staticEt=new Et();
}

