package sgf;

import io.Logging;
import static org.junit.Assert.*;

final class SgfRoundTripHarness {
    private SgfRoundTripHarness() {}

    static String prepareExpectedSgf(Object key,String rawSgf) {
        return SgfParserHarness.prepareExpectedSgf(key,rawSgf);
    }

    static void assertNoLineFeeds(String sgf) {
        if(sgf!=null) assertFalse(sgf.contains("\n"));
    }

    static String prepareSgf(String sgf) {
        return sgf!=null?SgfNode.options.prepareSgf(sgf):null;
    }

    static String prepareActual(String actualSgf) {
        return prepareSgf(actualSgf);
    }

    static void assertPreparedEquals(Object key,String expectedSgf,String preparedSgf) {
        assertEquals(key.toString(),expectedSgf,preparedSgf);
    }

    static void assertPreparedRoundTrip(Object key,String expectedSgf,String actualSgf) {
        assertPreparedEquals(key,expectedSgf,prepareActual(actualSgf));
    }

    static void assertPreparedRoundTripWithParenthesesCheck(Object key,String expectedSgf,String actualSgf,String label) {
        String prepared=prepareActual(actualSgf);
        SgfTestSupport.logBadParentheses(prepared,key,label);
        assertPreparedEquals(key,expectedSgf,prepared);
    }

    static void assertSgfSaveAndRestore(Object key,String expectedSgf) {
        assertNoLineFeeds(expectedSgf);
        SgfNode expected=SgfTestIo.restore(expectedSgf);
        SgfNode actualSgf=SgfTestIo.saveAndRestore(expected);
        if(expected!=null) assertTrue(key.toString(),expected.deepEquals(actualSgf));
    }

    static void assertSgfRoundTrip(Object key,String expectedSgf) {
        if(expectedSgf==null) return;
        String actualSgf=SgfTestSupport.restoreAndSave(expectedSgf);
        actualSgf=prepareSgf(actualSgf);
        if(actualSgf.length()==expectedSgf.length()+1) if(actualSgf.endsWith(")")) {
            Logging.mainLogger.info(key+"removing extra ')' "+actualSgf.length());
            if(true) throw new RuntimeException(key+"removing extra ')' "+actualSgf.length());
            actualSgf=actualSgf.substring(0,actualSgf.length()-1);
        }
        assertPreparedEquals(key,expectedSgf,actualSgf);
    }

    static void assertRoundTripTwice(Object key,String expectedSgf) {
        assertNoLineFeeds(expectedSgf);
        boolean isOk=SgfTestIo.roundTripTwice(expectedSgf);
        assertTrue(key.toString(),isOk);
    }

    static void assertSgfCannonical(Object key,String expectedSgf) {
        assertNoLineFeeds(expectedSgf);
        SgfTestSupport.assertSgfRestoreSaveStable(expectedSgf,key);
    }

    static void assertMNodeRoundTrip(Object key,String expectedSgf,SgfRoundTrip.MNodeSaveMode saveMode,boolean logExpected) {
        if(logExpected) SgfTestSupport.logBadParentheses(expectedSgf,key,"ex");
        String actualSgf=SgfTestIo.mNodeRoundTrip(expectedSgf,saveMode);
        assertPreparedRoundTripWithParenthesesCheck(key,expectedSgf,actualSgf,"ac");
    }
}
