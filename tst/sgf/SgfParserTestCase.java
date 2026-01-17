package sgf;

import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class) public class SgfParserTestCase extends AbstractWatchedTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        return SgfTestSupport.parserParameters();
    }

    @Parameterized.Parameter public Object key;
    private String expectedSgf;
    private SgfNode games;
    private boolean rawInput;

    @Before public void setUp() throws Exception {
        watcher.key=key;
        rawInput=false;
        if(key instanceof SgfTestSupport.RawSgf raw) {
            expectedSgf=raw.sgf();
            rawInput=true;
        } else {
            expectedSgf=SgfTestSupport.loadExpectedSgf(key);
        }
    }

    @Test public void testKey() {
        SgfHarness.assertKeyPresent(key,expectedSgf);
    }

    @Test public void testParse() {
        games=SgfHarness.assertParse(key,expectedSgf,!rawInput);
    }

    @Test public void testHexAscii() {
        SgfHarness.assertHexAscii(key,expectedSgf);
    }

    @Test public void testFlags() {
        games=SgfHarness.assertFlags(key,expectedSgf,true,!rawInput);
    }

    @Test public void testFlagsNew() {
        games=SgfHarness.assertFlags(key,expectedSgf,false,!rawInput);
    }
}
