package sgf;
import static io.Logging.parserLogger;
import static org.junit.Assert.fail;
import static utilities.Utilities.addFiles;
import java.io.*;
import java.util.*;
import org.junit.*;
import io.IO;
import utilities.MyTestWatcher;
@Ignore public class WierdSGFTestCase {
    // these take a long time when tst/ is run
    // check to see if they take a long time when run by themselves.
    // they do not take a long time.
    // they mumble a lot, so ignoring for now.
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // parameterize this!
    @Test public void testWierd() throws Exception {
        if(!strange.exists()) fail(strange+" does not exits!");
        List<File> files=addFiles(null,strange);
        if(files.size()==0) fail("no files!");
        fail=false;
        for(File file:files) try {
            boolean ok=Parser.sgfRoundTripTwice(IO.toReader(file));
            if(!ok)
                System.out.println(file+" fails!");
        } catch(Exception e) {
            parserLogger.warning(this+" caught: "+e);
        }
    }
    @Test public void testFirstNodeOfWierd() throws Exception {
        if(!strange.exists()) fail(strange+" does not exits!");
        List<File> files=addFiles(null,strange);
        if(files.size()==0) fail("no files!");
        fail=false;
        PrintStream old=System.err;
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        // forgot why i was doing this. maybe i need to
        // change some of the logger stuff back to print on err.
        List<SgfNode> all=new ArrayList<>();
        for(File file:files) try {
            Parser parser=new Parser();
            SgfNode games=parser.parse(IO.toReader(file));
            all.add(games);
        } catch(Exception e) {
            System.out.println(this+" caught: "+e); //
        }
        System.setErr(old);
        parserLogger.warning(all.size()+" games in "+strange);
    }
    boolean fail=true;
    static final File strange=new File("strangesgf/");
}
