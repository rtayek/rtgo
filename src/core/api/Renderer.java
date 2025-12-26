package core.api;

public interface Renderer<S extends GameState> {
    String render(S state);
}
