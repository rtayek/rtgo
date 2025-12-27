package games.go;

import core.api.*;
import equipment.*;

public final class GoPlugin implements GamePlugin<GoState, GoMove, GoSpec> {
    @Override public String gameId() { return "go"; }

    @Override public GoSpec defaultSpec() {
        return new GoSpec(19, 19, Board.Topology.normal, Board.Shape.normal);
    }

    @Override public GoState initialState(GoSpec spec) {
        Board board = Board.factory.create(spec.width, spec.height, spec.topology, spec.shape);
        return new GoState(board, Stone.black);
    }

    @Override public ApplyResult<GoState> applyMove(GoState state, GoMove move) {
        if(move instanceof GoMove.Pass) {
            return ApplyResult.accepted(new GoState(state.board, state.toPlay.otherColor()));
        }
        if(move instanceof GoMove.Resign) {
            return ApplyResult.accepted(state);
        }

        GoMove.Play play = (GoMove.Play) move;

        // TODO wire to your existing legality + capture logic.
        // This should:
        //  - reject illegal move (occupied, suicide, ko, off-board, masked)
        //  - update board stones and captures
        //  - flip toPlay
        //
        // For now, placeholder:
        boolean legal = state.board.isOnBoard(play.point);
        if(legal) legal=state.board.at(play.point)==Stone.vacant;
        if(!legal) return ApplyResult.rejected(state, "off board");
        return ApplyResult.rejected(state, "not wired yet");
    }

    @Override public MoveCodec<GoMove> moveCodec() { return codec; }

    @Override public Renderer<GoState> renderer() { return renderer; }

    @Override public RolePolicy<GoState, GoMove> rolePolicy() { return rolePolicy; }

    private final MoveCodec<GoMove> codec = new GoMoveCodec();
    private final Renderer<GoState> renderer = state -> state.board.toString();

    private final RolePolicy<GoState, GoMove> rolePolicy = (actor, role, state, move) -> {
        if(role == Role.anything) return Permission.allow();
        if(role == Role.observer) return Permission.deny("observer cannot play");
        if(role == Role.playBlack && state.toPlay != Stone.black) return Permission.deny("not black's turn");
        if(role == Role.playWhite && state.toPlay != Stone.white) return Permission.deny("not white's turn");
        return Permission.allow();
    };
}
