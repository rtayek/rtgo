package sgf;
import static org.junit.Assert.assertTrue;
import java.io.StringReader;
import java.util.function.BiPredicate;
import org.junit.*;
import model.MNodeAcceptor.MNodeFinder;
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
        assertTrue(finder.ancestors.size()>0);
    }
    @Test public void testFindWithLabelPredicate() {
        MNodeFinder finder=MNodeFinder.find(root1,root2,labelPredicate);
        System.out.println(root1.label+","+root2.label);
        System.out.println(finder.ancestors);
        assertTrue(finder.ancestors.size()>0);
    }
    @Test public void testFindFirstChildWithLabelPredicate() {
        MNodeFinder finder=MNodeFinder.find(root1.children.iterator().next(),root2,labelPredicate);
        System.out.println(root1.label+","+root2.label);
        System.out.println(finder.ancestors);
        assertTrue(finder.ancestors.size()>0);
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
}
