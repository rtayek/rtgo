package games.ttt;

import core.api.Move;
import equipment.Point;

public interface TttMove extends Move {

    final class Place implements TttMove {
        public Place(Point point) {
            this.point = point;
        }

        public final Point point;
    }
}
