package tree;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.TestRule;
import utilities.MyTestWatcher;
@Ignore public class MultiwayTestCase extends AbstractArborescenceTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {}
    @Override @After public void tearDown() throws Exception {}
    @Test public void testParent() {
        assertNull(arborescence.parent());
        assertNotNull(child.parent());
        assertEquals(parent.children().iterator().next(),child);
        assertEquals(child.parent(),parent);
    }
    {
        arborescence=new MultiNodeImpl(null);
        parent=new MultiNodeImpl(null);
        child=new MultiNodeImpl(null);
        sibling=new MultiNodeImpl(null);
        parent.addChild(child);
        parent.addChild(sibling);
    }
}
