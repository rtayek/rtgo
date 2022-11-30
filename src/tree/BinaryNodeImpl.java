package tree;
import java.util.*;
class BinaryNodeImpl extends tree.Arborescence.ABC { // seems to be a clone of sgf node.
    public BinaryNodeImpl(Arborescence left,Arborescence right,Arborescence parent) {
        this.left=left;
        this.right=right;
        this.parent=parent;
    }
    @Override public Arborescence left() { return left; }
    @Override public Arborescence right() { return right; }
    @Override public Arborescence parent() { return parent; }
    @Override public List<Arborescence> siblings() { // right sibs only
        ArrayList<Arborescence> nodes=new ArrayList<>();
        nodes.add(this);
        Arborescence node=null;
        for(node=right();node!=null;node=node.right()) nodes.add(node);
        System.out.println("siblingds: "+nodes);
        return nodes;
    }
    @Override public List<Arborescence> children() { return left()!=null?left().siblings():Collections.emptyList(); }
    @Override public List<Arborescence> descendents() {
        ArrayList<Arborescence> nodes=new ArrayList<>();
        nodes.add(this);
        Arborescence node=null;
        for(node=left();node!=null;node=node.left()) nodes.add(node);
        return nodes;
    }
    public Arborescence lastSibling() {
        Arborescence node=this;
        while(node.right()!=null) node=node.right();
        return node;
    }
    @Override public void addSibling(Arborescence node) {
        Arborescence lastSibling=lastSibling();
        if(lastSibling==null) lastSibling=this;
        //lastSibling.right=node;
        // problem here. how to set right
        Arborescence oldRight=right();
        //node.parent=right=node;
        //node.right=oldRight;
        // want to set this.right=node and nold.right=oldRight;
        // and node.parent-parent()
        // maybe we need the set methods?
        // we can postpone this for now
        System.out.println("my parent is: "+parent());
        System.out.println(node+"'s parent is: "+node.parent());
        if(!parent().equals(node.parent())) throw new RuntimeException(node+" sibling has wrong parent.");
        siblings().add(node);
    }
    @Override public void addDescendant(Arborescence node) { descendents().add(node); }
    @Override public void addChild(Arborescence node) {
        if(!this.equals(node.parent())) throw new RuntimeException(node+" child has wrong parent.");
        if(left()==null) { // first child
            left=node;
            return;
        }
        List<Arborescence> siblings=left().siblings();
        System.out.println("before "+siblings);
        siblings.add(node);
        System.out.println("after "+siblings);
        // System.err.println("added node "+node.id+" as child of node "+this.id);
    }
    public MultiNodeImpl toNode() { return Arborescence.toMultiwayNode(this); }
    // should become multiway node?
    public static void main(String[] args) { System.out.println("main"); }
    private Arborescence left,right,parent;
}
