package sgf;
import static sgf.Parser.*;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import io.*;
public class SgfFiles {
    public static void main(String[] arguments) {
        System.out.println(Init.first);
        Logging.setLevels(Level.OFF);
        int n=0;
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(sgfDataKeySet());
        objects.addAll(sgfFiles());
        for(Object key:objects) {
            if(arguments.length>0) {
                String sgf=getSgfData(key);
                SgfNode games=restoreSgf(new StringReader(sgf));
                if(games!=null) if(games.right!=null) System.out.println(n+" "+key+" has more than one game: "+games.right);
            } else System.out.println(n+" "+key);
            ++n;
        }
    }
}
