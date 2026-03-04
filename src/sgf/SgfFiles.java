package sgf;
import io.Logging;
import model.ModelIo;
import static io.Init.first;
import static sgf.Parser.*;
import java.util.*;
import java.util.logging.Level;
import com.tayek.util.io.FileIO;
import io.Init;
public class SgfFiles {
    public static void main(String[] arguments) {
		first.twice(); // do this first in all main programs!
        Logging.mainLogger.info(String.valueOf(Init.first));
        Logging.setLevels(Level.WARNING);
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
            SgfNode games=ModelIo.restoreSGF(FileIO.toReader(expectedSgf));
            if(games.siblings()>0) System.out.println(key+" has "+(games.siblings()+1)+" games.");
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

