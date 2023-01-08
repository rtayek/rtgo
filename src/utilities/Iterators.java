package utilities;
import java.util.Iterator;
public class Iterators {
    public static class Longs implements Iterator<Long> {
        @Override public boolean hasNext() { return n<Long.MAX_VALUE; }
        @Override public Long next() { return ++n; }
        Long n=0l; // -1?
    }
    public static class Characters implements Iterator<Character> {
        Characters(char first) { character=first; }
        public Characters() { this((char)('a'-1)); }
        @Override public boolean hasNext() { return character<Character.MAX_VALUE; }
        @Override public Character next() { return ++character; }
        Character character='a'-1;
    }
    public static class Strings implements Iterator<String> {
        @Override public boolean hasNext() { return n<Long.MAX_VALUE; }
        @Override public String next() { return (++n).toString(); }
        Long n=0l; // starts at 1.
    }
}
