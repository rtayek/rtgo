package sgf;

import org.junit.Before;
import org.junit.runners.Parameterized;
import utilities.TestKeys;

abstract class AbstractSgfKeyedTestCase extends AbstractWatchedTestCase {
    @Parameterized.Parameter public Object key;
    protected String expectedSgf;

    protected Object defaultKey() {
        return null;
    }

    protected final void ensureKey() {
        if(key!=null) return;
        Object fallback=defaultKey();
        if(fallback==null&&this instanceof RedBeanKeyed) {
            fallback=TestKeys.sgfExampleFromRedBean;
        }
        if(fallback!=null) key=fallback;
    }

    @Before public void setUpKey() throws Exception {
        ensureKey();
        watcher.key=key;
    }

    protected SgfNode restoreExpectedSgf() {
        return SgfTestIo.restore(expectedSgf);
    }

    protected MNode restoreExpectedMNode() {
        return SgfTestIo.restoreMNode(expectedSgf);
    }
}
