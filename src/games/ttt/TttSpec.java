package games.ttt;

import core.api.GameSpec;

public final class TttSpec implements GameSpec {
    public TttSpec(int width, int height, int winLength) {
        this.width = width;
        this.height = height;
        this.winLength = winLength;
    }

    public static TttSpec standard3x3() {
        return new TttSpec(3, 3, 3);
    }

    public final int width;
    public final int height;
    public final int winLength;
}
