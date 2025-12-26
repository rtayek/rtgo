package games.go;

import core.api.Move;
import equipment.Point;

public interface GoMove extends Move {

    final class Play implements GoMove {
        public final Point point;

        public Play(Point point) {
            this.point = point;
        }
    }

    final class Pass implements GoMove {
        public Pass() {
        }
    }

    final class Resign implements GoMove {
        public Resign() {
        }
    }
}
