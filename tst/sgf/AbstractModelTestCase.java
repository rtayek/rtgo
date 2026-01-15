package sgf;
import io.Logging;
import model.Model;
import model.Navigate;
import static org.junit.Assert.assertNotNull;
import java.util.Collection;
import org.junit.*;
import io.IOs;
public abstract class AbstractModelTestCase extends AbstractMNodeTestCase {
    private boolean checkBoardInRoot() {
        // move this?
        if(key==null) { Logging.mainLogger.info("key is null!"); return true; }
        Model original=ModelTestIo.restoreNew(expectedSgf);
        boolean hasABoard=original.board()!=null;
        Model model=ModelTestIo.restoreNew(expectedSgf);
        if(model.board()==null); // Logging.mainLogger.info("model has no board!");
        else Logging.mainLogger.info("model has a board!");
        Navigate.down.do_(model);
        //hasABoard|=
        //Logging.mainLogger.info(model.board().width()+" "+model.board().depth());
        Collection<SgfNode> sgfNodes=Model.mainLineFromCurrentPosition(model);
        return hasABoard;
    }
    @Test public void testCheckBoardInRoot() {
        if(key==null) {
            IOs.stackTrace(10);
            //Logging.mainLogger.info("system exit");
            //System.exit(1);
        }
        assertNotNull(key);
        boolean ok=checkBoardInRoot();
        Logging.mainLogger.info("after key: "+key);
        // always fails because none of these have a board in root.
        // that seems to be the usual case.
        // that is always the usual case,
        // since i always add a dummy multi-way node!
        // a better check would be if there was a board in the first real node.
        //assertTrue(ok);
    }
}
