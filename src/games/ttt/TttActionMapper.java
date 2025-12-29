package games.ttt;

import core.engine.Action;
import equipment.Point;

/**
 * Utility to convert TttMove to game-agnostic Action.
 */
public final class TttActionMapper {
    private TttActionMapper() {}

    public static Action toAction(TttMove move) {
        if(move instanceof TttMove.Place place) {
            Point p=place.point;
            return new Action.Play(p.x,p.y,null);
        }
        throw new IllegalArgumentException("Unknown TttMove type: "+move.getClass());
    }
}
