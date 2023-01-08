package sgf;
import static org.junit.Assert.*;
import java.io.StringReader;
import java.util.List;
import java.util.function.BiPredicate;
import org.junit.*;
import model.MNodeAcceptor.*;
import utilities.Iterators.Longs;
public class LabelMNodeTestCase {
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testNodeNotInTree() {
        MNode mNode=new MNode(null);
        MNodeFinder finder=MNodeFinder.find(mNode,root2,equalsPredicate);
        assertTrue(finder.ancestors.size()==0);
    }
    @Test public void testFindSelf() {
        MNode mNode=new MNode(null);
        MNodeFinder finder=MNodeFinder.find(root1,root1,equalsPredicate);
        assertTrue(finder.ancestors.size()>0);
    }
    @Test public void testFindSelfWithLabelPredicate() {
        MNodeFinder finder=MNodeFinder.find(root1,root1,labelPredicate);
        System.out.println(finder.found);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(root1.label,finder.found.label);
    }
    @Test public void testFindWithLabelPredicate() {
        MNodeFinder finder=MNodeFinder.find(root1,root2,labelPredicate);
        System.out.println(root1.label+","+root2.label);
        System.out.println(finder.ancestors);
        System.out.println(finder.found);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(root1.label,finder.found.label);
    }
    @Test public void testFindFirstChildWithLabelPredicate() {
        MNode child=root1.children.iterator().next();
        MNodeFinder finder=MNodeFinder.find(child,root2,labelPredicate);
        System.out.println(root1.label+","+root2.label);
        System.out.println(finder.ancestors);
        System.out.println(finder.found);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(child.label,finder.found.label);
    }
    @Test public void testFindNodeWithLabelPredicate() {
        for(MNode node1:list1) {
            System.out.println("node1: "+node1+", label: "+node1.label);
            MNode remote=new MNode(null);
            remote.label=node1.label;
            MNodeFinder finder=MNodeFinder.find(remote,root2,labelPredicate);
            System.out.println(finder.ancestors);
            System.out.println(finder.found);
            assertTrue(finder.ancestors.size()>0);
            assertEquals(node1.label,finder.found.label);
        }
    }
    String key="sgfExamleFromRedBean";
    String sgf=Parser.getSgfData(key);
    BiPredicate<MNode,MNode> equalsPredicate=(x,y)->x.equals(y);
    BiPredicate<MNode,MNode> labelPredicate=(x,y)->x.label.equals(y.label);
    MNode root1=MNode.restore(new StringReader(sgf));
    MNode root2=MNode.restore(new StringReader(sgf));
    {
        MNode.label(root1,new Longs());
        MNode.label(root2,new Longs());
    }
    List<MNode> list1=MakeList.toList(root1);
    List<MNode> list2=MakeList.toList(root2);
}
