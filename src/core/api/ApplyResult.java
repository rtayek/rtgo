package core.api;

public final class ApplyResult<S extends GameState> {
    public final S state;
    public final boolean accepted;
    public final String message;

    public static <S extends GameState> ApplyResult<S> accepted(S state) {
        return new ApplyResult<>(state, true, "");
    }

    public static <S extends GameState> ApplyResult<S> rejected(S state, String message) {
        return new ApplyResult<>(state, false, message);
    }

    private ApplyResult(S state, boolean accepted, String message) {
        this.state = state;
        this.accepted = accepted;
        this.message = message;
    }
}
