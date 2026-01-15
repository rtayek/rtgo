package sgf.combine;
import io.Logging;
import static org.junit.Assert.assertTrue;
import java.io.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import sgf.AbstractWatchedTestCase;
import utilities.ParameterArray;
@Ignore @RunWith(Parameterized.class) public class ParameterizedCombineTestCase extends AbstractWatchedTestCase {
    @Parameters public static Collection<Object[]> data() {
        // consoldate this!
        // yes, look at this later.
        String[] names=new String[] {"simple.sgf","ff4_ex.sgf",/*"test.sgf","test.sgf"*/};
        // first test.sgf above was in annotated/
        List<Object> objects=new ArrayList<>(Arrays.asList((Object[])(names)));
        return ParameterArray.parameterize(objects);
    }
    @Before public void setUp() throws Exception { Logging.mainLogger.info(String.valueOf(filename)); }
    public ParameterizedCombineTestCase(String file) { this.filename=file; }
    @Test public void testCombine() {
        File file=new File(Combine.pathToHere,filename);
        Logging.mainLogger.info(String.valueOf(file));
        if(file.exists()) {
            assertTrue(file.toString(),file.exists());
            //assertTrue(testCombine(""+file));
            assertTrue(file.toString(),CombineTest.testCombine(filename));
        } else Logging.mainLogger.info("file "+file+" does not exist!");
    }
    // ignore test combine. this is a path confusion problem.
    String filename;
}
