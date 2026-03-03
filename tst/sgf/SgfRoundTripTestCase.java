package sgf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Collection;
import java.util.List;
import java.io.StringWriter;
import com.tayek.util.io.FileIO;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import model.MNodeAcceptor.MakeList;
import model.Model;
import model.ModelIo;

@RunWith(Parameterized.class) public class SgfRoundTripTestCase extends AbstractSgfParserTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        return SgfTestHarness.parserParameters();
    }

    @Override protected String normalizeExpectedSgf(String rawSgf) {
        return SgfTestHarness.prepareExpectedSgf(key,rawSgf);
    }

    @Test public void testSgfSaveAndRestore() throws Exception {
        runIfRoundTrip(() -> SgfTestHarness.assertSgfSaveAndRestore(key,expectedSgf));
    }

    @Test public void testSgfRoundTrip() throws Exception {
        runIfRoundTrip(() -> SgfTestHarness.assertSgfRoundTrip(key,expectedSgf));
    }

    @Ignore @Test public void testSPreordergfRoundTrip() throws Exception {
        if(skipRoundTripTests()) return;
        if(expectedSgf==null) return;
        if(expectedSgf.equals("")) return;
        expectedSgf=SgfIo.prepareSgf(expectedSgf);
        String actualSgf=SgfNode.preorderRoundTrip(expectedSgf);
        SgfIo.logBadParentheses(expectedSgf,key,"ex");
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }

    @Test public void testRSgfoundTripeTwice() throws Exception {
        runIfRoundTrip(() -> SgfTestHarness.assertRoundTripTwice(key,expectedSgf));
    }

    @Test public void testSgfCannonical() throws Exception {
        runIfRoundTrip(() -> SgfTestHarness.assertSgfCannonical(key,expectedSgf));
    }

    @Test public void testMMNodeRoundTrip() throws Exception {
        runIfRoundTrip(() -> assertMNodeRoundTrip(SgfIo.MNodeSaveMode.standard,true));
    }

    @Test public void testMMNodeDirectRoundTrip() throws Exception {
        runIfRoundTrip(() -> assertMNodeRoundTrip(SgfIo.MNodeSaveMode.direct,false));
    }

    @Test public void testModelRT0NewWay() throws Exception {
        runIfRoundTrip(() -> {
            // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
            SgfTestHarness.assertModelRestoreAndSave(key,expectedSgf,new Model(""));
        });
    }

    @Test public void testModelRestoreAndSave() throws Exception {
        runIfRoundTrip(() -> {
            // then it does a restore and a save.
            // this one looks more complicated than is necessary.
            //the round trip above could be package if we removed it.
            SgfTestHarness.assertSgfRestoreAndSave(key,expectedSgf);
            // failing probably due to add new root problem
            SgfTestHarness.assertModelRestoreAndSaveWithExplicitModel(key,expectedSgf);
        });
    }

    @Test public void testLongRoundTrip() throws Exception {
        runIfRoundTrip(() -> SgfTestHarness.assertModelRoundTripToString(key,expectedSgf,
                model.ModelHelper.ModelSaveMode.sgfNode,false));
    }

    @Test public void testModelRestoreAndSave1() throws Exception {
        runIfRoundTrip(() -> {
            MNode root=MNode.restoreMNodes(FileIO.toReader(expectedSgf));
            SgfTestHarness.assertModelSaveFromMNode(key,expectedSgf,root);
        });
    }

    @Test public void testLongRoundTrip21() throws Exception {
        runIfRoundTrip(() -> SgfTestHarness.assertModelRoundTripToString(key,expectedSgf,
                model.ModelHelper.ModelSaveMode.sgfNodeChecked,true));
    }

    @Test public void testCannonicalRoundTripTwice() throws Exception {
        runIfRoundTrip(() -> SgfTestHarness.assertCanonicalRoundTripTwice(key,expectedSgf));
    }

    @Test public void testCheckBoardInRoot() throws Exception {
        runIfRoundTrip(() -> {
            String sgf=rawSgf!=null?rawSgf:expectedSgf;
            SgfTestHarness.assertCheckBoardInRoot(key,sgf);
        });
    }

    @Test public void testThatGeneralTreeRootIsAentinel() throws Exception {
        runIfRoundTrip(() -> {
            // this will depend on whether the add new root switch is on.
            SgfNode games=SgfTestHarness.restoreExpectedSgf(expectedSgf,key,true);
            MNode root=MNode.toGeneralTree(games);
            // this may not be present
            // check the add new root flag in mnode.
            if(root!=null) {
                SgfProperty property=root.sgfProperties().get(0);
                assertEquals(P.RT,property.p());
            }
        });
    }

    @Test public void testSaveMultupleGames() throws Exception {
        runIfRoundTrip(() -> {
            Model model=new Model();
            ModelIo.restoreModel(model,FileIO.toReader(fixtureSgf()));
            boolean hasMultipleGames=model.root().children().size()>1;
            StringWriter sgfWriter=new StringWriter();
            ModelIo.saveModel(model,sgfWriter);
            String sgfString=sgfWriter.toString();
            boolean containsRTNode=sgfString.contains("RT[Tgo root]");
            // when games.right!=null ==> multiple games
            // and we end up with an RT in the sgf!
            // assertEquals(hasMultipleGames,containsRTNode);
            // but we do not want the RT node in the sgf!
            // need to check the add new root switch.
            // 11/28/22 seems like we are doing this somewhere else.
        });
    }

    @Ignore @Test public void testLeastCommonAncester() { // slow, so ignore for now.
        if(skipRoundTripTests()) return;
        // seems to be working for multiple games
        MNode root=MNode.restoreMNodes(FileIO.toReader(expectedSgf));
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

    @Test public void testFinder() throws Exception {
        runIfRoundTrip(() -> SgfTestHarness.assertFinderMatches(SgfTestHarness.restoreExpectedSgf(expectedSgf,key,true)));
    }

    @Test public void testMultipleGames() throws Exception { // how does it do that?
        runIfRoundTrip(() -> {
            StringWriter writer=new StringWriter();
            ModelIo.restoreAndSaveSGF(FileIO.toReader(fixtureSgf()),writer);
            String actualSgf=writer.toString();
            // assertFalse(expectedSgf.contains(P.RT.toString()));
            // why would we expect this?
        });
    }

    private void assertMNodeRoundTrip(SgfIo.MNodeSaveMode saveMode,boolean logExpected) {
        SgfTestHarness.assertMNodeRoundTrip(key,expectedSgf,saveMode,logExpected);
    }

    private String fixtureSgf() {
        return rawSgf!=null?rawSgf:expectedSgf;
    }

    private boolean skipRoundTripTests() {
        return rawInput||expectedSgf==null;
    }
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
    private void runIfRoundTrip(ThrowingRunnable action) throws Exception {
        if(skipRoundTripTests()) return;
        action.run();
    }
}


