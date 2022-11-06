package equipment;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import utilities.MyTestWatcher;
public class BigBoardTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testBigBoard() { int n=52; Board board=Board.factory.create(n); board.setAll(Stone.black); }
}
