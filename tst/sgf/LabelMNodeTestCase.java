package sgf;
import static model.MNodeAcceptor.MNodeFinder .*;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.*;
import model.MNodeAcceptor.*;
import utilities.Iterators.Longs;
public class LabelMNodeTestCase extends AbstractSgfFixtureTestCase implements RedBeanKeyed {
    @Before public void setUpLabel() throws Exception {
        root1=restoreExpectedMNode();
        root2=restoreExpectedMNode();
        MNode.label(root1,new Longs());
        MNode.label(root2,new Longs());
        list1=MakeList.toList(root1);
        list2=MakeList.toList(root2);
    }
    @Test public void testNodeNotInTree() {
        MNode mNode=new MNode(null);
        MNodeFinder finder=MNodeFinder.find(mNode,root2,equalsPredicate);
        assertTrue(finder.ancestors.size()==0);
    }
    @Test public void testFindSelfWithEqualsPredicate() {
        MNodeFinder finder=MNodeFinder.find(root1,root1,equalsPredicate);
        assertTrue(finder.ancestors.size()>0);
    }
    @Test public void testFindSelfWithLabelPredicate() {
        MNodeFinder finder=MNodeFinder.find(root1,root1,labelPredicate);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(root1.label(),finder.found.label());
    }
    @Ignore   @Test public void testFindRootWithEqualsPredicate() {
        // should fail because we use equals which is just ==?
        MNodeFinder finder=MNodeFinder.find(root1,root2,equalsPredicate);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(root1.label(),finder.found.label()); // might not always be labeled?
    }
    @Test public void testFindRootWithLabelPredicate() {
        MNodeFinder finder=MNodeFinder.find(root1,root2,labelPredicate);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(root1.label(),finder.found.label());
    }
    @Test public void testFindFirstChildWithLabelPredicate() {
        MNode child=root1.children().iterator().next();
        MNodeFinder finder=MNodeFinder.find(child,root2,labelPredicate);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(child.label(),finder.found.label());
    }
    @Test public void testFindNodeWithLabelPredicate() {
        for(MNode node1:list1) {
            MNode remote=new MNode(null,node1.sgfProperties());
            remote.setLabel(node1.label());
            MNodeFinder finder=MNodeFinder.find(remote,root2,labelPredicate);
            assertTrue(finder.ancestors.size()>0);
            assertEquals(node1.label(),finder.found.label());
        }
    }
    MNode root1;
    MNode root2;
    List<MNode> list1;
    List<MNode> list2;
}
