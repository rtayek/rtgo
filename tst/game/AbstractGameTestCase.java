package game;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import equipment.Point;
import equipment.Stone;
import io.IOs;
import model.Move2;
import model.Move2.MoveType;
import utilities.ParameterArray;
public abstract class AbstractGameTestCase extends GameTestSupport { // these test cases require a running game.
    public static class GameSocketTestCase extends AbstractGameTestCase {
        @Override @Before public void setUp() throws Exception {
            setServerPort(IOs.anyPort);
            super.setUp();
        }
        @RunWith(Parameterized.class) public static class ParameterizedGameSocketTestCase extends GameSocketTestCase {
            @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
            public ParameterizedGameSocketTestCase(int i) { this.i=i; }
            final int i;
            static final int n=5;
        }
    }
    public static class GameDuplexTestCase extends AbstractGameTestCase {
        @Override @Before public void setUp() throws Exception {
            setServerPort(IOs.noPort);
            super.setUp();
        }
        @RunWith(Parameterized.class) public static class ParameterizedGameDuplexTestCase extends GameDuplexTestCase {
            @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
            public ParameterizedGameDuplexTestCase(int i) { this.i=i; }
            final int i;
            static final int n=2;
        }
    }
    // these guys need a game running.
    @Test() public void testPlayZeroMoves() throws Exception {}
    @Test() public void testPlayOneMove() throws Exception {
        playMoves(new Move2(MoveType.move,Stone.black,new Point()));
    }
    @Test() public void testPlayTwoMoves() throws Exception {
        playMoves(
                new Move2(MoveType.move,Stone.black,new Point()),
                new Move2(MoveType.move,Stone.white,new Point(1,0))
        );
    }
    @Test() public void testPlayThreeMoves() throws Exception {
        playMoves(
                new Move2(MoveType.move,Stone.black,new Point()),
                new Move2(MoveType.move,Stone.white,new Point(1,0)),
                new Move2(MoveType.move,Stone.black,new Point(2,0))
        );
    }
    @Test() public void testPassOnce() throws Exception {
        playMoves(Move2.blackPass);
    }
    @Test() public void testPassTwice() throws Exception {
        playMoves(Move2.blackPass,Move2.whitePass);
    }
    @Test() public void testPassThreTimes() throws Exception {
        playMoves(Move2.blackPass,Move2.whitePass,Move2.blackPass);
    }
    @Test() public void testResign() throws Exception {
        playMoves(Move2.blackResign);
    }

    protected final void playMoves(Move2... moves) throws Exception {
        for(Move2 move:moves) {
            if(move.color.equals(Stone.black)) {
                game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,move);
            } else {
                game.playOneMoveAndWait(game.whiteFixture,game.blackFixture,move);
            }
        }
    }
}
