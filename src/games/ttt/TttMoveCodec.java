package games.ttt;

import core.api.MoveCodec;
import equipment.Point;

public final class TttMoveCodec implements MoveCodec<TttMove> {
    private final TttSpec spec;

    public TttMoveCodec() {
        this(TttSpec.standard3x3());
    }

    public TttMoveCodec(TttSpec spec) {
        this.spec=spec;
    }

    @Override
    public TttMove parse(String text) {
        String t = text.trim().toLowerCase();
        if (t.isEmpty()) throw new IllegalArgumentException("move must be: x y");

        String[] parts = t.split("\\s+");
        if (parts.length != 2) throw new IllegalArgumentException("move must be: x y");

        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        return TttMove.Place.of(spec,new Point(x, y));
    }

    @Override
    public String format(TttMove move) {
        if (move instanceof TttMove.Place place) {
            return place.point().x + " " + place.point().y;
        }
        return move.toString();
    }
}
