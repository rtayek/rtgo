package equipment;
import org.junit.Rule;
import utilities.MyTestWatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class BigBoardTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testBigBoard() { int n=52; Board board=Board.factory.create(n); board.setAll(Stone.black); }
}

