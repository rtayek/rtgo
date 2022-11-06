package server;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import io.IO;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedGoServerTestCase extends AbstractGoServerTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public ParameterizedGoServerTestCase(Integer i) { this.i=i; }
    @Override @Before public void setUp() throws Exception {
        serverPort=IO.anyPort;
        super.setUp();
    }
    @Override @After public void tearDown() throws Exception { super.tearDown(); }
    @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
    final Integer i;
    static int n=2; // fails sometimes with bigger n.
}
