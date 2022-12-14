package sgf;
import org.junit.*;
import utilities.MyTestWatcher;
public class MultipleGamesTestCase extends AbstractMultipleGamesTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        key=Parser.empty;
        key="twoEmptyWithSemicolon";
        key="sgfExamleFromRedBean";
        super.setUp();
        //System.out.println(expectedSgf);
    }
}
