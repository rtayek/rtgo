package sgf;
import java.util.*;
public class SgfProperty {
    public SgfProperty(P p,List<String> list) { this.p=p; this.list=list; }
    @Override public String toString() {
        StringBuffer s=new StringBuffer();
        s.append(p.id);
        for(Iterator<String> i=list.iterator();i.hasNext();) s.append('[').append(i.next().toString()).append(']');
        return s.toString();
    }
    @Override public boolean equals(Object o) {
        if(this==o) return true;
        if(!(o instanceof SgfProperty)) return false;
        SgfProperty property=(SgfProperty)o;
        if(p!=property.p) return false; // is this correct?
        // maybe use equals above?
        return list.equals(property.list);
    }
    public P p() { return p; }
    public List<String> list() { return list; }
    private final P p;
    private final List<String> list;
}
