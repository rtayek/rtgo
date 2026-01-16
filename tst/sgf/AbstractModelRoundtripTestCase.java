package sgf;
import org.junit.*;
public abstract class AbstractModelRoundtripTestCase extends AbstractMNodeRoundTripTestCase {
    @Test public void testModelRT0() throws Exception {
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        SgfModelRoundTripHarness.assertModelRestoreAndSave(key,expectedSgf,true);
    }
    @Test public void testModelRT0NewWay() throws Exception {
        // fails with (;RT[Tgo root];FF[4]C[root](;C[a];C[b](;C[c])(;C[d];C[e]))(;C[f](;C[g];C[h];C[i])(;C[j])))
        SgfModelRoundTripHarness.assertModelRestoreAndSave(key,expectedSgf,false);
    }
    @Test public void testModelRestoreAndSave() throws Exception {
        // then it does a restore and a save.
        // this one looks more complicated than is necessary.
        //the round trip above could be package if we removed it.
        SgfModelRoundTripHarness.assertSgfRestoreAndSave(key,expectedSgf);
        // failing probably due to add new root problem
        SgfModelRoundTripHarness.assertModelRestoreAndSaveWithExplicitModel(key,expectedSgf);
    }
    @Test public void testLongRoundTrip() throws Exception {
        SgfModelRoundTripHarness.assertModelRoundTripToString(key,expectedSgf,model.ModelHelper.ModelSaveMode.sgfNode,false);
    }
    @Test public void testModelRestoreAndSave1() throws Exception {
        MNode root=restoreExpectedMNode();
        SgfModelRoundTripHarness.assertModelSaveFromMNode(key,expectedSgf,root);
    }
    @Test public void testLongRoundTrip21() throws Exception {
        SgfModelRoundTripHarness.assertModelRoundTripToString(key,expectedSgf,model.ModelHelper.ModelSaveMode.sgfNodeChecked,true);
    }
    @Test public void testCannonicalRoundTripTwice() {
        SgfModelRoundTripHarness.assertCanonicalRoundTripTwice(key,expectedSgf);
    }
}
