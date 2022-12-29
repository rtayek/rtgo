package sgf;
import static sgf.Parser.*;
import java.io.StringReader;
import java.util.*;
import io.Init;
public class SgfFiles {
    public static void main(String[] arguments) {
        System.out.println(Init.first);
        int n=0;
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(sgfDataKeySet());
        objects.addAll(sgfFiles());
        //Set<Object> many=findMultipleGames(objects);
        //System.out.println(many);
        for(Object key:objects) {
            String expectedSgf=getSgfData(key);
            int p=Parser.parentheses(expectedSgf);
            if(p!=0) {
                System.out.println(" bad parentheses: "+p);
                throw new RuntimeException(key+" bad parentheses: "+p);
            }
            SgfNode games=expectedSgf!=null?restoreSgf(new StringReader(expectedSgf)):null;
            games.preorderCheckFlags();
            String s="";
            if(games.hasASetupType) s+='S';
            if(games.hasAMoveType) s+='m';
            if(games.hasAMove) s+='M';
            if(s.equals("")) s="0";
            if(true||!s.equals("0")) { System.out.println("key: "+key); System.out.println(s+" "+games); }
        }
    }
}
