package utilities;
import java.util.*;
public interface Primitive {
    public static List<Byte> asList(byte array[]) {
        Objects.requireNonNull(array);
        class ResultList extends AbstractList<Byte> implements RandomAccess {
            @Override public Byte get(int index) { return array[index]; }
            @Override public Byte set(int index,Byte element) {
                byte old=array[index];
                array[index]=element;
                return old;
            }
            @Override public int size() { return array.length; }
        }
        return new ResultList();
    }
    public static List<Integer> asList(int array[]) {
        Objects.requireNonNull(array);
        class ResultList extends AbstractList<Integer> implements RandomAccess {
            @Override public Integer get(int index) { return array[index]; }
            @Override public Integer set(int index,Integer element) {
                int old=array[index];
                array[index]=element;
                return old;
            }
            @Override public int size() { return array.length; }
        }
        return new ResultList();
    }
}
