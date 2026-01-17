package sgf;

import io.Logging;
import io.IOs;
import static org.junit.Assert.*;
import static sgf.SgfNode.SgfOptions.containsQuotedControlCharacters;
import static utilities.Utilities.implies;
import sgf.SgfNode.SgfOptions;

final class SgfHarness {
    private SgfHarness() {}

    // Parser support
    static String prepareExpectedSgf(Object key,String sgf) {
        String normalized=sgf;
        if(normalized!=null) {
            normalized=SgfNode.options.prepareSgf(normalized);
            if(SgfNode.options.removeLineFeed) if(normalized.contains("\n)")) {
                Logging.mainLogger.info("lf badness");
                System.exit(0);
            }
            if(containsQuotedControlCharacters(key,normalized)) {
                Logging.mainLogger.info(key+" contains quoted control characters.");
                normalized=SgfOptions.removeQuotedControlCharacters(normalized);
            }
        }
        assertFalse(containsQuotedControlCharacters(key.toString(),normalized));
        return normalized;
    }

    static void assertKeyPresent(Object key,String expectedSgf) {
        if(!(key!=null||expectedSgf!=null)) {
            Logging.mainLogger.info("key!=null||expectedSgf!=null");
            IOs.stackTrace(10);
        }
        assertTrue(key!=null||expectedSgf!=null);
    }

    static SgfNode restoreExpectedSgf(String expectedSgf,Object key) {
        return restoreExpectedSgf(expectedSgf,key,true);
    }

    static SgfNode restoreExpectedSgf(String expectedSgf,Object key,boolean checkDelimiters) {
        if(checkDelimiters) SgfTestSupport.assertSgfDelimiters(expectedSgf,key);
        return SgfTestIo.restore(expectedSgf);
    }

    static SgfNode assertParse(Object key,String expectedSgf) {
        return restoreExpectedSgf(expectedSgf,key,true);
    }

    static SgfNode assertParse(Object key,String expectedSgf,boolean checkDelimiters) {
        return restoreExpectedSgf(expectedSgf,key,checkDelimiters);
    }

    static void assertHexAscii(Object key,String expectedSgf) {
        String encoded=expectedSgf!=null?HexAscii.encode(expectedSgf.getBytes()):null;
        String actualSgf=encoded!=null?HexAscii.decodeToString(encoded):null;
        String keyString=key!=null?key.toString():null;
        assertTrue(keyString,implies(expectedSgf==null,encoded==null));
        assertTrue(keyString,implies(encoded==null,actualSgf==null));
        assertEquals(keyString,expectedSgf,actualSgf);
    }

    static SgfNode assertFlags(Object key,String expectedSgf,boolean oldFlags) {
        return assertFlags(key,expectedSgf,oldFlags,true);
    }

    static SgfNode assertFlags(Object key,String expectedSgf,boolean oldFlags,boolean checkDelimiters) {
        SgfNode games=restoreExpectedSgf(expectedSgf,key,checkDelimiters);
        if(games==null) return games;
        if(oldFlags) games.oldPreorderCheckFlags();
        else games.preorderCheckFlags();
        return games;
    }

    // Round-trip support
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
