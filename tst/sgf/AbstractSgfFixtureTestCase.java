package sgf;
import java.util.function.Function;
import org.junit.Before;
abstract class AbstractSgfFixtureTestCase extends AbstractSgfKeyedTestCase {
    @Before public void setUp() throws Exception {
        expectedSgf=SgfTestSupport.loadExpectedSgf(key);
        if(expectedSgf==null) { return; }
    }
    protected final SgfNode restoreAndTraverse(SgfAcceptor acceptor) {
        SgfNode games=restoreExpectedSgf();
        if(games!=null) SgfTestSupport.traverse(acceptor,games);
        return games;
    }
    protected final SgfNode restoreAndTraverse(Function<SgfNode,SgfAcceptor> acceptorFactory) {
        SgfNode games=restoreExpectedSgf();
        if(games!=null) SgfTestSupport.traverse(acceptorFactory.apply(games),games);
        return games;
    }
    void assertSgfDelimiters() {
        SgfTestSupport.assertSgfDelimiters(expectedSgf,key);
    }
}
