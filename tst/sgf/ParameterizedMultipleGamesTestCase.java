package sgf;
import static sgf.Parser.*;
import java.util.*;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedMultipleGamesTestCase extends AbstractMNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(sgfDataKeySet());
        objects.addAll(sgfFiles());
        Set<Object> multipleGames=findMultipleGames(objects);
        return ParameterArray.parameterize(multipleGames);
    }
    public ParameterizedMultipleGamesTestCase(Object key) { this.key=key; }
}
