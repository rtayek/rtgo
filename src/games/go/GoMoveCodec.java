package games.go;

import core.api.MoveCodec;
import equipment.Point;

public final class GoMoveCodec implements MoveCodec<GoMove> {

    @Override
    public GoMove parse(String text) {
        String t = text.trim().toLowerCase();
        if (t.equals("pass")) return new GoMove.Pass();
        if (t.equals("resign")) return new GoMove.Resign();

        String[] parts = t.split("\\s+");
        if (parts.length != 2) throw new IllegalArgumentException("move must be: x y, or pass/resign");

        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        return new GoMove.Play(new Point(x, y));
    }

    @Override
    public String format(GoMove move) {
        if (move instanceof GoMove.Pass) return "pass";
        if (move instanceof GoMove.Resign) return "resign";
        GoMove.Play play = (GoMove.Play) move;
        return play.point.x + " " + play.point.y;
    }
}
