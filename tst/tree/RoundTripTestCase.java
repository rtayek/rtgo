package tree;
import static tree.Node.reLabelCopy;
import static tree.RedBean.binary;
import java.util.*;
import org.junit.*;
import tree.G2.Generator;
import utilities.MyTestWatcher;
public class RoundTripTestCase extends AbstractRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    //ArrayList<Node<Character>> trees=new ArrayList<>();
    @Override @Before public void setUp() throws Exception {}
    @Override @After public void tearDown() throws Exception {}
    Iterator<Long> iterator=new G2.Longs();
    ArrayList<Node<Long>> trees=Generator.one(10,iterator,false);
    {
        // bRoot=trees.get(0);
        //  Node<Long> bRoot;
        //bRoot=trees.get(8420);
        key="red bean sample";
        Node<Character> binary=binary();
        Iterator<Long> i=new G2.Longs();
        bRoot=reLabelCopy(binary,i);
        System.out.println("relabeled bRoot:");
        //G2.print(bRoot,"");
    }
}
