package sgf;
import static sgf.Parser.*;
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
        Set<Object> many=findMultipleGames(objects);
        System.out.println(many);
        for(Object key:objects) {
            System.out.println("key: "+key);
            String expectedSgf=getSgfData(key);
            ///setIsAMoveFlags();
        }

    }
}
