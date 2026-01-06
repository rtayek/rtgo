package utilities;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import sgf.Parser;
public final class SgfTestParameters {
    private SgfTestParameters() {}
    public static Collection<Object[]> allSgfKeysAndFiles() {
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(Parser.sgfDataKeySet());
        objects.addAll(Parser.sgfFiles());
        return ParameterArray.parameterize(objects);
    }
    public static Collection<Object[]> multipleGameKeysAndFiles() {
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(Parser.sgfDataKeySet());
        objects.addAll(Parser.sgfFiles());
        Set<Object> multipleGames=Parser.findMultipleGames(objects);
        if(multipleGames.isEmpty()) throw new RuntimeException("no multiple games found!");
        return ParameterArray.parameterize(multipleGames);
    }
}
