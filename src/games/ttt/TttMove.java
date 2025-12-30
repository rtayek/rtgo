package games.ttt;

import core.api.Move;
import equipment.Point;

public interface TttMove extends Move {

    final class Place implements TttMove {
        public Place(Point point) {
            this.point = point;
        }

        public static Place of(TttSpec spec,Point point) {
            if(spec==null) throw new IllegalArgumentException("spec is required");
            if(point==null) throw new IllegalArgumentException("point is required");
            if(!spec.isOnBoard(point.x,point.y)) {
                throw new IllegalArgumentException("move "+point+" is off-board for "+spec.width()+"x"+spec.height());
            }
            return new Place(point);
        }

        public Point point() { return point; }

        private final Point point;
    }
}
