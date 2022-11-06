package x;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
abstract class P1 extends P { P1(String id) { super(id); } }
abstract class P {
    P(String id) { this.id=id; }
    final String id;
    public static final P1 AB=new P11("AB");
    public static final P1 AE=new P12("AE");
    private static final P[] ps= {AB,AE};
    public static SortedMap<String,P> idToP=new TreeMap<>();
    static {
        for(P p:ps) idToP.put(p.id,p);
    }
}
class P11 extends P1 { P11(String id) { super(id); } }
class P12 extends P1 { P12(String id) { super(id); } }
enum P2 { // looks like the only thing i need this for is so i can switch
    // on it!
    AB,AE;
    public enum Types { p11, p12; }
    P2() { p=P.idToP.get(name()); if(p==null) System.out.println("can not find p for "+name()); type=type(p); }
    static Types type(P p) {
        if(p instanceof P11) return Types.p11;
        else if(p instanceof P12) return Types.p12;
        else throw new RuntimeException("unknown type for "+p);
    }
    final P p;
    final Types type;
}
class Property {
    public Property(P p,List<String> list) { this.p=p; this.list=list; }
    @Override public String toString() {
        StringBuffer s=new StringBuffer();
        s.append(p.id);
        for(Iterator<String> i=list.iterator();i.hasNext();) s.append('[').append(i.next().toString()).append(']');
        return s.toString();
    }
    final P p;
    final List<String> list;
}
public class X {
    static void processProperty(P p) {
        P2 p2=P2.valueOf(p.id);
        if(p2!=null) {
            switch(p2) {
                case AB:
                    System.out.println(p2);
                    break;
                case AE:
                    System.out.println(p2);
                    break;
            }
        } else System.out.println("p2 is null!");
    }
    public static void main(String[] args) {
        P p=P.AB;
        Property property=new Property(p,Arrays.asList(new String[] {}));
        System.out.println(property);
        processProperty(p);
        p=P.AE;
        property=new Property(p,Arrays.asList(new String[] {}));
        System.out.println(property);
        processProperty(p);
    }
}
