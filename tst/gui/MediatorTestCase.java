package gui;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.*;
import model.Model;
import utilities.TestSupport;
// not a good name. these have nothing to do with mediator.
public class MediatorTestCase extends TestSupport {
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testGetExtension() {
        File file=new File("d:/ray/dev/goapps/go2/nosgfextension.sgy");
        String extention=Model.getExtension(file);
        if(extention==null||!extention.equalsIgnoreCase("sgf")) file=new File(file.getParent(),file.getName()+".sgf");
        assertTrue(file.getName().endsWith(".sgf"));
        // lots of duplicate code. file.getName().endsWith(".sgf")
    }
    @Test public void testGetExtension2() {
        File file=new File("savedxyz");
        String extention=Model.getExtension(file);
        if(extention==null||!extention.equalsIgnoreCase("sgf")) file=new File(file.getParent(),file.getName()+".sgf");
        assertTrue(file.getName().endsWith(".sgf"));
    }
}
