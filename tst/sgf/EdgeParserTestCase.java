package sgf;
import io.Logging;
import java.io.File;
import java.util.*;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class EdgeParserTestCase extends AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> data() {
        String[] filenames=new String[] { //
                //"empty.sgf", //
                // "reallyempty.sgf", //
                //"saved.sgf", //
                //"mf0false.sgf", //
                //"mf1false.sgf", //
                "mf0.sgf", //
                "mf1.sgf", //
                "smart0.sgf", //
                "smart1.sgf", //
                "rtgo0.sgf", //
                "rtgo1.sgf", //
        };
        String[] keys=new String[] {"justASemicolon","justSomeSemicolons","empty","twoEmpty","twoEmptyWithLinefeed",
                "reallyEmpty","emptyWithSemicolon","twoEmptyWithSemicolon",};
        // use variable names above
        File[] files=new File[filenames.length];
        for(int i=0;i<filenames.length;i++) files[i]=new File(Parser.sgfPath,filenames[i]);
        List<Object> objects=new ArrayList<>(Arrays.asList((Object[])(files)));
        //List<Object> objects=Arrays.asList((Object[])(files));
        Logging.mainLogger.info(String.valueOf(objects.iterator().next().getClass().getName()));
        objects.add("reallyEmpty");
        Collection<Object[]> parameters=ParameterArray.parameterize(objects);
        for(Object[] parameterized:parameters) Logging.mainLogger.info(parameterized[0]+" "+parameterized[0].getClass());
        return ParameterArray.parameterize(objects);
        // if(!badSgfFiles.contains(file)) objects.add(new Object[] {file});
        //private static void getSgfFiles(File dir,Set<Object[]> objects) {
        //getSgfFiles(new File("strangesgf/"),objects);
        //Set<Object[]> objects=new LinkedHashSet<>();
        //objects.addAll(sgfData());
        //getSgfFiles(new File("sgf/"),objects);
    }
    public EdgeParserTestCase(Object key) { this.key=key; }
}
