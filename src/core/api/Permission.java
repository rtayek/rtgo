package core.api;

/**
 * A Permission is the result of evaluating whether a specific action
 * is allowed at a specific moment, given the current game state.
 *
 * It is intentionally short-lived and immutable.
 */
public final class Permission {

    public final boolean allowed;
    public final String reason;

    private Permission(boolean allowed, String reason) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public static Permission allow() {
        return new Permission(true, "");
    }

    public static Permission deny(String reason) {
        if (reason == null) {
            throw new IllegalArgumentException("reason must not be null");
        }
        return new Permission(false, reason);
    }

    @Override
    public String toString() {
        return allowed ? "allowed" : "denied: " + reason;
    }
}
