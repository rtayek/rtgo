package core.session;

import java.util.HashMap;
import java.util.Map;
import core.api.*;

public final class BasicGameSession<S extends GameState, M extends Move, P extends GameSpec> implements GameSession<S, M> {
    public BasicGameSession(String sessionId, GamePlugin<S, M, P> plugin, P spec) {
        this.sessionId = sessionId;
        this.plugin = plugin;
        this.state = plugin.initialState(spec);
        this.rolesByParticipantId = new HashMap<>();
    }

    @Override public String sessionId() { return sessionId; }

    @Override public S state() { return state; }

    @Override public ApplyResult<S> submit(Actor actor, M move) {
        Role role = rolesByParticipantId.getOrDefault(actor.participantId(), Role.observer);
        Permission permission = plugin.rolePolicy().check(actor, role, state, move);
        if(!permission.allowed) return ApplyResult.rejected(state, permission.reason);

        ApplyResult<S> result = plugin.applyMove(state, move);
        if(result.accepted) state = result.state;
        return result;
    }

    @Override public void setRole(String participantId, Role role) { rolesByParticipantId.put(participantId, role); }

    @Override public Role roleOf(String participantId) { return rolesByParticipantId.getOrDefault(participantId, Role.observer); }

    private S state;

    private final String sessionId;
    private final GamePlugin<S, M, P> plugin;
    private final Map<String, Role> rolesByParticipantId;
}
