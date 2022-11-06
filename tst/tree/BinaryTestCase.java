package tree;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.TestRule;
import utilities.MyTestWatcher;
@Ignore public class BinaryTestCase extends AbstractArborescenceTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {}
    @Override @After public void tearDown() throws Exception {}
    @Test(expected=UnsupportedOperationException.class) public void testParentThrows() {
        assertNull(arborescence.parent());
    }
    @Test public void testParent() {
        assertEquals(parent.children().iterator().next(),child);
    }
    {
        arborescence=new BinaryNodeImpl(null,null);
        parent=new BinaryNodeImpl(null,null);
        child=new BinaryNodeImpl(null,null);
        parent.addChild(child);
        sibling=new BinaryNodeImpl(null,null);
        child.addSibling(sibling);
    }
}
