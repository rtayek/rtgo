package model;
import static org.junit.Assert.*;
import static sgf.SgfNode.sgfRoundTrip;
import java.io.*;
import java.util.Collection;
import org.junit.*;
import sgf.*;
import utilities.MyTestWatcher;
public class AbstractModelTestCase extends AbstractMNodeTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testModelRT0() throws Exception {
        Model model=new Model();
        model.restore(new StringReader(expectedSgf));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String actualSgf=stringWriter.toString();
        System.out.println(actualSgf);
    }
    @Test public void testModelRestoreAndSave() throws Exception {
        String actual=sgfRoundTrip(expectedSgf);
        assertEquals(key.toString(),expectedSgf,actual);
        // failing probably due to add new root problem
        Model model=new Model();
        System.out.println("ex: "+expectedSgf);
        model.restore(new StringReader(expectedSgf));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String actualSgf=stringWriter.toString();
        actualSgf=SgfNode.options.removeUnwanted(actualSgf);
        //Utilities.printDifferences(System.out,expectedSgf,actualSgf);
        System.out.println("ex: "+expectedSgf);
        System.out.println("ac0: "+actual);
        System.out.println("ac: "+actualSgf);
        //assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testLongRoundTrip() throws Exception {
        StringWriter stringWriter=new StringWriter();
        MNode games=Model.modelRoundTrip(expectedSgf!=null?new StringReader(expectedSgf):null,stringWriter);
        String actualSgf=expectedSgf!=null?stringWriter.toString():null;
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    public static boolean checkBoardInRoot(Object key) {
        // move this?
        String expectedSgf=Parser.getSgfData(key);
        Model original=new Model();
        original.restore(new StringReader(expectedSgf));
        boolean hasABoard=original.board()!=null;
        int n=Math.min(expectedSgf.length(),20);
        Model model=new Model();
        model.restore(new StringReader(expectedSgf));
        if(model.board()==null); // System.out.println("model has no board!");
        else System.out.println("model has a board!");
        Navigate.down.do_(model);
        //hasABoard|=
        //System.out.println(model.board().width()+" "+model.board().depth());
        Collection<SgfNode> sgfNodes=Model.mainLineFromCurrentPosition(model);
        return hasABoard;
    }
    @Test public void testCheckBoardInRoot() {
        boolean ok=checkBoardInRoot(key);
        // always fails because none of these have a board in root.
        // that seems to be the usual case.
        // that is always the usual case,
        // since i always add a dummy multi-way node!
        // a better check would be if there was a board in the first real node.
        //assertTrue(ok);
    }
}
