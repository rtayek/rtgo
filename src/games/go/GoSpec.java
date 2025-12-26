package games.go;

import core.api.GameSpec;
import equipment.Board;

public final class GoSpec implements GameSpec {
    GoSpec(int width, int height, Board.Topology topology, Board.Shape shape) {
        this.width = width;
        this.height = height;
        this.topology = topology;
        this.shape = shape;
    }

    final int width;
    final int height;
    final Board.Topology topology;
    final Board.Shape shape;
}
