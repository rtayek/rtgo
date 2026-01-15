package sgf;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class) public class EdgeParserTestCase extends AbstractSgfParserTestCase {
    @Parameters public static Collection<Object[]> data() {
        return SgfTestSupport.edgeParserParameters();
        // if(!badSgfFiles.contains(file)) objects.add(new Object[] {file});
        //private static void getSgfFiles(File dir,Set<Object[]> objects) {
        //getSgfFiles(new File("strangesgf/"),objects);
        //Set<Object[]> objects=new LinkedHashSet<>();
        //objects.addAll(sgfData());
        //getSgfFiles(new File("sgf/"),objects);
    }
}
