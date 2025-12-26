package core.api;

public interface GameSession<S extends GameState, M extends Move> {
    String sessionId();
    S state();
    ApplyResult<S> submit(Actor actor, M move);
    void setRole(String participantId, Role role);
    Role roleOf(String participantId);
}
