package sgf;
import io.Logging;
import model.Model;
import model.ModelHelper;
import model.ModelHelper.ModelSaveMode;
import static org.junit.Assert.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractModelRoundtripTestCase extends AbstractMNodeRoundTripTestCase {
    @Test public void testModelRT0() throws Exception {
        Model model=new Model("",true);
        String actualSgf=ModelTestIo.restoreAndSave(model,expectedSgf);
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        assertPreparedEquals(prepareActual(actualSgf));
    }
    @Test public void testModelRT0NewWay() throws Exception {
        Model model=new Model("",false);
        String actualSgf=ModelTestIo.restoreAndSave(model,expectedSgf);
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        assertPreparedEquals(prepareActual(actualSgf));
    }
    @Test public void testModelRestoreAndSave() throws Exception {
        String actualSgf=SgfTestIo.restoreAndSave(expectedSgf);
        // then it does a restore and a save.
        // this one looks more complicated than is necessary.
        //the round trip above could be package if we removed it.
        assertPreparedEquals(prepareActual(actualSgf));
        // failing probably due to add new root problem
        Model model=new Model();
        Logging.mainLogger.info("ex: "+expectedSgf);
        String actualSgf2=ModelTestIo.restoreAndSave(model,expectedSgf,key.toString());
        actualSgf2=SgfNode.options.removeUnwanted(actualSgf2);
        assertPreparedEquals(prepareActual(actualSgf2));
    }
    @Test public void testLongRoundTrip() throws Exception {
        String actualSgf=ModelTestIo.modelRoundTripToString(expectedSgf,ModelHelper.ModelSaveMode.sgfNode);
        assertPreparedEquals(prepareActual(actualSgf));
    }
    @Test public void testModelRestoreAndSave1() throws Exception {
        MNode root=SgfTestIo.restoreMNode(expectedSgf);
        Model model=new Model();
        model.setRoot(root);
        String actualSgf=ModelTestIo.save(model,key.toString());
        if(!expectedSgf.equals(actualSgf)); //printDifferences(expected,actual);
        assertPreparedEquals(prepareActual(actualSgf));
    }
    @Test public void testLongRoundTrip21() throws Exception {
        String actualSgf=ModelTestIo.modelRoundTripToString(expectedSgf,ModelHelper.ModelSaveMode.sgfNodeChecked);
        Logging.mainLogger.info("ex: "+expectedSgf);
        Logging.mainLogger.info("ac: "+actualSgf);
        assertPreparedEquals(prepareActual(actualSgf));
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
