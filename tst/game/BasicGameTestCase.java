package game;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import controller.GameFixture;
import utilities.Et;
import utilities.ParameterArray;

@RunWith(Parameterized.class) public class BasicGameTestCase extends GameTestSupport {
    @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
    public BasicGameTestCase(int i) { this.i=i; }
    @Test() public void testillyGame() throws Exception {
        et.reset();
        GameFixture.playSillyGame(game,m);
    }
    int i;
    final Et et=new Et();
    final int m=3;
    static final int n=1;
}
