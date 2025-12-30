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
        return y * spec.width() + x;
    }

    public boolean isOnBoard(int x, int y) {
        return spec.isOnBoard(x,y);
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

    public TttSpec spec() { return spec; }
    public TttMark toPlay() { return toPlay; }
    public TttOutcome outcome() { return outcome; }
    public TttMark markAtIndex(int idx) { return marks[idx]; }
    public TttMark[] marksCopy() { return marks.clone(); }

    private final TttSpec spec;
    private final TttMark[] marks;
    private final TttMark toPlay;
    private final TttOutcome outcome;
}
