package sgf;
import io.Logging;
import java.io.File;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
// https://stackoverflow.com/questions/14082004/create-multiple-parameter-sets-in-one-parameterized-class-junit
@Ignore @RunWith(Parameterized.class) public class SmallerParameterizedRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    public static File[] someFiles(String dir) {
        String[] filenames=new String[] { //
                //"reallyEmpty.sgf", //
                //"empty.sgf",
                //"rtgo0false.sgf",
                //"ff4.sgf",
                "smallestogs.sgf", // fails
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
        String dir=Parser.sgfPath;
        dir="ogs";
        if(true) { // all files in dir and subdirectoroes.
            objects.addAll(Parser.sgfFiles(dir));
        }
        if(false) {
            File[] files=someFiles(dir); // some files in dir. not recursive.
            Logging.mainLogger.info("adding: "+Arrays.asList((Object[])(files))+" files.");
            List<Object> fileList=new ArrayList<>(Arrays.asList((Object[])(files)));
            for(File file:files) if(!file.exists()) {
                Logging.mainLogger.info(file+" does not exist!");
                fileList.remove(file);
            }
            objects.addAll(fileList);
        }
        if(false) {
            String[] keys=new String[] {
                    //"reallyEmpty",
            };
            objects.addAll(new ArrayList<>(Arrays.asList((Object[])(keys))));
        }
        Logging.mainLogger.info(objects.size()+" keys");
        return ParameterArray.parameterize(objects);
    }
    public SmallerParameterizedRoundTripTestCase(Object key) { this.key=key; watcher.key=key; }
}
