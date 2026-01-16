package sgf;
import static model.MNodeAcceptor.MNodeFinder .*;
import static org.junit.Assert.*;
import java.util.List;
import java.util.function.BiPredicate;
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
        assertNotFound(mNode,root2);
    }
    @Test public void testFindSelfWithEqualsPredicate() {
        assertFound(root1,root1,equalsPredicate);
    }
    @Test public void testFindSelfWithLabelPredicate() {
        assertFoundWithLabel(root1,root1);
    }
    @Ignore   @Test public void testFindRootWithEqualsPredicate() {
        // should fail because we use equals which is just ==?
        MNodeFinder finder=MNodeFinder.find(root1,root2,equalsPredicate);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(root1.label(),finder.found.label()); // might not always be labeled?
    }
    @Test public void testFindRootWithLabelPredicate() {
        assertFoundWithLabel(root1,root2);
    }
    @Test public void testFindFirstChildWithLabelPredicate() {
        MNode child=root1.children().iterator().next();
        assertFoundWithLabel(child,root2);
    }
    @Test public void testFindNodeWithLabelPredicate() {
        for(MNode node1:list1) {
            MNode remote=new MNode(null,node1.sgfProperties());
            remote.setLabel(node1.label());
            assertFoundWithLabel(remote,root2);
        }
    }
    private static MNodeFinder assertFound(MNode target,MNode root,BiPredicate<MNode,MNode> predicate) {
        MNodeFinder finder=MNodeFinder.find(target,root,predicate);
        assertTrue(finder.ancestors.size()>0);
        return finder;
    }
    private static void assertFoundWithLabel(MNode target,MNode root) {
        MNodeFinder finder=assertFound(target,root,labelPredicate);
        assertEquals(target.label(),finder.found.label());
    }
    private static void assertNotFound(MNode target,MNode root) {
        MNodeFinder finder=MNodeFinder.find(target,root,equalsPredicate);
        assertTrue(finder.ancestors.size()==0);
    }
    MNode root1;
    MNode root2;
    List<MNode> list1;
    List<MNode> list2;
}
