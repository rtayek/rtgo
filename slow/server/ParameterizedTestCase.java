package server;
import static io.Logging.serverLogger;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
// was excluded some time ago.
// some of the test cases do not exist in this project but do exist in earlier versions.
/*@RunWith(Parameterized.class) public class ParameterizedTestCase extends GoServerTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        super.setUp();
        if(!i.equals(previous)) serverLogger.info("parameter: "+i);
        previous=i;
    }
    public ParameterizedTestCase(Integer i) { this.i=i; }
    @Parameters public static Collection<Object[]> data() {
        return ParameterArray.modulo(n);
    }
    final Integer i;
    static Integer previous=-1;
    static final int n=1000; // 1000 fails a lot around 685
}
*/
