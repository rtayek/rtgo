package equipment;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import model.Model;
import utilities.MyTestWatcher;
public class ScreenCoordinatesTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
   @Test public void testSize() {
        for(int n=1;n<=Model.maxN;n++) {
            Board board=new BoardImpl(n,0);
            assertEquals(n,board.width());
            assertEquals(n,board.depth());
        }
    }
    @Test public void testAt() {
        for(int x=0;x<b.width();x++) for(int y=0;y<b.depth();y++) assertEquals(Stone.vacant,b.at(x,y));
    }
    @Test public void testSetAt() {
        for(Stone stone:Stone.values()) for(int x=0;x<b.width();x++) for(int y=0;y<b.depth();y++) {
            b.setAt(x,y,stone);
            assertEquals(stone,b.at(x,y));
        }
    }
    Board b=new BoardImpl(Board.standard,0);
}
