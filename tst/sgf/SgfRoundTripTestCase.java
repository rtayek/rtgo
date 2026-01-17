package sgf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Collection;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import model.MNodeAcceptor.MakeList;
import model.Model;

@RunWith(Parameterized.class) public class SgfRoundTripTestCase extends AbstractSgfParserTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        return SgfHarness.parserParameters();
    }

    @Override protected String normalizeExpectedSgf(String rawSgf) {
        return SgfHarness.prepareExpectedSgf(key,rawSgf);
    }

    @Test public void testSgfSaveAndRestore() throws Exception {
        if(skipRoundTripTests()) return;
        SgfHarness.assertSgfSaveAndRestore(key,expectedSgf);
    }

    @Test public void testSgfRoundTrip() throws Exception {
        if(skipRoundTripTests()) return;
        SgfHarness.assertSgfRoundTrip(key,expectedSgf);
    }

    @Ignore @Test public void testSPreordergfRoundTrip() throws Exception {
        if(skipRoundTripTests()) return;
        if(expectedSgf==null) return;
        if(expectedSgf.equals("")) return;
        expectedSgf=SgfHarness.prepareSgf(expectedSgf);
        String actualSgf=SgfNode.preorderRouundTrip(expectedSgf);
        SgfHarness.logBadParentheses(expectedSgf,key,"ex");
        SgfHarness.assertPreparedEquals(key,expectedSgf,actualSgf);
    }

    @Test public void testRSgfoundTripeTwice() throws Exception {
        if(skipRoundTripTests()) return;
        SgfHarness.assertRoundTripTwice(key,expectedSgf);
    }

    @Test public void testSgfCannonical() {
        if(skipRoundTripTests()) return;
        SgfHarness.assertSgfCannonical(key,expectedSgf);
    }

    @Test public void testMMNodeRoundTrip() throws Exception {
        if(skipRoundTripTests()) return;
        assertMNodeRoundTrip(SgfRoundTrip.MNodeSaveMode.standard,true);
    }

    @Test public void testMMNodeDirectRoundTrip() throws Exception {
        if(skipRoundTripTests()) return;
        assertMNodeRoundTrip(SgfRoundTrip.MNodeSaveMode.direct,false);
    }

    @Test public void testModelRT0() throws Exception {
        if(skipRoundTripTests()) return;
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        SgfHarness.assertModelRestoreAndSave(key,expectedSgf,true);
    }

    @Test public void testModelRT0NewWay() throws Exception {
        if(skipRoundTripTests()) return;
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        SgfHarness.assertModelRestoreAndSave(key,expectedSgf,false);
    }

    @Test public void testModelRestoreAndSave() throws Exception {
        if(skipRoundTripTests()) return;
        // then it does a restore and a save.
        // this one looks more complicated than is necessary.
        //the round trip above could be package if we removed it.
        SgfHarness.assertSgfRestoreAndSave(key,expectedSgf);
        // failing probably due to add new root problem
        SgfHarness.assertModelRestoreAndSaveWithExplicitModel(key,expectedSgf);
    }

    @Test public void testLongRoundTrip() throws Exception {
        if(skipRoundTripTests()) return;
        SgfHarness.assertModelRoundTripToString(key,expectedSgf,model.ModelHelper.ModelSaveMode.sgfNode,false);
    }

    @Test public void testModelRestoreAndSave1() throws Exception {
        if(skipRoundTripTests()) return;
        MNode root=restoreExpectedMNode();
        SgfHarness.assertModelSaveFromMNode(key,expectedSgf,root);
    }

    @Test public void testLongRoundTrip21() throws Exception {
        if(skipRoundTripTests()) return;
        SgfHarness.assertModelRoundTripToString(key,expectedSgf,model.ModelHelper.ModelSaveMode.sgfNodeChecked,true);
    }

    @Test public void testCannonicalRoundTripTwice() {
        if(skipRoundTripTests()) return;
        SgfHarness.assertCanonicalRoundTripTwice(key,expectedSgf);
    }

    @Test public void testCheckBoardInRoot() {
        if(skipRoundTripTests()) return;
        String sgf=rawSgf!=null?rawSgf:expectedSgf;
        SgfHarness.assertCheckBoardInRoot(key,sgf);
    }

    @Test public void testThatGeneralTreeRootIsAentinel() {
        if(skipRoundTripTests()) return;
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
        if(skipRoundTripTests()) return;
        Model model=SgfHarness.restoreNew(fixtureSgf());
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
        if(skipRoundTripTests()) return;
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
        if(skipRoundTripTests()) return;
        SgfHarness.assertFinderMatches(restoreExpectedSgf());
    }

    @Test public void testMultipleGames() { // how does it do that?
        if(skipRoundTripTests()) return;
        String actualSgf=SgfHarness.restoreAndSave(fixtureSgf());
        // assertFalse(expectedSgf.contains(P.RT.toString()));
        // why would we expect this?
    }

    private void assertMNodeRoundTrip(SgfRoundTrip.MNodeSaveMode saveMode,boolean logExpected) {
        SgfHarness.assertMNodeRoundTrip(key,expectedSgf,saveMode,logExpected);
    }

    private String fixtureSgf() {
        return rawSgf!=null?rawSgf:expectedSgf;
    }

    private boolean skipRoundTripTests() {
        return rawInput||expectedSgf==null;
    }
}

