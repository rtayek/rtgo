package sgf;
import java.io.File;
import java.util.*;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
// https://stackoverflow.com/questions/14082004/create-multiple-parameter-sets-in-one-parameterized-class-junit
@RunWith(Parameterized.class) public class SmallerParameterizedRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public static File[] someFiles(String dir) {
        String[] filenames=new String[] { //
                //"reallyEmpty.sgf", //
                //"empty.sgf",
                //"rtgo0false.sgf",
                //"ff4.sgf",
                "smallestogs.sgf",
                //"smallestrt.sgf",
                //"smallestrt2.sgf",
                //"ray-SmartGo-2022-01-07.2.sgf",
                //"recentogsgames/39312326-031-DonJuan-rtayek.sgf",
                //"recentogsgames/39312326-026-DonJuan-rtayek.sgf",
                //
        };
        // use variable names above
        File[] files=new File[filenames.length];
        for(int i=0;i<filenames.length;i++) files[i]=new File(dir,filenames[i]);
        // files are made from filenames
        return files;
    }
    @Parameters public static Collection<Object[]> parameters() {
        Set<Object> objects=new LinkedHashSet<>();
        //objects.addAll(sgfDataKeySet());
        String dir="sgf";
        dir="sgf/recentogsgames";
        // some problems here. can not find the files in recent!
        if(true) { // all files in dir and subdirectoroes.
            objects.addAll(Parser.sgfFiles(dir));
        }
        if(true) {
            File[] files=someFiles(dir); // some files in dir. not recursive.
            System.out.println("adding: "+Arrays.asList((Object[])(files))+" files.");
            List<Object> fileList=new ArrayList<>(Arrays.asList((Object[])(files)));
            for(File file:files) if(!file.exists()) { System.out.println(file+" does not exist!"); fileList.remove(file); }
            objects.addAll(fileList);
        }
        if(false) {
            String[] keys=new String[] {
                    //"reallyEmpty",
            };
            objects.addAll(new ArrayList<>(Arrays.asList((Object[])(keys))));
        }
        System.out.println(objects.size()+" keys");
        return ParameterArray.parameterize(objects);
    }
    public SmallerParameterizedRoundTripTestCase(Object key) { this.key=key; }
}
