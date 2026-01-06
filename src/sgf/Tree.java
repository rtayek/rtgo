package sgf;
import static io.Logging.parserLogger;
import java.util.*;
import java.util.function.*;
import tree.BinaryTreeSupport;
import utilities.Holder;
public interface Tree {
    public static class BinaryNode<T> {
        //BinaryNode(T t) { this.t=t; }
        GeneralNodee<T> convert() { return null; }
        BinaryNode(BinaryNode<T> left,BinaryNode<T> right,T t) { this.left=left; this.right=right; this.t=t; }
        private BinaryNode<T> lastSibling_(Holder<Integer> h) {
            return BinaryTreeSupport.lastSibling(this,node -> node.right,h);
        }
        private BinaryNode<T> lastDescendant_(Holder<Integer> h) {
            return BinaryTreeSupport.lastDescendant(this,node -> node.left,h);
        }
        protected BinaryNode<T> lastSibling() { return lastSibling_(new Holder<Integer>(0)); }
        int siblings() { return BinaryTreeSupport.siblingCount(this,node -> node.right); }
        protected BinaryNode<T> lastDescendant() { return lastDescendant_(new Holder<Integer>(0)); }
        void preorder(Consumer<BinaryNode<T>> consumer) {
            BinaryTreeSupport.preorder(this,node -> node.left,node -> node.right,consumer);
        }
        void preorder(Predicate<BinaryNode<T>> predicate) {
            BinaryTreeSupport.preorder(this,node -> node.left,node -> node.right,predicate);
        }
        void inorder(Consumer<BinaryNode<T>> consumer) {
            BinaryTreeSupport.inorder(this,node -> node.left,node -> node.right,consumer);
        }
        void postorder(Consumer<BinaryNode<T>> consumer) {
            BinaryTreeSupport.postorder(this,node -> node.left,node -> node.right,consumer);
        }
        void addSibling(BinaryNode<T> node) {
            BinaryTreeSupport.appendSibling(this,n -> n.right,(parent,sibling) -> parent.right=sibling,node);
        }
        private BinaryNode<T> lastChild() { return BinaryTreeSupport.lastChild(left,n -> n.right); }
        private void addDescendant(T t) {
            BinaryTreeSupport.appendDescendant(this,n -> n.left,(parent,child) -> parent.left=child,
                    new BinaryNode<T>(null,null,t));
        }
        private int children() {
            return BinaryTreeSupport.childCount(left,node -> node.right);
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
