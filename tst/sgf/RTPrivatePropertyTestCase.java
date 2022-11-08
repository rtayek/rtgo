package sgf;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class RTPrivatePropertyTestCase {
    public RTPrivatePropertyTestCase(Object key) { this.key=key; }
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        return ParameterArray.parameterize(Parser.sgfData());
    }
    @Before public void setUp() throws Exception {
        originalSgf=getSgfData(key);
        // no prepare here!
    }
    @After public void tearDown() throws Exception {}
    @Test public void testCannonical() {
        String expectedSgf=sgfRoundTrip(originalSgf);
        String actualSgf=sgfRoundTrip(expectedSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testMultipleGames() {
        String expectedSgf=sgfRoundTrip(originalSgf);
        //System.out.println("exx: "+expectedSgf);
        assertFalse(expectedSgf.contains(P.RT.toString()));
    }
    Object key;
    String originalSgf;
    static final Set<String> paths=new LinkedHashSet<>();
}
