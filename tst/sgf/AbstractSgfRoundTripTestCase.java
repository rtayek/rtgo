package sgf;
import io.Logging;
import static org.junit.Assert.*;
import org.junit.*;
public abstract class AbstractSgfRoundTripTestCase extends AbstractSgfParserTestCase {
    @Override protected String normalizeExpectedSgf(String rawSgf) {
        return SgfHarness.prepareExpectedSgf(key,rawSgf);
    }
    protected static void assertNoLineFeeds(String sgf) {
        SgfHarness.assertNoLineFeeds(sgf);
    }
    protected static String prepareSgf(String sgf) {
        return SgfHarness.prepareSgf(sgf);
    }
    private final String prepareActual(String actualSgf) {
        return SgfHarness.prepareActual(actualSgf);
    }
    protected final void assertPreparedEquals(String preparedSgf) {
        SgfHarness.assertPreparedEquals(key,expectedSgf,preparedSgf);
    }
    private final void assertPreparedRoundTrip(String actualSgf) {
        SgfHarness.assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }
    private final void assertPreparedRoundTripWithParenthesesCheck(String actualSgf,String label) {
        SgfHarness.assertPreparedRoundTripWithParenthesesCheck(key,expectedSgf,actualSgf,label);
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
        SgfHarness.assertSgfSaveAndRestore(key,expectedSgf);
    }
    @Test public void testSgfRoundTrip() throws Exception {
        SgfHarness.assertSgfRoundTrip(key,expectedSgf);
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
        SgfHarness.assertRoundTripTwice(key,expectedSgf);
    }
    @Test public void testSgfCannonical() {
        SgfHarness.assertSgfCannonical(key,expectedSgf);
    }
}
