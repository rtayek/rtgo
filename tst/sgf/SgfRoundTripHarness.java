package sgf;

import io.Logging;
import static org.junit.Assert.*;

final class SgfRoundTripHarness {
    private SgfRoundTripHarness() {}

    static String prepareExpectedSgf(Object key,String rawSgf) {
        return SgfParserHarness.prepareExpectedSgf(key,rawSgf);
    }

    static void assert5NoLineFeeds(String sgf) {
        if(sgf!=null) assertFalse(sgf.contains("\n"));
    }

    static String prepare3Sgf(String sgf) {
        return sgf!=null?SgfNode.options.prepareSgf(sgf):null;
    }

    static String prepare4Actual(String actualSgf) {
        return prepare3Sgf(actualSgf);
    }

    static void assert4PreparedEquals(Object key,String expectedSgf,String preparedSgf) {
        assertEquals(key.toString(),expectedSgf,preparedSgf);
    }

    static void assert6PreparedRoundTrip(Object key,String expectedSgf,String actualSgf) {
        assert4PreparedEquals(key,expectedSgf,prepare4Actual(actualSgf));
    }

    static void assert2PreparedRoundTripWithParenthesesCheck(Object key,String expectedSgf,String actualSgf,String label) {
        String prepared=prepare4Actual(actualSgf);
        SgfTestSupport.logBadParentheses(prepared,key,label);
        assert4PreparedEquals(key,expectedSgf,prepared);
    }

    static void assertSgfSaveAndRestore(Object key,String expectedSgf) {
        assert5NoLineFeeds(expectedSgf);
        SgfNode expected=SgfTestIo.restore(expectedSgf);
        SgfNode actualSgf=SgfTestIo.saveAndRestore(expected);
        if(expected!=null) assertTrue(key.toString(),expected.deepEquals(actualSgf));
    }

    static void assertSgfRoundTrip(Object key,String expectedSgf) {
        if(expectedSgf==null) return;
        String actualSgf=SgfTestSupport.restoreAndSave(expectedSgf);
        actualSgf=prepare3Sgf(actualSgf);
        if(actualSgf.length()==expectedSgf.length()+1) if(actualSgf.endsWith(")")) {
            Logging.mainLogger.info(key+"removing extra ')' "+actualSgf.length());
            if(true) throw new RuntimeException(key+"removing extra ')' "+actualSgf.length());
            actualSgf=actualSgf.substring(0,actualSgf.length()-1);
        }
        assert4PreparedEquals(key,expectedSgf,actualSgf);
    }

    static void assertRoundTripTwice(Object key,String expectedSgf) {
        assert5NoLineFeeds(expectedSgf);
        boolean isOk=SgfTestIo.roundTripTwice(expectedSgf);
        assertTrue(key.toString(),isOk);
    }

    static void assertSgfCannonical(Object key,String expectedSgf) {
        assert5NoLineFeeds(expectedSgf);
        SgfTestSupport.assertSgfRestoreSaveStable(expectedSgf,key);
    }

    static void assertMNodeRoundTrip(Object key,String expectedSgf,SgfRoundTrip.MNodeSaveMode saveMode,boolean logExpected) {
        if(logExpected) SgfTestSupport.logBadParentheses(expectedSgf,key,"ex");
        String actualSgf=SgfTestIo.mNodeRoundTrip(expectedSgf,saveMode);
        assert2PreparedRoundTripWithParenthesesCheck(key,expectedSgf,actualSgf,"ac");
    }
}
