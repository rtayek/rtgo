package game;
import static org.junit.Assert.assertEquals;
import java.io.StringReader;
import java.util.List;
import org.junit.*;
import model.MNodeAcceptor.MakeList;
import model.Model;
import sgf.*;
public class GoToNodeTesCase {
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testGoToLocalNode() {
        // hanging now when we go the other way
        // used to work just fine.
        // the test below was should be going from any to any.
        //String sgfString=getSgfData("simpleWithVariations");
        //MNode mNode=restore(new StringReader(expectedSgf));
        //String sgfString=getSgfData("noVariation");
        String key="sgfExamleFromRedBean";
        String sgfString=Parser.getSgfData(key);
        //String sgfString=Parser.sgfExamleFromRedBean;
        System.out.println(sgfString);
        // maybe add a test that uses restore
        MNode root=MNode.quietLoad(new StringReader(sgfString));
        model.setRoot(root);
        List<MNode> list1=MakeList.toList(root);
        System.out.println(list1);
        List<MNode> list2=MakeList.toList(root);
        System.out.println(list1);
        list1.remove(0); // get rid of private property RT
        list2.remove(0); // get rid of private property RT
        // maybe we should not always remove?
        System.out.println(list1);
        System.out.println(list1);
        for(MNode node1:list1) {
            System.out.println("node1: "+node1);
            model.setRoot(root);
            for(MNode node2:list2) {
                System.out.println("testGoToNode() <<<<<<<<<<<<<");
                System.out.println("node1: "+node1);
                System.out.println("node2: "+node2);
                boolean ok=model.goToMNode(node1);
                if(!ok) System.out.println("go to node fails!");
                System.out.println("at node 1 "+node1);
                assertEquals(node1,model.currentNode());
                boolean theOtherWay=true;
                if(theOtherWay) {
                    System.out.println("try to go to node 2: "+node2);
                    boolean ok2=model.goToMNode(node2); // uses equals predicate.
                    if(!ok2) System.out.println("go to node fails!");
                    System.out.println("at node 2 "+node2);
                    assertEquals(node2,model.currentNode());
                }
                System.out.println("testGoToNode() >>>>>>>>>>>>>");
            }
        }
        //System.setOut(x);
    }
    Model model=new Model();
}
