package games.ttt;

import core.engine.Action;
import equipment.Point;

/**
 * Utility to convert TttMove to game-agnostic Action.
 */
public final class TttActionMapper {
    private TttActionMapper() {}

    public static Action toAction(TttMove move,TttSpec spec) {
        if(move instanceof TttMove.Place place) {
            Point p=place.point;
            if(spec==null) throw new IllegalArgumentException("spec is required to validate move bounds");
            if(p.x<0||p.y<0||p.x>=spec.width||p.y>=spec.height) {
                throw new IllegalArgumentException("move "+p+" is off-board for "+spec.width+"x"+spec.height);
            }
            return new Action.Play(p.x,p.y,null);
        }
        throw new IllegalArgumentException("Unknown TttMove type: "+move.getClass());
    }
}
