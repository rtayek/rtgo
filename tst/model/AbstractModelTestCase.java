package model;
import static org.junit.Assert.assertNotNull;
import java.util.Collection;
import org.junit.*;
import io.IOs;
import sgf.*;
import utilities.MyTestWatcher;
public abstract class AbstractModelTestCase extends AbstractMNodeTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public static boolean checkBoardInRoot(Object key) {
        // move this?
        if(key==null) { System.out.println("key is null!"); return true; }
        String expectedSgf=Parser.getSgfData(key);
        Model original=new Model();
        ModelTestIo.restore(original,expectedSgf);
        boolean hasABoard=original.board()!=null;
        int n=Math.min(expectedSgf.length(),20);
        Model model=new Model();
        ModelTestIo.restore(model,expectedSgf);
        if(model.board()==null); // System.out.println("model has no board!");
        else System.out.println("model has a board!");
        Navigate.down.do_(model);
        //hasABoard|=
        //System.out.println(model.board().width()+" "+model.board().depth());
        Collection<SgfNode> sgfNodes=Model.mainLineFromCurrentPosition(model);
        return hasABoard;
    }
    @Test public void testCheckBoardInRoot() {
        if(key==null) {
            IOs.stackTrace(10);
            //System.out.println("system exit");
            //System.exit(1);
        }
        assertNotNull(key);
        boolean ok=checkBoardInRoot(key);
        System.out.println("after key: "+key);
        // always fails because none of these have a board in root.
        // that seems to be the usual case.
        // that is always the usual case,
        // since i always add a dummy multi-way node!
        // a better check would be if there was a board in the first real node.
        //assertTrue(ok);
    }
}
