package sgf;

import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class) public class SgfModelRoundTripTestCase extends AbstractModelRoundtripTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        return SgfTestSupport.allSgfParameters();
    }

    @Test public void testCheckBoardInRoot() {
        String sgf=rawSgf!=null?rawSgf:expectedSgf;
        SgfModelRoundTripHarness.assertCheckBoardInRoot(key,sgf);
    }
}
