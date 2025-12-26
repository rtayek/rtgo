package core.api;

public interface EngineAdapter<S extends GameState, M extends Move> {
    String engineId();
    M genMove(S state, Role role);
}
