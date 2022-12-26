package utilities;
import java.util.*;
public class Range<T> {
    static class Ranges extends TreeSet<Range> {
        // add  and and or
        // how about not?
        // allow -max int x, and c max int?
        private static final long serialVersionUID=1L;
        
    }
    static class NumberComparator<T extends Number> implements Comparator<T> {
        @Override public int compare(T a,T b) {
            if(a instanceof Comparable) if(a.getClass().equals(b.getClass())) return ((Comparable<T>)a).compareTo(b);
            throw new UnsupportedOperationException();
        }
    }
    static class NumberComparator2<T extends Number & Comparable> implements Comparator<T> {
        @Override public int compare(T a,T b) throws ClassCastException { return a.compareTo(b); }
    }
    public static <T extends Number & Comparable<T>> int compare(T a,T b) { return a.compareTo(b); }
    static class NumberComparator3<T extends Number> implements Comparator<T> {
        @Override public int compare(T a,T b) {
            if(a instanceof Comparable) if(a.getClass().equals(b.getClass())) return ((Comparable<T>)a).compareTo(b);
            throw new UnsupportedOperationException();
        }
    }
    Range(T min,T max) { this.min=min; this.max=max; }
    boolean contains(T t) {
        if(min instanceof Number) {
            int rc1=comparator.compare((Number)min,(Number)t);
            int rc2=comparator.compare((Number)t,(Number)max);
            if(rc1<=0&&rc2<=0) return true;
        } else if(min instanceof Enum) {
            Class<T> clazz=((Enum)t).getDeclaringClass();
            if(clazz!=null) {
                List<T> enums=Arrays.asList(clazz.getEnumConstants());
                if(enums.contains(t))
                    if(((Enum)min).ordinal()<=((Enum)t).ordinal()&&((Enum)t).ordinal()<=((Enum)max).ordinal())
                        return true;
            }
        }
        return false;
    }
    enum L { a, b, c, d, e }
    public static void main(String[] args) {
        // make this a test!
        Range<Integer> ir=new Range<>(0,10);
        System.out.println(ir.contains(-1));
        System.out.println(ir.contains(0));
        System.out.println(ir.contains(1));
        System.out.println(ir.contains(10));
        System.out.println(ir.contains(11));
        Range<L> er=new Range<>(L.b,L.e);
        System.out.println(er.contains(L.a));
        System.out.println(er.contains(L.b));
        System.out.println(er.contains(L.c));
        System.out.println(er.contains(L.e));
    }
    final T min,max;
    static final NumberComparator3 comparator=new NumberComparator3<>();
}
