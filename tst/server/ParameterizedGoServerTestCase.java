package server;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import io.IOs;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedGoServerTestCase extends AbstractGoServerTestCase {
    public ParameterizedGoServerTestCase(Integer i) { this.i=i; }
    @Override @Before public void setUp() throws Exception { serverPort=IOs.anyPort; super.setUp(); }
    @Override @After public void tearDown() throws Exception { super.tearDown(); }
    @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
    final Integer i;
    static int n=1; // fails sometimes with bigger n.
    // 1/2/23 with n=200, one "game was not started "failure.
}
