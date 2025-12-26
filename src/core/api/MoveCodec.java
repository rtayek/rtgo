package core.api;

public interface MoveCodec<M extends Move> {
    M parse(String text);
    String format(M move);
}
