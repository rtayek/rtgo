package x;
import io.Logging;
import java.util.LinkedHashMap;
import x.Os.O;
enum E1 { e1, e11 }
enum E2 { e2, e22 }
enum Names { e1, e2 }
public class Os extends LinkedHashMap<Enum<?>,O> {
    public static class O { // add range later
        public O(Object value) { this.defaultValue=currentValue=value; }
        public Object currentValue() { return currentValue; }
        public void setCurrentValue(Object newValue) { this.currentValue=newValue; }
        public Object fromString(String string) { return Integer.valueOf(string); } // overwrite in sub class.
        @Override public String toString() { return currentValue+"("+defaultValue+")"; }
        public final Object defaultValue;
        private Object currentValue;
    }
    public void add(Enum<?> t,O o) { put(t,o); }
    public Enum<?> valueOf(String name) { for(Enum<?> e:keySet()) if(e.name().equals(name)) return e; return null; }
    // do i need a fully qualified name like Frog.fred?
    public static void main(String[] args) {
        Os os=new Os() {
            {
                add(E1.e1,new O(Integer.valueOf(42)));
                Logging.mainLogger.info(String.valueOf(get(E1.e1)));
                add(E2.e2,new O(Double.valueOf(.5)));
                Logging.mainLogger.info(String.valueOf(get(E2.e2)));
                add(Names.e1,new O(E1.e1));
                Logging.mainLogger.info(String.valueOf(get(Names.e1)));
                add(Names.e2,new O(Names.e2));
                Logging.mainLogger.info(String.valueOf(get(Names.e2)));
            }
        };
        Logging.mainLogger.info(String.valueOf(os));
    }
}
