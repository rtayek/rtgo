package tree;
import utilities.MyTestWatcher;
import io.Logging;
import static org.junit.Assert.*;
import static tree.Node.*;
import java.util.*;
import org.junit.*;
import tree.G2.Generator;
import com.tayek.util.core.Iterators.Longs;
public class G2TestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    // round trip?
    // parameterize?
    // so far, none of these tests use a non null value for data.
    @Before public void setUp() throws Exception {
        g2=new G2();
        //all=new G2().generate(3);
        verbose=false; //
    }
    @Test public void testEncodeCases() {
        for(Object[] test:ENCODE_CASES) {
            int nodes=(Integer)test[0];
            boolean useMap=(Boolean)test[1];
            String[] expected=(String[])test[2];
            assertEncodings(nodes,useMap,expected);
        }
    }
    @Test public void testDecodeCases() {
        for(String expected:DECODE_CASES) {
            // tree is encoded as null
            // should be "0 maybe?
            Node<Long> tree=decode(expected,null);
            if(LOG_DECODE_DETAILS) {
                Logging.mainLogger.info("encoded in node: "+tree.encoded);
                Logging.mainLogger.info("ex: "+expected);
            }
            G2.p(tree);
            String actual=encode(tree,null);
            if(LOG_DECODE_DETAILS) Logging.mainLogger.info("ac: "+actual);
            Node<Long> actualNode=decode(actual,null);
            G2.p(actualNode);
            assertEquals(expected,actual);
            assertTrue(structureDeepEquals(tree,actualNode));
        }
    }
    @Test public void testMirrorCases() {
        for(int nodes:MIRROR_CASES) assertMirrorRoundTrip(nodes);
    }
    private void assertMirrorRoundTrip(int nodes) {
        trees=Generator.one(nodes,iterator,false);
        for(Node<Long> node:trees) {
            if(node==null) continue;
            String expected=encode(node,null);
            mirror(node);
            mirror(node);
            String actual=encode(node,null);
            assertEquals(expected,actual);
        }
    }
    private void assertEncodings(int nodes,boolean useMap,String... expectedEncodings) {
        trees=Generator.one(nodes,iterator,useMap);
        assertEquals(expectedEncodings.length,trees.size());
        for(int i=0;i<expectedEncodings.length;i++) {
            Node<Long> tree=trees.get(i);
            if(tree==null) assertNull(tree);
            else assertNotNull(tree);
            String encoded=encode(tree,null);
            assertEquals(expectedEncodings[i],encoded);
        }
    }
    private static final String[] DECODE_CASES=new String[] {"100","11000"};
    private static final int[] MIRROR_CASES=new int[] {2,3};
    private static final boolean LOG_DECODE_DETAILS=false;
    private static final Object[][] ENCODE_CASES=new Object[][] {
        {0,false,new String[] {"0"}},
        {0,true,new String[] {"0"}},
        {1,false,new String[] {"100"}},
        {1,true,new String[] {"100"}},
        {2,false,new String[] {"10100","11000"}},
        {2,true,new String[] {"10100","11000"}},
    };
    G2 g2;
    boolean verbose;
    Generator<Long> generator=new Generator<>(false);
    Iterator<Long> iterator=new Longs();
    ArrayList<Node<Long>> trees;
    //ArrayList<ArrayList<Node<Long>>> all;
}

