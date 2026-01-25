package gui;
import static org.junit.Assert.*;
import org.junit.*;
import utilities.TestSupport;
public class TreeViewTestCase extends TestSupport {
    @Before public void setUp() throws Exception {
        //Init.first.restoreSystmeIO();
    }
    @After public void tearDown() throws Exception {}
    @Test public void testTreeView() throws Exception {
        TreeView myTreeView=TreeView.simple();
        assertNotNull(myTreeView.tree);
        assertNotNull(myTreeView.tree.getCellRenderer());
        assertTrue(myTreeView.tree.getCellRenderer() instanceof TreeView.NodeRenderer);
    }
}
