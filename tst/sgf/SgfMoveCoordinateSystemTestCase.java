package sgf;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import utilities.TestKeys;
public class SgfMoveCoordinateSystemTestCase extends AbstractSgfFixtureTestCase {
    @Override protected Object defaultKey() {
        return TestKeys.manyFacesTwoMovesAtA1AndR16OnA9by9Board;
    }
    private SgfNode restoreExample() {
        return restoreExpectedSgf();
    }
    @Test public void testTwoMoves() {
        SgfNode games=restoreExample();
        SgfAcceptor acceptor=new SgfNoOpAcceptor();
        SgfTestSupport.traverse(acceptor,games);
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
        SgfNode games=restoreExample();
        SgfMovesPath acceptor=new SgfMovesPath();
        SgfTestSupport.traverse(acceptor,games);
        SgfNode move1=games.left;
        SgfNode move2=games.left.left;
        //Logging.mainLogger.info(games);
        //Logging.mainLogger.info(move1);
        //Logging.mainLogger.info(move2);
        //Logging.mainLogger.info(acceptor.moves);
        assertEquals(2,acceptor.moves.size());
    }
}
