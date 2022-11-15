package tree;
import static org.junit.Assert.*;
import java.util.Iterator;
import org.junit.*;
import utilities.MyTestWatcher;
public class MultiwayTestCase extends AbstractArborescenceTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {}
    @Override @After public void tearDown() throws Exception {}
    @Test public void testParent() {
        assertNull(arborescence.parent());
        assertNotNull(child.parent());
        Iterator<Arborescence> i=parent.children().iterator();
        assertEquals(i.next(),child);
        assertEquals(child.parent(),parent);
        assertEquals(i.next(),sibling);
    }
    {
        arborescence=new MultiNodeImpl(null);
        parent=new MultiNodeImpl(null);
        child=new MultiNodeImpl((MultiNodeImpl)parent);
        sibling=new MultiNodeImpl((MultiNodeImpl)parent);
        parent.addChild(child);
        parent.addChild(sibling);
    }
}
