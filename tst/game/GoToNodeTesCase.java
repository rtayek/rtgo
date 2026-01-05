package game;
import io.Logging;
import static org.junit.Assert.assertEquals;
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
        Logging.mainLogger.info(String.valueOf(sgfString));
        // maybe add a test that uses restore
        MNode root=SgfTestIo.quietLoadMNode(sgfString);
        model.setRoot(root);
        List<MNode> list1=MakeList.toList(root);
        Logging.mainLogger.info(String.valueOf(list1));
        List<MNode> list2=MakeList.toList(root);
        Logging.mainLogger.info(String.valueOf(list1));
        list1.remove(0); // get rid of private property RT
        list2.remove(0); // get rid of private property RT
        // maybe we should not always remove?
        Logging.mainLogger.info(String.valueOf(list1));
        Logging.mainLogger.info(String.valueOf(list1));
        for(MNode node1:list1) {
            Logging.mainLogger.info("node1: "+node1);
            model.setRoot(root);
            for(MNode node2:list2) {
                Logging.mainLogger.info("testGoToNode() <<<<<<<<<<<<<");
                Logging.mainLogger.info("node1: "+node1);
                Logging.mainLogger.info("node2: "+node2);
                boolean ok=model.goToMNode(node1);
                if(!ok) Logging.mainLogger.info("go to node fails!");
                Logging.mainLogger.info("at node 1 "+node1);
                assertEquals(node1,model.currentNode());
                boolean theOtherWay=true;
                if(theOtherWay) {
                    Logging.mainLogger.info("try to go to node 2: "+node2);
                    boolean ok2=model.goToMNode(node2); // uses equals predicate.
                    if(!ok2) Logging.mainLogger.info("go to node fails!");
                    Logging.mainLogger.info("at node 2 "+node2);
                    assertEquals(node2,model.currentNode());
                }
                Logging.mainLogger.info("testGoToNode() >>>>>>>>>>>>>");
            }
        }
        //System.setOut(x);
    }
    Model model=new Model();
}
