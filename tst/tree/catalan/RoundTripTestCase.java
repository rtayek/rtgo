package tree.catalan;
import static tree.catalan.RedBean.binary;
import org.junit.*;
import utilities.MyTestWatcher;
public class RoundTripTestCase extends AbstractRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    //ArrayList<Node<Character>> trees=new ArrayList<>();
    @Override @Before public void setUp() throws Exception {}
    @Override @After public void tearDown() throws Exception {}
    //Iterator<Character> iterator=new G2.Characters();
    //ArrayList<Node<Character>> trees=Generator.all(0,iterator,false);
    {
        // bRoot=trees.get(0);
        bRoot=binary();
    }
}
