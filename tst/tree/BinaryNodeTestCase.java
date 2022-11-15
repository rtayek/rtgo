package tree;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import utilities.MyTestWatcher;
public class BinaryNodeTestCase extends AbstractArborescenceTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {}
    @Override @After public void tearDown() throws Exception {}
    @Test public void testParentThrows() {
        //assertNull(arborescence.parent());
    }
    @Test public void testParent() { assertEquals(parent.children().iterator().next(),child); }
    { /*
      arborescence=new MultiNodeImpl(null);
      parent=new MultiNodeImpl(null);
      child=new MultiNodeImpl((MultiNodeImpl)parent);
      sibling=new MultiNodeImpl((MultiNodeImpl)parent);
      parent.addChild(child);
      parent.addChild(sibling);
     */
    }
    {
        arborescence=new BinaryNodeImpl(null,null,null);
        parent=new BinaryNodeImpl(null,null,null);
        child=new BinaryNodeImpl(null,null,parent);
        sibling=new BinaryNodeImpl(null,null,parent);
        {
            System.out.println("pcs: "+parent+" "+child+" "+sibling);
        }
        System.out.println("before add child in init: "+parent.children());
        parent.addChild(child);
        System.out.println("aftere add child in init: "+parent.children());
        child.addSibling(sibling);
        System.out.println("added sib: "+parent.left().right());
        System.out.println("aftere add sibling in init: "+parent.children());
    }
}
