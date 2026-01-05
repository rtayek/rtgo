package sgf.combine;
import io.Logging;
import static io.IOs.standardIndent;
import static org.junit.Assert.assertTrue;
import java.io.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import sgf.SgfNode;
import sgf.SgfTestIo;
import utilities.MyTestWatcher;
@Ignore @RunWith(Parameterized.class) public class ParameterizedCombineTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> data() {
        // consoldate this!
        // yes, look at this later.
        String[] names=new String[] {"simple.sgf","ff4_ex.sgf",/*"test.sgf","test.sgf"*/};
        // first test.sgf above was in annotated/
        List<Object[]> list=new ArrayList<Object[]>();
        for(String name:names) {
            Object[] array=new Object[1];
            array[0]=name;
            list.add(array);
        }
        return list;
    }
    @Before public void setUp() throws Exception { Logging.mainLogger.info(String.valueOf(filename)); }
    @After public void tearDown() throws Exception {}
    public ParameterizedCombineTestCase(String file) { this.filename=file; }
    static boolean testCombine(String name) {
        // this needs a matching file in annotated/.
        Logging.mainLogger.info("test combine: "+name);
        try {
            SgfNode combined=Combine.combine(name);
            if(combined!=null) {
                Logging.mainLogger.warning("combined");
                Logging.mainLogger.warning(String.valueOf(SgfTestIo.save(combined,standardIndent)));
                Logging.mainLogger.warning("");
            } else {
                Logging.mainLogger.warning("combine returns null!");
                return false;
            }
        } catch(Exception e) {
            Logging.mainLogger.warning("in testCombine()");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Test public void testCombine() {
        File file=new File(Combine.pathToHere,filename);
        Logging.mainLogger.info(String.valueOf(file));
        if(file.exists()) {
            assertTrue(file.toString(),file.exists());
            //assertTrue(testCombine(""+file));
            assertTrue(file.toString(),testCombine(filename));
        } else Logging.mainLogger.info("file "+file+" does not exist!");
    }
    // ignore test combine. this is a path confusion problem.
    String filename;
}
