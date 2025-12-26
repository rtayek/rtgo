package core.api;

public interface GamePlugin<S extends GameState, M extends Move, P extends GameSpec> {
    String gameId();
    P defaultSpec();
    S initialState(P spec);
    ApplyResult<S> applyMove(S state, M move);
    MoveCodec<M> moveCodec();
    Renderer<S> renderer();
    RolePolicy<S, M> rolePolicy();
}
