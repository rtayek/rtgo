package tree;
import java.util.ArrayList;
import java.util.Collections;
//http://en.wikipedia.org/wiki/Binary_tree#Encoding_general_trees_as_binary_trees
//http://blogs.msdn.com/b/ericlippert/archive/2010/04/19/every-binary-tree-there-is.aspx
import java.util.List;
import sgf.SgfProperty;
public class MultiNodeImpl implements Arborescence {
    public MultiNodeImpl(MultiNodeImpl parent) { this.parent=parent; }
    @Override public Arborescence left() { return children.size()>0?children().iterator().next():null; }
    @Override public Arborescence right() {
        int index;
        return (index=children().indexOf(this))>0?children().get(index+1):null;
    }
    @Override public Arborescence parent() throws UnsupportedOperationException { return parent; }
    @Override public List<Arborescence> siblings() {
        return parent!=null?parent.children:Collections.emptyList(); }
    @Override public List<Arborescence> children() { return children; }
    @Override public List<Arborescence> descendents() {
        // should work, but may not be best
        ArrayList<Arborescence> nodes=new ArrayList<>();
        nodes.add(this);
        Arborescence node=null;
        for(node=left();node!=null;node=node.left()) nodes.add(node);
        return nodes;
    }
    @Override public void addSibling(Arborescence node) { siblings().add(node); }
    @Override public void addDescendant(Arborescence node) { descendents().add(node); }
    @Override public void addChild(Arborescence node) { children().add(node); }
    public final MultiNodeImpl parent;
    public ArrayList<Arborescence> children=new ArrayList<>();
    public List<SgfProperty> properties=new ArrayList<>();
}
