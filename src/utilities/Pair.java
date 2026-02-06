package utilities;
public class Pair<T,U> extends com.tayek.util.core.Pair<T,U> {
    public Pair(T first,U second) {
        super(first,second);
    }
    @Override public String toString() {
        return "("+first+","+second+")";
    }
}
