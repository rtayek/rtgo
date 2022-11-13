package sgf;
import static sgf.Parser.*;
import java.io.StringReader;
import java.util.logging.Level;
import io.*;
public class SgfFiles {
    public static void main(String[] arguments) {
        System.out.println(Init.first);
        Logging.setLevels(Level.OFF);
        int n=0;
        for(Object key:sgfTestData()) {
            if(arguments.length>0) {
                String sgf=getSgfData(key);
                SgfNode games=restoreSgf(new StringReader(sgf));
                if(games!=null) if(games.right!=null) System.out.println(n+" "+key+" "+games.right);
            } else System.out.println(n+" "+key);
            ++n;
        }
    }
}
