package sgf;
import io.Logging;
import static org.junit.Assert.*;
import org.junit.*;
public abstract class AbstractSgfRoundTripTestCase extends AbstractSgfParserTestCase {
    @Override protected String normalizeExpectedSgf(String rawSgf) {
        return prepareExpectedSgf(rawSgf);
    }
    protected static void assertNoLineFeeds(String sgf) {
        if(sgf!=null) assertFalse(sgf.contains("\n"));
    }
    protected static String prepareSgf(String sgf) {
        return sgf!=null?SgfNode.options.prepareSgf(sgf):null;
    }
    protected final String prepareActual(String actualSgf) {
        return prepareSgf(actualSgf);
    }
    protected final void assertPreparedEquals(String preparedSgf) {
        assertEquals(key.toString(),expectedSgf,preparedSgf);
    }
    protected final void assertPreparedRoundTrip(String actualSgf) {
        assertPreparedEquals(prepareActual(actualSgf));
    }
    protected final void assertPreparedRoundTripWithParenthesesCheck(String actualSgf,String label) {
        String prepared=prepareActual(actualSgf);
        SgfTestSupport.logBadParentheses(prepared,key,label);
        assertPreparedEquals(prepared);
    }
    private Boolean specialCases(String actualSgf) {
        Boolean ok=false; // no more assertions are needed
        if(expectedSgf.equals("")) expectedSgf=null; //11/29/22
        else if(expectedSgf.equals("()")) expectedSgf=null;
        if(expectedSgf==null) {
            if(actualSgf==null) ok=true;
            else if(actualSgf.equals("")) { actualSgf=null; ok=true; }
        } else {
            if(!expectedSgf.equals(actualSgf)) {
                if(expectedSgf.contains("PC[OGS: https://online-go.com/")) {
                    Logging.mainLogger.info("passing ogs file");
                    ok=true;
                }
            }
        }
        return ok;
    }
    @Test public void testSgfSaveAndRestore() throws Exception {
        // but does a restore first, then a deep equals on the trees.
        assertNoLineFeeds(expectedSgf);
        SgfNode expected=restoreExpectedSgf();
        SgfNode actualSgf=SgfTestIo.saveAndRestore(expected);
        if(expected!=null) assertTrue(key.toString(),expected.deepEquals(actualSgf));
    }
    @Test public void testSgfRoundTrip() throws Exception {
        if(expectedSgf==null) return;
        String actualSgf=SgfTestSupport.restoreAndSave(expectedSgf);
        actualSgf=prepareSgf(actualSgf);
        if(actualSgf.length()==expectedSgf.length()+1) if(actualSgf.endsWith(")")) {
            Logging.mainLogger.info(key+"removing extra ')' "+actualSgf.length());
            if(true) throw new RuntimeException(key+"removing extra ')' "+actualSgf.length());
            actualSgf=actualSgf.substring(0,actualSgf.length()-1);
        }
        // how to do this more often?
        //Boolean ok=specialCases(actualSgf);
        //if(ok) return;
        assertPreparedEquals(actualSgf);
    }
    @Ignore @Test public void testSPreordergfRoundTrip() throws Exception {
        if(expectedSgf==null) return;
        if(expectedSgf.equals("")) return;
        expectedSgf=prepareSgf(expectedSgf);
        String actualSgf=SgfNode.preorderRouundTrip(expectedSgf);
        SgfTestSupport.logBadParentheses(expectedSgf,key,"ex");
        assertPreparedEquals(actualSgf);
    }
    @Test public void testRSgfoundTripeTwice() throws Exception {
        assertNoLineFeeds(expectedSgf);
        boolean isOk=SgfTestIo.roundTripTwice(expectedSgf);
        assertTrue(key.toString(),isOk);
    }
    @Test public void testSgfCannonical() {
        assertNoLineFeeds(expectedSgf);
        SgfTestSupport.assertSgfRestoreSaveStable(expectedSgf,key);
    }
}
