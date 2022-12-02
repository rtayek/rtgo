package tree.catalan;
import java.util.*;
import org.junit.*;
import tree.catalan.G2.*;
import utilities.MyTestWatcher;
public class RoundTripTestCase extends AbstractRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    //ArrayList<Node<Character>> trees=new ArrayList<>();
    @Override @Before public void setUp() throws Exception {}
    @Override @After public void tearDown() throws Exception {}
    Iterator<Character> iterator=new G2.Characters();
    ArrayList<Node<Character>> trees=Generator.all(0,iterator,false);
    {
        bRoot=trees.get(0);
    }
}
