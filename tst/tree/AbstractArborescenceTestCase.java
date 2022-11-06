package tree;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.TestRule;
import io.Logging;
import utilities.MyTestWatcher;
public abstract class AbstractArborescenceTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testToString() {}
    @Test public void testLeft() { assertNull(arborescence.left()); }
    @Test public void testRight() {
        assertNull(arborescence.right());
    }
    @Test public void testSiblings() {
        Logging.mainLogger.warning("arb: "+arborescence.siblings());
        assertTrue(arborescence.siblings().size()==0);
        Logging.mainLogger.warning("child: "+child.siblings());
        assertTrue(child.siblings().size()==2);
    }
    @Test public void testChildren() { assertTrue(arborescence.children().size()==0); }
    @Test public void testDescendents() {
        assertTrue(arborescence.descendents().size()==0);
        assertTrue(parent.descendents().size()==1);
        assertTrue(parent.descendents().contains(child));
    }
    @Test public void testAddSibling() {
        assertTrue(child.siblings().size()==1);
        assertTrue(child.siblings().contains(sibling));
        assertTrue(child.siblings().contains(child));
    }
    @Test public void testAddDescendant() { fail("Not yet implemented"); }
    @Test public void testAddChild() { assertTrue(parent.children().contains(child)); }
    @Test public void testPreOrder() { fail("Not yet implemented"); }
    @Test public void testInOrder() { fail("Not yet implemented"); }
    @Test public void testPostOrder() { fail("Not yet implemented"); }
    @Test public void testIsStrange() { fail("Not yet implemented"); }
    @Test public void testIsStrangeArborescence() { fail("Not yet implemented"); }
    @Test public void testToBinaryNode() { fail("Not yet implemented"); }
    @Test public void testToBinaryNodeArborescence() { fail("Not yet implemented"); }
    @Test public void testToMultiwayNode() { fail("Not yet implemented"); }
    @Test public void testToMultiwayNodeArborescence() { fail("Not yet implemented"); }
    Arborescence arborescence,parent,child,sibling;
}
