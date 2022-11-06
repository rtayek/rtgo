package tree;
import java.util.List;
interface Arborescence { // long name so as not to be confused with sgf.Node.
    Arborescence left();
    Arborescence right();
    Arborescence parent() throws UnsupportedOperationException;
    List<Arborescence> siblings();
    List<Arborescence> children(); // we should be able to always do this
    List<Arborescence> descendents();
    void addSibling(Arborescence node);
    void addDescendant(Arborescence node);
    void addChild(Arborescence node);
    default void preOrder(Arborescence root) {
        if(root==null) return;
        System.out.println("x");
        preOrder(root.left());
        preOrder(root.right());
    }
    default void inOrder(Arborescence root) {
        if(root==null) return;
        inOrder(root.left());
        System.out.println("x");
        inOrder(root.right());
    }
    default void postOrder(Arborescence root) {
        if(root==null) return;
        inOrder(root.left());
        inOrder(root.right());
        System.out.println("x");
    }
    default boolean isStrange() { return isStrange(this); }
    static boolean isStrange(Arborescence node) {
        if(node==null) return false;
        if(node.left()!=null) {
            if(isStrange(node.left())) return true;
            if(node.right()!=null) if(isStrange(node.right())) return true;
        } else if(node.right()!=null) return true;
        return false;
    }
    default BinaryNodeImpl toBinaryNode() { return Arborescence.toBinaryNode(this); }
    static BinaryNodeImpl toBinaryNode(Arborescence node) {
        // what if it is already a binary node?
        if(node==null) return null;
        // first child is left, remaining are right of left
        // ...
        Arborescence right=null,left=node.children().size()>0?toBinaryNode(node.children().get(0)):null;
        for(int i=node.children().size()-1;i>=1;--i)
            // why is this going backwards?
            right=toBinaryNode(node.children().get(i));
        BinaryNodeImpl binaryNode=new BinaryNodeImpl(left,right);
        return binaryNode;
    }
    default MultiNodeImpl toMultiwayNode() { return Arborescence.toMultiwayNode(this); }
    static MultiNodeImpl toMultiwayNode(Arborescence node) {
        // what if it is already a multiway node?
        if(node==null) return null;
        MultiNodeImpl parent=new MultiNodeImpl(null);
        if(node.left()!=null) {
            MultiNodeImpl child=toMultiwayNode(node.left());
            parent.children.add(child);
            // node.addSibling(node); // will this work?
            if(node.right()!=null) {
                child=toMultiwayNode(node.right());
                parent.children.add(child);
                // node.addSibling(node); // will this work?
            }
        } else if(node.right()!=null) {
            //System.out.println("strange!");
            if(!isStrange(node)) System.out.println("isStrange() disagrees!");
        }
        return parent;
    }
}