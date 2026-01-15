package sgf;
import io.Logging;
import static io.IOs.noIndent;
import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.*;
public class SimpleParserTestCase {
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    private SgfNode comment(String string,SgfNode left,SgfNode right) {
        SgfNode node=new SgfNode();
        List<String> list=Arrays.asList(new String[] {string});
        SgfProperty property=new SgfProperty(P.C,list);
        //y= new Property(P.B,list);
        node.add(property);
        if(left==null&&right==null) return node;
        if(left!=null) left.left=node;
        else if(right!=null) right.right=node;
        else throw new RuntimeException("both left and right are not null!");
        return node;
    }
    void print(SgfNode node) {
        Logging.mainLogger.info("saved sgf node "+SgfTestIo.save(node,noIndent));
        Logging.mainLogger.info("----------------");
    }
    SgfNode sample() { // maybe use redbean example?
        SgfNode root=comment("root",null,null);
        Logging.mainLogger.info("node root: "+root+" ");
        print(root);
        SgfNode left1=comment("left1",root,null);
        Logging.mainLogger.info("node left1: "+left1+" ");
        print(root);
        SgfNode right1=comment("right1",null,root);
        Logging.mainLogger.info("node right; "+right1+" ");
        print(root);
        SgfNode left1Left2=comment("left1.left2",left1,null);
        Logging.mainLogger.info("node left1Left2: "+left1Left2+" ");
        print(root);
        SgfNode left1right1=comment("left1.right1",null,left1);
        Logging.mainLogger.info("node left1.right1: "+left1right1+" ");
        print(root);
        Logging.mainLogger.info("node at end");
        return root;
    }
    @Test public void testSample() {
        SgfNode root=sample();
        String expected=SgfTestIo.save(root,noIndent);
        Logging.mainLogger.info("sample sgf: "+expected);
        String actual=SgfTestSupport.restoreAndSave(expected);
        assertEquals(expected,actual);
    }
}
