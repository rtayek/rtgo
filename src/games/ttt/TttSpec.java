package games.ttt;

import core.api.GameSpec;
import equipment.Point;

public final class TttSpec implements GameSpec {
    public TttSpec(int width, int height, int winLength) {
        this.width = width;
        this.height = height;
        this.winLength = winLength;
    }

    public static TttSpec standard3x3() {
        return new TttSpec(3, 3, 3);
    }

    public int width() { return width; }
    public int height() { return height; }
    public int winLength() { return winLength; }
    public boolean isOnBoard(int x,int y) { return x>=0 && y>=0 && x<width && y<height; }
    public void requireOnBoard(Point point) {
        if(!isOnBoard(point.x,point.y)) throw new IllegalArgumentException("point "+point+" is off-board for "+width+"x"+height);
    }

    private final int width;
    private final int height;
    private final int winLength;
}
