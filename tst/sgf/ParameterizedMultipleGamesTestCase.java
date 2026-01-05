package sgf;
import static sgf.Parser.*;
import java.util.*;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedMultipleGamesTestCase extends AbstractMNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameterized.Parameters(name = "{0}") public static Collection<Object[]> parameters() {
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(sgfDataKeySet());
        objects.addAll(sgfFiles());
        Set<Object> multipleGames=findMultipleGames(objects);
        if(multipleGames.isEmpty()) throw new RuntimeException("no multiple games found!");
        return ParameterArray.parameterize(multipleGames);
    }
    public ParameterizedMultipleGamesTestCase(Object key) { this.key=key; }
}
