package gui;
import java.util.Arrays;
import org.junit.*;
import io.Logging;
import sgf.*;
import utilities.MyTestWatcher;
public class TreeViewTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        //Init.first.restoreSystmeIO();
    }
    @After public void tearDown() throws Exception {}
    @Test public void testTreeView() throws Exception { TreeView myTreeView=TreeView.simple(); }
    @Test public void testTreeVie2() throws Exception { TreeView2 myTreeView=TreeView2.simple2(); }
    @Test public void testNode2Constructor() throws Exception {
        MNode node=new MNode(null);
        P p=P.B;
        String sgfCoordinates="aa";
        SgfProperty property=new SgfProperty(p,Arrays.asList(new String[] {sgfCoordinates}));
        node.sgfProperties().add(property);
        Logging.mainLogger.info("MNode node: "+node);
        Node2 node2=new Node2(node,null);
        Logging.mainLogger.info("MNode node2 "+node2);
        Logging.mainLogger.info(node2.sgfProperties().toString());
    }
}
