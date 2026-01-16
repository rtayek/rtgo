package sgf;

import io.Logging;
import static org.junit.Assert.*;
import static sgf.SgfNode.SgfOptions.containsQuotedControlCharacters;
import static utilities.Utilities.implies;
import io.IOs;
import sgf.SgfNode.SgfOptions;

final class SgfParserHarness {
    private SgfParserHarness() {}

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
}
