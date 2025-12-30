package games.ttt;

import core.api.ApplyResult;
import core.engine.Action;
import equipment.Point;

/**
 * Applies neutral Actions to Tic-Tac-Toe state.
 */
public final class TttActionApplier {
    private final TttPlugin plugin=new TttPlugin();

    public ApplyResult<TttState> apply(TttState state,Action action) {
        return switch(action) {
            case Action.Play play -> {
                TttMove move=new TttMove.Place(new Point(play.x(),play.y()));
                yield plugin.applyMove(state,move);
            }
            case Action.Pass pass -> ApplyResult.rejected(state,"pass not supported in tic-tac-toe");
            case Action.Resign resign -> ApplyResult.rejected(state,"resign not supported in tic-tac-toe");
            case Action.SetBoard setBoard -> ApplyResult.rejected(state,"dynamic board config not supported in tic-tac-toe");
            case Action.Metadata metadata -> ApplyResult.rejected(state,"metadata actions are ignored in tic-tac-toe");
        };
    }
}
