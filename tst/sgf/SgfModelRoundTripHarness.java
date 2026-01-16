package sgf;

import io.Logging;
import static org.junit.Assert.*;
import model.Model;
import model.ModelHelper.ModelSaveMode;

final class SgfModelRoundTripHarness {
    private SgfModelRoundTripHarness() {}

    static void assertModelRestoreAndSave(Object key,String expectedSgf,boolean oldWay) {
        Model model=new Model("",oldWay);
        assertModelRestoreAndSave(key,expectedSgf,model);
    }

    static void assertModelRestoreAndSave(Object key,String expectedSgf,Model model) {
        String actualSgf=ModelTestIo.restoreAndSave(model,expectedSgf);
        SgfRoundTripHarness.assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRoundTripToString(Object key,String expectedSgf,ModelSaveMode saveMode,boolean log) {
        String actualSgf=ModelTestIo.modelRoundTripToString(expectedSgf,saveMode);
        if(log) {
            Logging.mainLogger.info("ex: "+expectedSgf);
            Logging.mainLogger.info("ac: "+actualSgf);
        }
        SgfRoundTripHarness.assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertSgfRestoreAndSave(Object key,String expectedSgf) {
        String actualSgf=SgfTestSupport.restoreAndSave(expectedSgf);
        SgfRoundTripHarness.assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRestoreAndSaveWithExplicitModel(Object key,String expectedSgf) {
        Model model=new Model();
        Logging.mainLogger.info("ex: "+expectedSgf);
        String actualSgf=ModelTestIo.restoreAndSave(model,expectedSgf,key.toString());
        actualSgf=SgfNode.options.removeUnwanted(actualSgf);
        SgfRoundTripHarness.assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelSaveFromMNode(Object key,String expectedSgf,MNode root) {
        Model model=new Model();
        model.setRoot(root);
        String actualSgf=ModelTestIo.save(model,key.toString());
        SgfRoundTripHarness.assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertCanonicalRoundTripTwice(Object key,String expectedSgf) {
        SgfRoundTripHarness.assertNoLineFeeds(expectedSgf);
        try {
            String expectedSgf2=restoreAndSavePrepared(expectedSgf);
            String actualSgf=restoreAndSavePrepared(expectedSgf2);
            assertEquals(key.toString(),expectedSgf2,actualSgf);
        } catch(Exception e) {
            fail("'"+key+"' caught: "+e);
        }
    }

    private static String restoreAndSavePrepared(String sgf) {
        Model model=ModelTestIo.restoreNew(sgf);
        String saved=model.save();
        return SgfRoundTripHarness.prepareActual(saved);
    }
}
