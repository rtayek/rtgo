package sgf;
import java.io.File;
import java.util.*;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class SmallerParameterizedRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        //return ParameterArray.parameterize(Parser.sgfDataKeySet());
        return ParameterArray.parameterize(Parser.sgfTestData());
    }
    @Parameters public static Collection<Object[]> data() {
        String[] filenames=new String[] { //
                "reallyEmpty.sgf",
                "empty.sgf",
                "a.sgf", //
                "rtgo0false.sgf",
                "ray-SmartGo-2022-01-07.2.sgf",
                "recentogsgames/39312326-031-DonJuan-rtayek.sgf",
                "recentogsgames/39312326-026-DonJuan-rtayek.sgf",
        };
        String[] keys=new String[] {"justASemicolon","justSomeSemicolons","empty",};
        // use variable names above
        File[] files=new File[filenames.length];
        for(int i=0;i<filenames.length;i++) files[i]=new File("sgf",filenames[i]);
        List<Object> objects=new ArrayList<>();
        //objects.addAll(new ArrayList<>(Arrays.asList((Object[])(keys))));
        objects.addAll(new ArrayList<>(Arrays.asList((Object[])(files))));
        System.out.println(objects.iterator().next().getClass().getName());
        objects.add("reallyEmpty");
        Collection<Object[]> parameters=ParameterArray.parameterize(objects);
        for(Object[] parameterized:parameters) System.out.println(parameterized[0]+" "+parameterized[0].getClass());
        return ParameterArray.parameterize(objects);
    }
    public SmallerParameterizedRoundTripTestCase(Object key) { this.key=key; }
}
