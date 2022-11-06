package sgf;
import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import model.Model;
import utilities.MyTestWatcher;
public abstract class MNodeRoundTripTestCase extends SgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testMNodeRoundTripTwoEmptyWithSemicolon() throws Exception {
        expected=expected.replace("\n","");
        MNode root=MNode.restore(new StringReader(expected));
        Model model=new Model();
        model.setRoot(root);
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String actual=stringWriter.toString();
        actual=actual.replace("\n",""); // who is putting the linefeed in?
        if(!expected.equals(actual)) ; //printDifferences(expected,actual);
        assertEquals(expected,actual);
    }
}
