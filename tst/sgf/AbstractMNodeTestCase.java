package sgf;
import org.junit.Rule;
import utilities.MyTestWatcher;
public abstract class AbstractMNodeTestCase extends AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // moved testLongRoundTrip2() to model round trip
    // this should have stuff that is not a round trip test.
}
