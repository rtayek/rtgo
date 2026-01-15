package sgf;
import io.Logging;
import model.Model;
import model.ModelHelper;
import model.ModelHelper.ModelSaveMode;
import static org.junit.Assert.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractModelRoundtripTestCase extends AbstractMNodeRoundTripTestCase {
    private void assertModelRestoreAndSave(Model model) {
        String actualSgf=ModelTestIo.restoreAndSave(model,expectedSgf);
        assertPreparedRoundTrip(actualSgf);
    }
    private void assertModelRoundTripToString(ModelHelper.ModelSaveMode saveMode) {
        String actualSgf=ModelTestIo.modelRoundTripToString(expectedSgf,saveMode);
        assertPreparedRoundTrip(actualSgf);
    }
    private void assertSgfRestoreAndSave() {
        String actualSgf=SgfTestSupport.restoreAndSave(expectedSgf);
        assertPreparedRoundTrip(actualSgf);
    }
    @Test public void testModelRT0() throws Exception {
        Model model=new Model("",true);
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        assertModelRestoreAndSave(model);
    }
    @Test public void testModelRT0NewWay() throws Exception {
        Model model=new Model("",false);
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        assertModelRestoreAndSave(model);
    }
    @Test public void testModelRestoreAndSave() throws Exception {
        // then it does a restore and a save.
        // this one looks more complicated than is necessary.
        //the round trip above could be package if we removed it.
        assertSgfRestoreAndSave();
        // failing probably due to add new root problem
        Model model=new Model();
        Logging.mainLogger.info("ex: "+expectedSgf);
        String actualSgf2=ModelTestIo.restoreAndSave(model,expectedSgf,key.toString());
        actualSgf2=SgfNode.options.removeUnwanted(actualSgf2);
        assertPreparedRoundTrip(actualSgf2);
    }
    @Test public void testLongRoundTrip() throws Exception {
        assertModelRoundTripToString(ModelHelper.ModelSaveMode.sgfNode);
    }
    @Test public void testModelRestoreAndSave1() throws Exception {
        MNode root=SgfTestIo.restoreMNode(expectedSgf);
        Model model=new Model();
        model.setRoot(root);
        String actualSgf=ModelTestIo.save(model,key.toString());
        if(!expectedSgf.equals(actualSgf)); //printDifferences(expected,actual);
        assertPreparedRoundTrip(actualSgf);
    }
    @Test public void testLongRoundTrip21() throws Exception {
        String actualSgf=ModelTestIo.modelRoundTripToString(expectedSgf,ModelHelper.ModelSaveMode.sgfNodeChecked);
        Logging.mainLogger.info("ex: "+expectedSgf);
        Logging.mainLogger.info("ac: "+actualSgf);
        assertPreparedRoundTrip(actualSgf);
    }
    @Test public void testCannonicalRoundTripTwice() {
        assertNoLineFeeds(expectedSgf);
        try {
            Model model=new Model();
            ModelTestIo.restore(model,expectedSgf);
            String expectedSgf2=model.save();
            expectedSgf2=prepareActual(expectedSgf2);
            model=new Model();
            ModelTestIo.restore(model,expectedSgf2);
            String actualSgf=model.save();
            actualSgf=prepareActual(actualSgf);
            // fails with (;RT[Tgo root];FF[4]GM[1]AP[RTGO]C[comment];B[as])
            assertEquals(key.toString(),expectedSgf2,actualSgf);
        } catch(Exception e) {
            fail("'"+key+"' caught: "+e);
        }
    }
}
