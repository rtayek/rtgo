package sgf;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.*;
import sgf.combine.Combine;
@Ignore public class KogosJosekiDictionaryTestCase extends AbstractIgnoreMoveAndSetupFlagsTestCase {
    @Test public void testFiles() throws Exception {
        File file=SgfTestSupport.firstExistingFile(
                new File(Combine.pathToHere,filename),
                new File("sgf",filename)
        );
        if(file==null) {
            assertTrue(filename+" not found",false);
            return;
        }
        boolean ok=SgfTestSupport.roundTripTwiceWithLogging(file);
        assertTrue(ok);
    }
    static final String filename="KogosJosekiDictionary.sgf";
}
