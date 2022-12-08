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
        //node: 10, tree: 8360
        Set<Object> objects=new LinkedHashSet<>();
        int[] x=new int[] {0,0};
        for(int nodes=0;nodes<=7;++nodes) {
            long trees=Catalan.catalan(nodes);
            for(int tree=0;tree<trees;++tree) {
                x=new int[] {nodes,tree};
                objects.add(x);
            }
        }
        return ParameterArray.parameterize(objects);
    }
    public ParameterizedNodeRoundTripTestCase(int[] x) {
        nodes=x[0];
        int tree=x[1];
        watcher.key=key="node: "+nodes+", tree: "+tree;
        Iterator<Long> iterator=new G2.Longs();
        boolean useMap=false;
        ArrayList<Node<Long>> trees=G2.Generator.one(nodes,iterator,useMap);
        bRoot=trees.get(tree);
    }
    int nodes;
}
