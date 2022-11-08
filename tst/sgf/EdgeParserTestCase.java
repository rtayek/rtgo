package sgf;
import java.io.File;
import java.util.*;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class EdgeParserTestCase extends AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> data() {
        String[] filenames=new String[] {"sgf/empty.sgf","sgf/reallyempty.sgf","sgf/saved.sgf"};
        String[] keys=new String[] {"justASemicolon","justSomeSemicolons","empty","twoEmpty","twoEmptyWithLinefeed",
                "eallyEmpty","emptyWithSemicolon","twoEmptyWithSemicolon",};
        File[] files=new File[filenames.length];
        for(int i=0;i<filenames.length;i++) files[i]=new File("sgf",filenames[i]);
        Collection<Object> objects=Arrays
                .asList((Object[])(files));
        //for(File file:files) objects.add(file);
        Collection<Object[]> parameters= ParameterArray.parameterize(objects);
        for(Object[] parameterized:parameters)
            System.out.println(parameterized[0]+" "+parameterized[0].getClass()
                    );
        System.out.println(parameters);
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
