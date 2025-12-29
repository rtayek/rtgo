package utilities;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
public class Range<T extends Comparable<T>> {
    static class Ranges extends TreeSet<Range<?>> {
        // add  and and or
        // how about not?
        // allow -max int x, and c max int?
        private static final long serialVersionUID=1L;
    }
    public static <T extends Number & Comparable<T>> int compare(T a,T b) { return a.compareTo(b); }
    public Range(T min,T max) { this.min=min; this.max=max; }
    boolean contains(T t) {
        if(min instanceof Number) {
            int rc1=numberComparator.compare((Number)min,(Number)t);
            int rc2=numberComparator.compare((Number)t,(Number)max);
            if(rc1<=0&&rc2<=0) return true;
        } else if(min instanceof Enum<?>) {
            Class<?> clazz=((Enum<?>)t).getDeclaringClass();
            if(clazz!=null) {
                List<?> enums=Arrays.asList(clazz.getEnumConstants());
                if(enums.contains(t))
                    if(((Enum<?>)min).ordinal()<=((Enum<?>)t).ordinal()
                            &&((Enum<?>)t).ordinal()<=((Enum<?>)max).ordinal())
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
    static final Comparator<Number> numberComparator=(a,b)->Double.compare(a.doubleValue(),b.doubleValue());
}
