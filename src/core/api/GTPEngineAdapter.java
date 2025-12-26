package core.api;

public final class GTPEngineAdapter<S extends GameState, M extends Move> implements EngineAdapter<S, M> {
    GTPEngineAdapter(String engineId) {
        this.engineId = engineId;
    }

    @Override public String engineId() { return engineId; }

    @Override public M genMove(S state, Role role) {
        throw new UnsupportedOperationException("not wired");
    }

    private final String engineId;
}
