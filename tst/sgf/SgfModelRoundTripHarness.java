package sgf;

import io.Logging;
import io.IOs;
import static org.junit.Assert.*;
import model.Model;
import model.ModelHelper.ModelSaveMode;
import model.Navigate;

final class SgfModelRoundTripHarness {
    private SgfModelRoundTripHarness() {}

    static void assertModelRestoreAndSave(Object key,String expectedSgf,boolean oldWay) {
        Model model=new Model("",oldWay);
        assertModelRestoreAndSave(key,expectedSgf,model);
    }

    static void assertModelRestoreAndSave(Object key,String expectedSgf,Model model) {
        String actualSgf=ModelTestIo.restoreAndSave(model,expectedSgf);
        SgfRoundTripHarness.assert6PreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRoundTripToString(Object key,String expectedSgf,ModelSaveMode saveMode,boolean log) {
        String actualSgf=ModelTestIo.modelRoundTripToString(expectedSgf,saveMode);
        if(log) {
            Logging.mainLogger.info("ex: "+expectedSgf);
            Logging.mainLogger.info("ac: "+actualSgf);
        }
        SgfRoundTripHarness.assert6PreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRoundTripTwice(String sgf) {
        String expected=ModelTestIo.modelRoundTripToString(sgf);
        String actual=ModelTestIo.modelRoundTripToString(expected);
        assertEquals(expected,actual);
    }

    static void assertCheckBoardInRoot(Object key,String sgf) {
        if(key==null) IOs.stackTrace(10);
        assertNotNull(key);
        checkBoardInRoot(key,sgf);
        Logging.mainLogger.info("after key: "+key);
    }

    static void assertSgfRestoreAndSave(Object key,String expectedSgf) {
        String actualSgf=SgfTestSupport.restoreAndSave(expectedSgf);
        SgfRoundTripHarness.assert6PreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRestoreAndSaveWithExplicitModel(Object key,String expectedSgf) {
        Model model=new Model();
        Logging.mainLogger.info("ex: "+expectedSgf);
        String actualSgf=ModelTestIo.restoreAndSave(model,expectedSgf,key.toString());
        actualSgf=SgfNode.options.removeUnwanted(actualSgf);
        SgfRoundTripHarness.assert6PreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelSaveFromMNode(Object key,String expectedSgf,MNode root) {
        Model model=new Model();
        model.setRoot(root);
        String actualSgf=ModelTestIo.save(model,key.toString());
        SgfRoundTripHarness.assert6PreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertCanonicalRoundTripTwice(Object key,String expectedSgf) {
        SgfRoundTripHarness.assert5NoLineFeeds(expectedSgf);
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
        return SgfRoundTripHarness.prepare4Actual(saved);
    }

    private static boolean checkBoardInRoot(Object key,String sgf) {
        // move this?
        if(key==null) { Logging.mainLogger.info("key is null!"); return true; }
        Model original=ModelTestIo.restoreNew(sgf);
        boolean hasABoard=original.board()!=null;
        Model model=ModelTestIo.restoreNew(sgf);
        if(model.board()==null); // Logging.mainLogger.info("model has no board!");
        else Logging.mainLogger.info("model has a board!");
        Navigate.down.do_(model);
        Model.mainLineFromCurrentPosition(model);
        return hasABoard;
    }
}
