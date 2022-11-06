package model;
import static org.junit.Assert.assertNotNull;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import sgf.Parser;
import utilities.MyTestWatcher;
@RunWith(Parameterized.class) public class ParameterizedModelRoundTripTestCase extends ModelRoundtripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Override @Before public void setUp() throws Exception {
        expected=Parser.getSgfData(key);
        assertNotNull(expected);
        expected=Parser.options.prepareSgf(expected);
    }
    @Override @After public void tearDown() throws Exception {}
    @Parameters public static Collection<Object[]> parameters() {
        //return Parser.sgfTestData();
        return Parser.sgfData();
    }
    public ParameterizedModelRoundTripTestCase(Object key) {
        this.key=key;
    }
    final Object key;
}
