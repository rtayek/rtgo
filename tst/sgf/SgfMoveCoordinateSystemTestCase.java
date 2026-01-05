package sgf;
import io.Logging;
import static org.junit.Assert.assertEquals;
import static sgf.Parser.*;
import org.junit.*;
import utilities.MyTestWatcher;
public class SgfMoveCoordinateSystemTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testTwoMoves() {
        String sgfString=getSgfData("manyFacesTwoMovesAtA1AndR16OnA9by9Board");
        SgfNode games=SgfTestIo.restore(sgfString);
        SgfAcceptor acceptor=new SgfNoOpAcceptor();
        Traverser traverser=new Traverser(acceptor);
        traverser.visit(games);
        SgfNode move1=games.left;
        SgfNode move2=games.left.left;
        // a1 maps to as
        // r16 maps to qd
        SgfProperty property1=move1.sgfProperties.get(0);
        //Logging.mainLogger.info("p="+property1.p());
        String m1=property1.list().get(0);
        //Logging.mainLogger.info("m1="+m1);
        SgfProperty property2=move2.sgfProperties.get(0);
        String m2=property2.list().get(0);
        //Logging.mainLogger.info("m2="+m2);
        // no testing done, just finding out what the coordinate map is.
    }
    @Test public void testTwoMovesPath() {
        String sgfString=getSgfData("manyFacesTwoMovesAtA1AndR16OnA9by9Board");
        SgfNode games=SgfTestIo.restore(sgfString);
        SgfMovesPath acceptor=new SgfMovesPath();
        Traverser traverser=new Traverser(acceptor);
        traverser.visit(games);
        SgfNode move1=games.left;
        SgfNode move2=games.left.left;
        //Logging.mainLogger.info(games);
        //Logging.mainLogger.info(move1);
        //Logging.mainLogger.info(move2);
        //Logging.mainLogger.info(acceptor.moves);
        assertEquals(2,acceptor.moves.size());
    }
}
