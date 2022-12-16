package sgf;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;
import equipment.*;
import utilities.MyTestWatcher;
public class SgfNodeTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testSgfCoordinates() {
        SgfNode expected=new SgfNode();
        List<String> list=Arrays.asList(new String[] {"AB"}); // what is AB?
        SgfProperty property=new SgfProperty(P.B,list);
        expected.add(property);
        String string=expected.properties.get(0).list().get(0);
        Point point=Coordinates.fromSgfCoordinates(string,Board.standard);
        String string2=Coordinates.toSgfCoordinates(point,Board.standard);
        SgfNode actual=new SgfNode();
        List<String> list2=Arrays.asList(new String[] {string2});
        SgfProperty property2=new SgfProperty(P.B,list2);
        actual.add(property2);
        assertEquals(expected,actual);
    }
    @Test public void testHasAMoveFlag() {
        SgfNode node=new SgfNode();
        List<String> list=Arrays.asList(new String[] {"inside the brackets"});
        SgfProperty property=new SgfProperty(P.B,list);
        node.add(property);
        assertTrue(node.hasAMoveType);
        assertTrue(node.hasAMove);
        node.setFlags();
        node.checkFlags();
        MNode mNode=MNode.toGeneralTree(node);
        for(MNode child:mNode.children) {
            System.out.println(child);
            child.setFlags();
            child.checkFlags();
            assertTrue(child.hasAMoveType);
            assertTrue(child.hasAMove);
        }
    }
    @Test public void testHasAMoveTypeFlag() {
        SgfNode node=new SgfNode();
        List<String> list=Arrays.asList(new String[] {"inside the brackets"});
        SgfProperty property=new SgfProperty(P.BM,list);
        node.add(property);
        assertTrue(node.hasAMoveType);
        assertFalse(node.hasAMove);
        MNode mNode=MNode.toGeneralTree(node);
        for(MNode child:mNode.children) {
            child.setFlags();
            assertTrue(child.hasAMoveType);
            assertFalse(child.hasAMove);
        }
    }
    @Test public void testBothFlagsFalse() {
        SgfNode node=new SgfNode();
        List<String> list=Arrays.asList(new String[] {"inside the brackets"});
        SgfProperty property=new SgfProperty(P.AB,list);
        node.add(property);
        assertFalse(node.hasAMoveType);
        assertFalse(node.hasAMove);
        MNode mNode=MNode.toGeneralTree(node);
        assertFalse(mNode.hasAMoveType);
        assertFalse(mNode.hasAMove);
    }
    @Test public void testConstructor() { SgfNode sgfNode=new SgfNode(); sgfNode.properties=new ArrayList<>(); }
    @Test public void testRT() {
        MNode mRoot=new MNode(null);
        try {
            SgfProperty property=new SgfProperty(P.RT,Arrays.asList(new String[] {"Tgo root"}));
            mRoot.properties.add(property);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        SgfNode sgfNode=mRoot.toBinaryTree();
        System.out.println(sgfNode);

    }
}
