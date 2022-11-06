package utilities;

public class Pair<T,U> {
    public Pair(T first,U second) { this.first=first; this.second=second; }
    @Override public String toString() { return("("+first+","+second+")"); }
    public T first;
    public U second;
}