package games.ttt;

import core.api.GameState;

public final class TttState implements GameState {
    public TttState(TttSpec spec, TttMark[] marks, TttMark toPlay, TttOutcome outcome) {
        this.spec = spec;
        this.marks = marks;
        this.toPlay = toPlay;
        this.outcome = outcome;
    }

    public int index(int x, int y) {
        return y * spec.width + x;
    }

    public boolean isOnBoard(int x, int y) {
        return x >= 0 && y >= 0 && x < spec.width && y < spec.height;
    }

    public TttMark at(int x, int y) {
        return marks[index(x, y)];
    }

    public boolean isFull() {
        for (TttMark m : marks) {
            if (m == TttMark.empty) return false;
        }
        return true;
    }

    public final TttSpec spec;
    public final TttMark[] marks;
    public final TttMark toPlay;
    public final TttOutcome outcome;
}
