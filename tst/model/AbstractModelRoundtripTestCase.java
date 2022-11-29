package model;
import static org.junit.Assert.*;
import static sgf.SgfNode.sgfRoundTrip;
import java.io.*;
import org.junit.*;
import sgf.*;
import utilities.MyTestWatcher;
public abstract class AbstractModelRoundtripTestCase extends AbstractMNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testModelRT0() throws Exception {
        Model model=new Model();
        model.restore(new StringReader(expectedSgf));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String actualSgf=stringWriter.toString();
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testModelRestoreAndSave() throws Exception {
        String actualSgf=sgfRoundTrip(expectedSgf);
        // then it does a restore and a save.
        // this one looks more complicated than is necessary.
        //the round trip above could be package if we removed it.
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
        // failing probably due to add new root problem
        Model model=new Model();
        System.out.println("ex: "+expectedSgf);
        model.restore(new StringReader(expectedSgf));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(key.toString(),ok);
        String actualSgf2=stringWriter.toString();
        actualSgf2=SgfNode.options.removeUnwanted(actualSgf2);
        //Utilities.printDifferences(System.out,expectedSgf,actualSgf);
        //System.out.println("ex: "+expectedSgf);
        //System.out.println("ac0: "+actualSgf);
        //System.out.println("ac: "+actualSgf2);
        assertEquals(key.toString(),expectedSgf,actualSgf2);
    }
    @Test public void testLongRoundTrip() throws Exception {
        StringWriter stringWriter=new StringWriter();
        MNode games=Model.modelRoundTrip(expectedSgf!=null?new StringReader(expectedSgf):null,stringWriter);
        String actualSgf=expectedSgf!=null?stringWriter.toString():null;
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testModelRestoreAndSave1() throws Exception {
        MNode root=MNode.restore(new StringReader(expectedSgf));
        Model model=new Model();
        model.setRoot(root);
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(key.toString(),ok);
        String actualSgf=stringWriter.toString();
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        if(!expectedSgf.equals(actualSgf)); //printDifferences(expected,actual);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testLongRoundTrip21() throws Exception {
        StringWriter stringWriter=new StringWriter();
        @SuppressWarnings("unused") MNode games=Model.modelRoundTrip2(expectedSgf,stringWriter);
        String actualSgf=stringWriter.toString();
        if(expectedSgf==null) actualSgf=null; // hack for now
        System.out.println("ex: "+expectedSgf);
        System.out.println("ac: "+actualSgf);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testCannonicalRoundTripTwice() {
        assertFalse(expectedSgf.contains("\n"));
        try {
            Model model=new Model();
            model.restore(new StringReader(expectedSgf));
            String expectedSgf2=model.save();
            if(expectedSgf2!=null) expectedSgf2=SgfNode.options.prepareSgf(expectedSgf2);
            model=new Model();
            model.restore(new StringReader(expectedSgf2));
            String actualSgf=model.save();
            if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
            assertEquals(key.toString(),expectedSgf2,actualSgf);
        } catch(Exception e) {
            fail("'"+key+"' caught: "+e);
        }
    }
}