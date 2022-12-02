package tree.catalan;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import tree.catalan.G2.Node;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedNodeRoundTripTestCase extends AbstractRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {}
    @Override @After public void tearDown() throws Exception {}
    @Parameters public static Collection<Object[]> parameters() {
        Set<Object> objects=new LinkedHashSet<>();
        int[] x=new int[] {0,0};
        objects.add(x);
        x=new int[] {1,0};
        objects.add(x);
        x=new int[] {2,0};
        objects.add(x);
        x=new int[] {2,1};
        objects.add(x);
        x=new int[] {3,0};
        objects.add(x);
        x=new int[] {3,1};
        objects.add(x);
        x=new int[] {3,2};
        objects.add(x);
        x=new int[] {3,3};
        objects.add(x);
        x=new int[] {3,4};
        objects.add(x);
        return ParameterArray.parameterize(objects);
    }
    public ParameterizedNodeRoundTripTestCase(int[] x) {
        nodes=x[0];
        int n=x[1];
        Iterator<Character> iterator=new G2.Characters();
        ArrayList<Node<Character>> trees=G2.Generator.all(nodes,iterator,false);
        bRoot=trees.get(n);
    }
    int nodes;
}
