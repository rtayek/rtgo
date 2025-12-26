package games.go;

import core.api.GameState;
import equipment.Board;
import equipment.Stone;

public final class GoState implements GameState {
    public final Board board;
    public final Stone toPlay;

    public GoState(Board board, Stone toPlay) {
        this.board = board;
        this.toPlay = toPlay;
    }
}
