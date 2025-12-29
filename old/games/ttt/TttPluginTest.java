package games.ttt;

import static org.junit.Assert.*;

import core.api.ApplyResult;
import core.api.MoveCodec;
import equipment.Point;
import org.junit.Test;

public class TttPluginTest {
    private final TttPlugin plugin = new TttPlugin();
    private final MoveCodec<TttMove> codec = plugin.moveCodec();

    @Test public void initialStateIsEmpty() {
        TttState state = plugin.initialState(plugin.defaultSpec());
        assertEquals(TttMark.x, state.toPlay);
        assertEquals(TttOutcome.ongoing, state.outcome);
        for (TttMark m : state.marks) assertEquals(TttMark.empty, m);
    }

    @Test public void acceptLegalMoveAndToggleTurn() {
        TttState state = plugin.initialState(plugin.defaultSpec());
        TttMove move = new TttMove.Place(new Point(0, 0));
        ApplyResult<TttState> result = plugin.applyMove(state, move);
        assertTrue(result.accepted);
        TttState next = result.state;
        assertEquals(TttMark.x, next.at(0, 0));
        assertEquals(TttMark.o, next.toPlay); // toggled
        assertEquals(TttOutcome.ongoing, next.outcome);
    }

    @Test public void rejectMoveOnOccupied() {
        TttState state = plugin.initialState(plugin.defaultSpec());
        state = plugin.applyMove(state, new TttMove.Place(new Point(0, 0))).state;
        ApplyResult<TttState> result = plugin.applyMove(state, new TttMove.Place(new Point(0, 0)));
        assertFalse(result.accepted);
    }

    @Test public void detectWin() {
        // X plays (0,0), (1,0), (2,0) for a row win
        TttState s = plugin.initialState(plugin.defaultSpec());
        s = plugin.applyMove(s, place(0, 0)).state;
        s = plugin.applyMove(s, place(0, 1)).state; // O
        s = plugin.applyMove(s, place(1, 0)).state;
        s = plugin.applyMove(s, place(1, 1)).state; // O
        ApplyResult<TttState> result = plugin.applyMove(s, place(2, 0)); // X wins
        assertTrue(result.accepted);
        assertEquals(TttOutcome.xWins, result.state.outcome);
    }

    @Test public void detectDraw() {
        // Fill the board with no 3-in-a-row
        TttState s = plugin.initialState(plugin.defaultSpec());
        int[][] seq = {
                {0,0}, {1,0}, {2,0},
                {1,1}, {0,1}, {2,1},
                {1,2}, {0,2}, {2,2}
        };
        for (int i=0;i<seq.length;i++) {
            ApplyResult<TttState> r = plugin.applyMove(s, place(seq[i][0], seq[i][1]));
            assertTrue(r.accepted);
            s = r.state;
        }
        assertEquals(TttOutcome.draw, s.outcome);
    }

    @Test public void codecRoundTrip() {
        TttMove move = place(2, 1);
        String encoded = codec.format(move);
        TttMove parsed = codec.parse(encoded);
        assertTrue(parsed instanceof TttMove.Place);
        assertEquals(2, ((TttMove.Place) parsed).point.x);
        assertEquals(1, ((TttMove.Place) parsed).point.y);
    }

    private static TttMove place(int x, int y) {
        return new TttMove.Place(new Point(x, y));
    }
}
