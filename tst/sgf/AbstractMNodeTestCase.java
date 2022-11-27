package sgf;
import static org.junit.Assert.assertEquals;
import java.io.StringWriter;
import org.junit.*;
import model.Model;
import utilities.MyTestWatcher;
public abstract class AbstractMNodeTestCase extends AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testLongRoundTrip2() throws Exception {
        // maybe we can get rid of this?
        StringWriter stringWriter=new StringWriter();
        @SuppressWarnings("unused") MNode games=Model.mNoderoundTrip2(expectedSgf,stringWriter);
        String actualSgf=stringWriter.toString();
        if(expectedSgf==null) actualSgf=null; // hack for now
        System.out.println("ex: "+expectedSgf);
        System.out.println("ac: "+actualSgf);
        actualSgf=SgfNode.options.prepareSgf(actualSgf); // actual has line feeds now
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
}
