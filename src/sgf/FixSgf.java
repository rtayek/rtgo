package sgf;
import static io.Logging.parserLogger;
import java.io.File;
import io.IO;
public class FixSgf {
    void fix(File in,File out) {
        // looks like a round trip!
        MNode root=MNode.restore(IO.toReader(in));
        boolean ok=MNode.save(IO.toWriter(out),root,null);
        if(!ok) parserLogger.severe("fix failed for: "+in);
    }
    void run(File directory) {
        String[] x=directory.list((dir,name)->name.endsWith(".sgf")&&!name.startsWith("fixed"));
        for(String name:x) {
            System.out.println("start: "+name);
            String name2=name.substring(name.length()-23);
            fix(new File(directory,name),new File(directory,"fixed"+name2));
            System.out.println("end: "+name);
            //break;
        }
        //new FixSgf().run(null,null);
    }
    public static void main(String[] args) {
        final File directory=new File("C:/Users/ray/Documents/2stonehandicap/FineArt_A-2hcp");
        new FixSgf().run(directory);
    }
}
