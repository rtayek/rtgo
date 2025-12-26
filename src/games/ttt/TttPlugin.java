package games.ttt;

import core.api.*;

public final class TttPlugin implements GamePlugin<TttState, TttMove, TttSpec> {

    @Override
    public String gameId() {
        return "ttt";
    }

    @Override
    public TttSpec defaultSpec() {
        return TttSpec.standard3x3();
    }

    @Override
    public TttState initialState(TttSpec spec) {
        TttMark[] marks = new TttMark[spec.width * spec.height];
        for (int i = 0; i < marks.length; i++) marks[i] = TttMark.empty;
        return new TttState(spec, marks, TttMark.x, TttOutcome.ongoing);
    }

    @Override
    public ApplyResult<TttState> applyMove(TttState state, TttMove move) {
        if (state.outcome != TttOutcome.ongoing) {
            return ApplyResult.rejected(state, "game is over: " + state.outcome);
        }

        if (!(move instanceof TttMove.Place place)) {
            return ApplyResult.rejected(state, "unknown move type");
        }

        int x = place.point.x;
        int y = place.point.y;

        if (!state.isOnBoard(x, y)) return ApplyResult.rejected(state, "off board");
        int idx = state.index(x, y);
        if (state.marks[idx] != TttMark.empty) return ApplyResult.rejected(state, "occupied");

        TttMark[] nextMarks = state.marks.clone();
        nextMarks[idx] = state.toPlay;

        TttOutcome outcome = evaluateOutcome(state.spec, nextMarks);
        TttMark nextToPlay = outcome == TttOutcome.ongoing ? state.toPlay.other() : state.toPlay;

        return ApplyResult.accepted(new TttState(state.spec, nextMarks, nextToPlay, outcome));
    }

    @Override
    public MoveCodec<TttMove> moveCodec() {
        return codec;
    }

    @Override
    public Renderer<TttState> renderer() {
        return renderer;
    }

    @Override
    public RolePolicy<TttState, TttMove> rolePolicy() {
        return rolePolicy;
    }

    private final MoveCodec<TttMove> codec = new TttMoveCodec();

    private final Renderer<TttState> renderer = state -> {
        StringBuilder sb = new StringBuilder();
        sb.append("TicTacToe ").append(state.spec.width).append("x").append(state.spec.height)
          .append(" win=").append(state.spec.winLength).append("\n");
        sb.append("toPlay=").append(state.toPlay).append(" outcome=").append(state.outcome).append("\n\n");

        for (int y = 0; y < state.spec.height; y++) {
            for (int x = 0; x < state.spec.width; x++) {
                TttMark m = state.at(x, y);
                char c = m == TttMark.empty ? '.' : (m == TttMark.x ? 'X' : 'O');
                sb.append(c);
                if (x + 1 < state.spec.width) sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    };

    private final RolePolicy<TttState, TttMove> rolePolicy = (actor, role, state, move) -> {
        if (!(move instanceof TttMove.Place)) return Permission.deny("unknown move type");

        if (role == Role.anything) return Permission.allow();
        if (role == Role.observer) return Permission.deny("observer cannot play");

        if (role == Role.playBlack && state.toPlay != TttMark.x) return Permission.deny("not X's turn");
        if (role == Role.playWhite && state.toPlay != TttMark.o) return Permission.deny("not O's turn");

        return Permission.allow();
    };

    private static TttOutcome evaluateOutcome(TttSpec spec, TttMark[] marks) {
        int w = spec.width;
        int h = spec.height;
        int k = spec.winLength;

        if (k <= 1) return TttOutcome.draw; // degenerate, treat as done

        // Check lines for X and O
        if (hasKInRow(w, h, k, marks, TttMark.x)) return TttOutcome.xWins;
        if (hasKInRow(w, h, k, marks, TttMark.o)) return TttOutcome.oWins;

        // Draw if full
        for (TttMark m : marks) {
            if (m == TttMark.empty) return TttOutcome.ongoing;
        }
        return TttOutcome.draw;
    }

    private static boolean hasKInRow(int w, int h, int k, TttMark[] marks, TttMark target) {
        // directions: right, down, diag-down-right, diag-down-left
        int[] dx = { 1, 0, 1, -1 };
        int[] dy = { 0, 1, 1,  1 };

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (marks[y * w + x] != target) continue;

                for (int dir = 0; dir < dx.length; dir++) {
                    int cx = x;
                    int cy = y;
                    int n = 1;

                    while (n < k) {
                        cx += dx[dir];
                        cy += dy[dir];
                        if (cx < 0 || cy < 0 || cx >= w || cy >= h) break;
                        if (marks[cy * w + cx] != target) break;
                        n++;
                    }

                    if (n == k) return true;
                }
            }
        }
        return false;
    }
}
