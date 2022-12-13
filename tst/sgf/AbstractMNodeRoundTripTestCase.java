package sgf;
import static org.junit.Assert.assertEquals;
import static sgf.MNode.mNodeRoundTrip;
import java.io.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractMNodeRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testMMNodeRoundTrip() throws Exception {
        int p=Parser.parentheses(expectedSgf);
        if(p!=0) System.out.println("ex bad parentheses: "+p);
        StringReader stringReader=new StringReader(expectedSgf);
        StringWriter stringWriter=new StringWriter();
        MNode root=mNodeRoundTrip(stringReader,stringWriter);
        String actualSgf=stringWriter.toString();
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        /*int*/ p=Parser.parentheses(actualSgf);
        if(p!=0) System.out.println("ac bad parentheses: "+p);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Ignore @Test public void testMMNodeDirectRoundTrip() throws Exception {
        StringReader stringReader=new StringReader(expectedSgf);
        StringWriter stringWriter=new StringWriter();
        MNode root=MNode.mNodeDirectRoundTrip(stringReader,stringWriter);
        String actualSgf=stringWriter.toString();
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        //Boolean ok=specialCases(actualSgf);
        //if(ok) return;
        int p=Parser.parentheses(actualSgf);
        if(p!=0) System.out.println(" bad parentheses: "+p);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
}
