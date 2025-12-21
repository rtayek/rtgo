package sgf;
import static io.IOs.noIndent;
import static org.junit.Assert.assertEquals;
import static sgf.SgfNode.sgfRestoreAndSave;
import java.io.*;
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
        StringWriter stringWriter=new StringWriter();
        node.saveSgf(stringWriter,noIndent);
        System.out.println("saved sgf node "+stringWriter.toString());
        System.out.println("----------------");
    }
    SgfNode sample() { // maybe use redbean example?
        SgfNode root=comment("root",null,null);
        System.out.print("node root: "+root+" ");
        print(root);
        SgfNode left1=comment("left1",root,null);
        System.out.print("node left1: "+left1+" ");
        print(root);
        SgfNode right1=comment("right1",null,root);
        System.out.print("node right; "+right1+" ");
        print(root);
        SgfNode left1Left2=comment("left1.left2",left1,null);
        System.out.print("node left1Left2: "+left1Left2+" ");
        print(root);
        SgfNode left1right1=comment("left1.right1",null,left1);
        System.out.print("node left1.right1: "+left1right1+" ");
        print(root);
        System.out.println("node at end");
        return root;
    }
    @Test public void testSample() {
        SgfNode root=sample();
        root.saveSgf(new OutputStreamWriter(System.out),noIndent);
        StringWriter stringWriter=new StringWriter();
        root.saveSgf(stringWriter,noIndent);
        String expected=stringWriter.toString();
        String actual=sgfRestoreAndSave(expected);
        assertEquals(expected,actual);
    }
}
