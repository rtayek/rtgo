package sgf;

import java.util.Collection;
import org.junit.runners.Parameterized.Parameters;

public abstract class AbstractAllSgfRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        return SgfTestSupport.allSgfParameters();
    }
}
