package tree;
import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import utilities.MyTestWatcher;
public class BinaryNodeParentTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    void checkParent(Arborescence node,Arborescence parent) {
        assertEquals(node.parent(),parent);
        for(Arborescence child:node.children()) checkParent(child,parent);
    }
}
