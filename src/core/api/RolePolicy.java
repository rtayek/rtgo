package core.api;

public interface RolePolicy<S extends GameState, M extends Move> {
    Permission check(Actor actor, Role role, S state, M move);
}
