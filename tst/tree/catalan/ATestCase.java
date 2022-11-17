package tree.catalan;
import static tree.catalan.Node.allBinaryTrees;
import java.util.ArrayList;
import org.junit.*;
import utilities.*;
public class ATestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @BeforeClass public static void setUpBeforeClass() throws Exception { MyTestWatcher.defaultVerbosity=true; }
    @Test public void test0() {
        Node.usingMap2=true;
        watcher.verbosity=true;
        Node.map.clear();
        Node.map2.clear();
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(0,data);
        System.out.println("trees: "+trees);
        trees=allBinaryTrees(1,data);
        System.out.println("trees: "+trees);
        trees=allBinaryTrees(2,data);
        System.out.println("trees: "+trees);
    }
}
