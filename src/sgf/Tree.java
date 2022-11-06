package sgf;
import static io.Logging.parserLogger;
import java.util.*;
import utilities.Holder;
public interface Tree {
    public static class BinaryNode<T> {
        //BinaryNode(T t) { this.t=t; }
        GeneralNodee<T> convert() { return null; }
        BinaryNode(BinaryNode<T> left,BinaryNode<T> right,T t) { this.left=left; this.right=right; this.t=t; }
        private BinaryNode<T> lastSibling_(Holder<Integer> h) {
            BinaryNode<T> node=null,last=this;
            for(node=right;node!=null;node=node.right) {
                last=node;
                ++h.t;
            }
            return last;
        }
        private BinaryNode<T> lastDescendant_(Holder<Integer> h) {
            BinaryNode<T> node=null,last=this;
            for(node=left;node!=null;node=node.left) {
                last=node;
                ++h.t;
            }
            return last;
        }
        protected BinaryNode<T> lastSibling() { return lastSibling_(new Holder<Integer>(0)); }
        int siblings() { Holder<Integer> siblings=new Holder<Integer>(0); lastSibling_(siblings); return siblings.t; }
        protected BinaryNode<T> lastDescendant() { return lastDescendant_(new Holder<Integer>(0)); }
        void addSibling(BinaryNode<T> node) { BinaryNode<T> last=lastSibling(); last.right=node; }
        private BinaryNode<T> lastChild() { return left==null?null:left.lastSibling(); }
        private void addDescendant(T t) {
            BinaryNode<T> last=lastDescendant();
            last.left=new BinaryNode<T>(null,null,t);
        }
        private int children() {
            if(left==null) return 0;
            Holder<Integer> siblings=new Holder<>(0);
            left.lastSibling_(siblings);
            return siblings.t+1; // why n+1? may be used as an index elsewhere
        }
        final T t;
        BinaryNode<T> left,right;
    }
    public static class GeneralNodee<T> /*extends BinaryNode<T>*/ {
        GeneralNodee(T t) { this.t=t; }
        BinaryNode<T> convert() { return null; }
        final T t;
        public final ArrayList<T> children=new ArrayList<>();
    }
    public static class Sgf extends ArrayList<SgfProperty> { // will be data in trees now
        public Sgf() {}
        public Sgf(List<SgfProperty> properties) { this(); addAll(properties); }
        @Override public boolean add(SgfProperty sgfProperty) {
            boolean rc=super.add(sgfProperty);
            setIsAMoveFlags();
            return rc;
        }
        public void setIsAMoveFlags() {
            // http://www.red-bean.com/sgf/user_guide/index.html#move_vs_place says: Therefore it's illegal to mix setup properties and move properties within the same node.
            for(SgfProperty property:this) {
                if(property.p() instanceof Setup) hasASetupType=true;
                if(property.p() instanceof sgf.Move) { hasAMoveType=true; }
                if((property.p().equals(P.W)||property.p().equals(P.B))) hasAMove=true;
            }
            if(hasAMoveType&&hasASetupType) parserLogger.severe("sgf node has move and setup type properties!");
        }
        @Override public String toString() {
            StringBuffer stringBuffer=new StringBuffer(";");
            if(false) { stringBuffer.append("{id="+sgfId); stringBuffer.append("}"); }
            for(Iterator<SgfProperty> i=iterator();i.hasNext();) stringBuffer.append(i.next().toString());
            return stringBuffer.toString();
        }
        boolean hasAMove,hasAMoveType,hasASetupType;
        final int sgfId=sgfIds++;
        static int sgfIds;
        private static final long serialVersionUID=1L;
    }
    public static void main(String[] args) {}
}
