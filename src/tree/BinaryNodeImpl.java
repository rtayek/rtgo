package tree;
import java.util.*;
class BinaryNodeImpl implements tree.Arborescence { // seems to be a clone of sgf node.
    public BinaryNodeImpl(Arborescence left,Arborescence right) { this.left=left; this.right=right; }
    @Override public Arborescence left() { return left; }
    @Override public Arborescence right() { return right; }
    @Override public Arborescence parent() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    @Override public List<Arborescence> siblings() { // right sibs only
        ArrayList<Arborescence> nodes=new ArrayList<>();
        nodes.add(this);
        Arborescence node=null;
        for(node=right();node!=null;node=node.right()) nodes.add(node);
        System.out.println(nodes);
        return nodes;
    }
    @Override public List<Arborescence> children() { return left().siblings(); }
    // how to get all children?
    @Override public List<Arborescence> descendents() {
        ArrayList<Arborescence> nodes=new ArrayList<>();
        nodes.add(this);
        Arborescence node=null;
        for(node=left();node!=null;node=node.left()) nodes.add(node);
        return nodes;
    }
    @Override public void addSibling(Arborescence node) { siblings().add(node); }
    @Override public void addDescendant(Arborescence node) { descendents().add(node); }
    @Override public void addChild(Arborescence node) {
        if(left()==null) { left=node; return; }
        List<Arborescence> siblings=left().siblings();
        siblings.add(node);
        // System.err.println("added node "+node.id+" as child of node "+this.id);
    }
    public MultiNodeImpl toNode() { return Arborescence.toMultiwayNode(this); }
    // should become multiway node?
    public static void main(String[] args) { System.out.println("main"); }
    private Arborescence left,right;
}
