package sgf.combine;
import static io.IO.standardIndent;
import static org.junit.Assert.assertTrue;
import java.io.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import sgf.SgfNode;
import utilities.MyTestWatcher;
@RunWith(Parameterized.class) public class ParameterizedCombineTestCase {
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
    @Before public void setUp() throws Exception { System.out.println(filename); }
    @After public void tearDown() throws Exception {}
    public ParameterizedCombineTestCase(String file) { this.filename=file; }
    static boolean testCombine(String name) {
        // this needs a matching file in annotated/.
        System.out.println("test combine: "+name);
        try {
            SgfNode combined=Combine.combine(name);
            if(combined!=null) {
                System.err.println("combined");
                Writer writer=new StringWriter();
                combined.save(writer,standardIndent);
                System.err.println(writer.toString());
                System.err.println();
            } else {
                System.err.println("combine returns null!");
                return false;
            }
        } catch(Exception e) {
            System.err.println("in testCombine()");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Test public void testCombine() {
        File file=new File(Combine.pathToHere,filename);
        System.out.println(file);
        if(file.exists()) {
            assertTrue(file.toString(),file.exists());
            //assertTrue(testCombine(""+file));
            assertTrue(file.toString(),testCombine(filename));
        } else System.out.println("file "+file+" does not exist!");
    }
    // ignore test combine. this is a path confusion problem.
    String filename;
}
