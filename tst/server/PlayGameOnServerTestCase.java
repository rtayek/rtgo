package server;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import controller.GameFixture;
import io.IO;
import utilities.*;
@RunWith(Parameterized.class) public class PlayGameOnServerTestCase extends AbstractGoServerTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // some tests time out when other jvms are running!
    @Override @Before public void setUp() throws Exception { serverPort=IO.anyPort; super.setUp(); }
    @Override @After public void tearDown() throws Exception { super.tearDown(); }
    public PlayGameOnServerTestCase(int i) { this.i=i; }
    @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
    @Test(/*timeout=GTPBackEnd.timeoutTime*/) public void testPlaySillyGame() throws Exception {
        GameFixture.playSillyGame(game,1);
    }
    final int i;
    static final int n=2;
}
