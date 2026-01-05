package model;
import static org.junit.Assert.*;
import org.junit.*;
import sgf.*;
import utilities.MyTestWatcher;
public abstract class AbstractModelRoundtripTestCase extends AbstractMNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testModelRT0() throws Exception {
        Model model=new Model("",true);
        ModelTestIo.restore(model,expectedSgf);
        String actualSgf=ModelTestIo.save(model);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testModelRT0NewWay() throws Exception {
        Model model=new Model("",false);
        ModelTestIo.restore(model,expectedSgf);
        String actualSgf=ModelTestIo.save(model);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testModelRestoreAndSave() throws Exception {
        String actualSgf=SgfRoundTrip.restoreAndSave(expectedSgf);
        // then it does a restore and a save.
        // this one looks more complicated than is necessary.
        //the round trip above could be package if we removed it.
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
        // failing probably due to add new root problem
        Model model=new Model();
        System.out.println("ex: "+expectedSgf);
        ModelTestIo.restore(model,expectedSgf);
        String actualSgf2=ModelTestIo.save(model,key.toString());
        actualSgf2=SgfNode.options.removeUnwanted(actualSgf2);
        //Utilities.printDifferences(System.out,expectedSgf,actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf2);
    }
    @Test public void testLongRoundTrip() throws Exception {
        String actualSgf=ModelTestIo.modelRoundTripToString(expectedSgf,ModelHelper.ModelSaveMode.sgfNode);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testModelRestoreAndSave1() throws Exception {
        MNode root=SgfTestIo.restoreMNode(expectedSgf);
        Model model=new Model();
        model.setRoot(root);
        String actualSgf=ModelTestIo.save(model,key.toString());
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        if(!expectedSgf.equals(actualSgf)); //printDifferences(expected,actual);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testLongRoundTrip21() throws Exception {
        String actualSgf=ModelTestIo.modelRoundTripToString(expectedSgf,ModelHelper.ModelSaveMode.sgfNodeChecked);
        System.out.println("ex: "+expectedSgf);
        System.out.println("ac: "+actualSgf);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testCannonicalRoundTripTwice() {
        assertFalse(expectedSgf.contains("\n"));
        try {
            Model model=new Model();
            ModelTestIo.restore(model,expectedSgf);
            String expectedSgf2=model.save();
            if(expectedSgf2!=null) expectedSgf2=SgfNode.options.prepareSgf(expectedSgf2);
            model=new Model();
            ModelTestIo.restore(model,expectedSgf2);
            String actualSgf=model.save();
            if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
            // fails with (;RT[Tgo root];FF[4]GM[1]AP[RTGO]C[comment];B[as])
            assertEquals(key.toString(),expectedSgf2,actualSgf);
        } catch(Exception e) {
            fail("'"+key+"' caught: "+e);
        }
    }
}
