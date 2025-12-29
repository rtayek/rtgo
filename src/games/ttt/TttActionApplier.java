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
        if(action instanceof Action.Play play) {
            TttMove move=new TttMove.Place(new Point(play.x(),play.y()));
            return plugin.applyMove(state,move);
        }
        // Pass/Resign/SetBoard/Metadata are not supported for now.
        return ApplyResult.rejected(state,"unsupported action: "+action.getClass().getSimpleName());
    }
}
