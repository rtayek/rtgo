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

    @Before public void setUp() throws Exception {
        watcher.key=key;
        expectedSgf=SgfTestSupport.loadExpectedSgf(key);
    }

    @Test public void testKey() {
        SgfParserHarness.assertKeyPresent(key,expectedSgf);
    }

    @Test public void testParse() {
        games=SgfParserHarness.assertParse(key,expectedSgf);
    }

    @Test public void testHexAscii() {
        SgfParserHarness.assertHexAscii(key,expectedSgf);
    }

    @Test public void testFlags() {
        games=SgfParserHarness.assertFlags(key,expectedSgf,true);
    }

    @Test public void testFlagsNew() {
        games=SgfParserHarness.assertFlags(key,expectedSgf,false);
    }
}
