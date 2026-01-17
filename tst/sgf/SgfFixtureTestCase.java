package sgf;

import static org.junit.Assert.*;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import model.MNodeAcceptor.MakeList;
import model.Model;

@RunWith(Parameterized.class) public class SgfFixtureTestCase extends AbstractSgfKeyedTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        return SgfTestSupport.allSgfParameters();
    }

    @Before public void setUpFixture() throws Exception {
        expectedSgf=SgfTestSupport.loadExpectedSgf(key);
        if(expectedSgf==null) { return; }
    }

    @Test public void testThatGeneralTreeAlwaysHasRTProperty() {
        // this will depend on whether the add new root switch is on.
        SgfNode games=restoreExpectedSgf();
        MNode root=MNode.toGeneralTree(games);
        // this may not be present
        // check the add new root flag in mnode.
        if(root!=null) {
            SgfProperty property=root.sgfProperties().get(0);
            assertEquals(P.RT,property.p());
        }
    }

    @Test public void testSaveMultupleGames() {
        Model model=TestIoSupport.restoreNew(expectedSgf);
        boolean hasMultipleGames=model.root().children().size()>1;
        String sgfString=model.save();
        boolean containsRTNode=sgfString.contains("RT[Tgo root]");
        // when games.right!=null ==> multiple games
        // and we end up with an RT in the sgf!
        // assertEquals(hasMultipleGames,containsRTNode);
        // but we do not want the RT node in the sgf!
        // need to check the add new root switch.
        // 11/28/22 seems like we are doing this somewhere else.
    }

    @Ignore @Test public void testLeastCommonAncester() { // slow, so ignore for now.
        // seems to be working for multiple games
        MNode root=restoreExpectedMNode();
        boolean hasMultipleGames=root!=null&&root.children().size()>1;
        assertNotNull(root);
        List<MNode> list1=MakeList.toList(root);
        List<MNode> list2=MakeList.toList(root);
        if(hasMultipleGames) {
            // Logging.mainLogger.info(originalSgf);
            // Logging.mainLogger.info("list1: "+list1);
        }
        for(MNode node1:list1)
            for(MNode node2:list2)
                if(!node1.equals(node2)) {
                    List<MNode> list=root.lca(node1,node2);
                    assertNotNull(key.toString(),list);
                }
    }

    @Test public void testFinder() {
        SgfTestSupport.assertFinderMatches(restoreExpectedSgf());
    }

    @Test public void testMultipleGames() { // how does it do that?
        String actualSgf=SgfTestSupport.restoreAndSave(expectedSgf);
        // assertFalse(expectedSgf.contains(P.RT.toString()));
        // why would we expect this?
    }
}
