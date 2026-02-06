package tree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.tayek.util.core.Iterators.Longs;
@RunWith(Parameterized.class) public class ParameterizedNodeRoundTripTestCase extends AbstractRoundTripTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        List<Object[]> parameters=new ArrayList<>();
        parameters.add(new Object[] {"red bean sample",redBeanSample()});
        for(int nodes=0;nodes<=7;++nodes) {
            long treeCount=Catalan.catalan(nodes);
            Iterator<Long> iterator=new Longs();
            boolean useMap=false;
            ArrayList<Node<Long>> trees=G2.Generator.one(nodes,iterator,useMap);
            for(int tree=0;tree<treeCount;++tree) {
                parameters.add(new Object[] {"node: "+nodes+", tree: "+tree,trees.get(tree)});
            }
        }
        return parameters;
    }
    public ParameterizedNodeRoundTripTestCase(String label,Node<Long> root) {
        watcher.key=key=label;
        bRoot=root;
    }
    private static Node<Long> redBeanSample() {
        Node<Character> binary=RedBean.binary();
        Iterator<Long> iterator=new Longs();
        return Node.reLabelCopy(binary,iterator);
    }
}
