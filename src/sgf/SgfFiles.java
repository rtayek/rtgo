package sgf;
import io.Logging;
import static com.tayek.util.io.FileIO.toReader;
import static sgf.Parser.*;
import java.util.*;
import io.Init;
public class SgfFiles {
    public static void main(String[] arguments) {
        Logging.mainLogger.info(String.valueOf(Init.first));
        int n=0;
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(sgfDataKeySet());
        objects.addAll(sgfFiles());
        //Set<Object> many=findMultipleGames(objects);
        //Logging.mainLogger.info(many);
        for(Object key:objects) {
            String expectedSgf=getSgfData(key);
            int p=Parser.parentheses(expectedSgf);
            if(p!=0) {
                Logging.mainLogger.info(" bad parentheses: "+p);
                throw new RuntimeException(key+" bad parentheses: "+p);
            }
            SgfNode games=expectedSgf!=null?restoreSgf(toReader(expectedSgf)):null;
            games.preorderCheckFlags();
            String s="";
            if(games.hasASetupType) s+='S';
            if(games.hasAMoveType) s+='m';
            if(games.hasAMove) s+='M';
            //if(!s.equals("")) { Logging.mainLogger.info("key: "+key); Logging.mainLogger.info(s+" "+games); }
            if(games.right!=null) {
                Logging.mainLogger.info("key: "+key);
                Logging.mainLogger.info(s+" "+games); 
                }
        }
    }
}

