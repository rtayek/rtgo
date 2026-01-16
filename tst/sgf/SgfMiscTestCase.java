package sgf;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.junit.Test;
import model.Model;
import io.Logging;

public class SgfMiscTestCase extends AbstractWatchedTestCase {
    @Test public void testLizzie() {
        String id="LZ";
        P p=P.idToP.get(id);
        assertNotNull(p);
    }

    @Test public void testOblong() {
        File file=new File("ogs/lecoblong.sgf");
        String expectedSgf=SgfTestSupport.loadExpectedSgf(file);
        MNode games=SgfTestIo.restoreMNode(expectedSgf);
        model=new Model("oblong");
        model.setRoot(games); // does this really trash everything correctly?
        model.down(0);
    }

    @Test public void testNoMoves() throws IOException {
        assertRoundTrip("new way",false);
    }

    @Test public void testNOneovesTheOldWay() throws IOException {
        assertRoundTrip("old way",true);
    }

    private String dtrt(Model m) {
        String actual=TestIoSupport.restoreAndSave(m,sgf,restored->{
            Logging.mainLogger.info("restored, root: "+restored.root().toString());
            boolean hasRT=Model.hasRT(restored.root());
            assertTrue(hasRT);
        });
        Logging.mainLogger.info("saved: "+actual);
        return actual;
    }

    private void assertRoundTrip(String name,boolean oldWay) throws IOException {
        Logging.setLevels(Level.INFO);
        Model m=new Model(name,oldWay);
        final String actual=dtrt(m);
        assertEquals(sgf,actual);
    }

    Model model;
    final String sgf="(;FF[4]GM[1]AP[RTGO]C[comment];B[as])";
}
