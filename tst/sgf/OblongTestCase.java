package sgf;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import equipment.Board;
import model.Model;
public class OblongTestCase extends AbstractWatchedTestCase {
    @Before public void setUp() throws Exception {
        File file=new File("ogs/lecoblong.sgf");
        expectedSgf=SgfTestSupport.loadExpectedSgf(file);
    }
    @Test public void test() {
        MNode games=SgfTestIo.restoreMNode(expectedSgf);
        //Logging.mainLogger.info("root: "+games);
        //Logging.mainLogger.info("children: "+games.children);
        model=new Model("oblong");
        model.setRoot(games); // does this really trash everything correctly?
        Board board=model.board();
        //if(board!=null) Logging.mainLogger.info("board is "+board.width()+"x"+board.depth());
        //else Logging.mainLogger.info("board is null");
        //Logging.mainLogger.info("before down");
        model.down(0);
        //Logging.mainLogger.info("after down");
        board=model.board();
        //if(board!=null) Logging.mainLogger.info("board is "+board.width()+"x"+board.depth());
        //else Logging.mainLogger.info("board is null");
    }
    String expectedSgf;
    Model model;
}
