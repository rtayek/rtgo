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
        System.out.println(actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
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
        assertTrue(key.toString(),ok);
        String actualSgf=stringWriter.toString();
        actualSgf=SgfNode.options.removeUnwanted(actualSgf);
        //Utilities.printDifferences(System.out,expectedSgf,actualSgf);
        System.out.println("ex: "+expectedSgf);
        System.out.println("ac0: "+actual);
        System.out.println("ac: "+actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testLongRoundTrip() throws Exception {
        StringWriter stringWriter=new StringWriter();
        MNode games=Model.modelRoundTrip(expectedSgf!=null?new StringReader(expectedSgf):null,stringWriter);
        String actualSgf=expectedSgf!=null?stringWriter.toString():null;
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testModelRestoreAndSave1() throws Exception {
        MNode root=MNode.restore(new StringReader(expectedSgf));
        Model model=new Model();
        model.setRoot(root);
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(key.toString(),ok);
        String actual=stringWriter.toString();
        actual=actual.replace("\n",""); // who is putting the line feed in?
        if(!expectedSgf.equals(actual)); //printDifferences(expected,actual);
        assertEquals(key.toString(),expectedSgf,actual);
    }
    @Test public void testLongRoundTrip21() throws Exception {
        StringWriter stringWriter=new StringWriter();
        @SuppressWarnings("unused") MNode games=Model.modelRoundTrip2(expectedSgf,stringWriter);
        String actualSgf=stringWriter.toString();
        if(expectedSgf==null) actualSgf=null; // hack for now
        System.out.println("ex: "+expectedSgf);
        System.out.println("ac: "+actualSgf);
        actualSgf=SgfNode.options.prepareSgf(actualSgf); // actual has line feeds now
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testCannonicalRoundTripTwice() {
        // belongs in round trip hierarchy
        try {
            Model model=new Model();
            //System.out.println("or:\n"+originalSgf);
            model.restore(new StringReader(expectedSgf));
            //System.out.println("restored");
            String expectedSgf2=model.save();
            //System.out.println("ex:\n"+expectedSgf);
            model=new Model();
            model.restore(new StringReader(expectedSgf2));
            String actualSgf=model.save();
            //System.out.println("ac:\n"+actualSgf);
            assertEquals(key.toString(),expectedSgf2,actualSgf);
        } catch(Exception e) {
            fail("'"+key+"' caught: "+e);
        }
    }
}