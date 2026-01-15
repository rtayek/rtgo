package sgf;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractIgnoreMoveAndSetupFlagsTestCase extends AbstractWatchedTestCase {
    private boolean oldIgnoreFlags;

    @Before public void setUpIgnoreMoveAndSetupFlags() throws Exception {
        oldIgnoreFlags=SgfNode.ignoreMoveAndSetupFlags;
        SgfNode.ignoreMoveAndSetupFlags=true;
    }

    @After public void tearDownIgnoreMoveAndSetupFlags() throws Exception {
        SgfNode.ignoreMoveAndSetupFlags=oldIgnoreFlags;
    }
}
