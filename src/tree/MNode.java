package tree;
import io.Logging;
import java.util.*;
import java.util.function.Consumer;
public class MNode<T> {
    public void preorder(Consumer<MNode<T>> consumer) {
        TreeSupport.preorderMway(this,node -> node.children,consumer);
    }
    static <T> void relabel(MNode<T> node,final Iterator<T> i) {
        TreeSupport.relabel(node,i,(target,data) -> target.data=data,
                (root,consumer) -> { if(root!=null) root.preorder(consumer); });
    }
    @Override public String toString() { return "MNode2 [data="+data+"]"; }
    public MNode(T data,MNode<T> parent) {
        // maybe just use t as first argument?
        this.parent=parent;
        this.data=data;
    }
    public static <T> Node<T> from(MNode<T> mNode2) {
        if(mNode2==null) { return null; }
        TreeSupport.markProcessed(processed,mNode2.data,
                mNode2.data+" MNode2 already processed!");
        Node<T> left=null,tail=null;
        for(int i=0;i<mNode2.children.size();++i) {
            MNode<T> child=mNode2.children.get(i);
            if(i==0) {
                if(child!=null) {
                    left=tail=from(child);
                    if(left.right!=null) throw new RuntimeException("wierdness!");
                } else Logging.mainLogger.info("first chile is null!");
            } else {
                Node<T> newRight=from(child);
                tail.right=newRight;
                tail=newRight;
            }
        }
        Node<T> binaryNode=new Node<>(mNode2.data,left,null); // first child
        return binaryNode;
    }
    public static <T> void print(MNode<T> tree,String indent,boolean last) {
        TreeSupport.printMwayTree(tree,indent,last,node -> node.children,node -> node.data);
    }
    @Override public int hashCode() { return Objects.hash(data); }
    @Override public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null) return false;
        if(getClass()!=obj.getClass()) return false;
        MNode<T> other=(MNode<T>)obj;
        if((data==null)!=(other.data==null)) return false;
        boolean equal=data.equals(other.data);
        //if(!equal) if(verbose) Logging.mainLogger.info(data+" "+other.data);
        return equal;
    }
    private boolean deepEquals_(MNode<T> other,boolean ckeckEqual) {
        return TreeSupport.deepEquals(this,other,ckeckEqual,(a,b) -> a.equals(b),mwayChildren());
    }
    public static <T> boolean deepEquals(MNode<T> node,MNode<T> other) {
        return node!=null?node.deepEquals_(other,true):other==null;
    }
    public static <T> boolean structureDeepEquals(MNode<T> node,MNode<T> other) {
        return node!=null?node.deepEquals_(other,false):other==null;
    }
    public static LinkedHashSet<Object> processed() { return processed; }
    public static void clearProcessed() { TreeSupport.clearProcessed(processed); }
    MNode<T> parent;
    ArrayList<MNode<T>> children=new ArrayList<>();
    // add a set temporarily to see if we are adding stuff in twice?
    public T data;
    final int id=++ids;
    static int ids;
    static LinkedHashSet<Object> processed=new LinkedHashSet<>();
    private static <T> TreeSupport.ChildrenAccess<MNode<T>> mwayChildren() {
        return new TreeSupport.ChildrenAccess<MNode<T>>() {
            @Override public int size(MNode<T> node) { return node.children.size(); }
            @Override public MNode<T> childAt(MNode<T> node,int index) { return node.children.get(index); }
        };
    }
}
