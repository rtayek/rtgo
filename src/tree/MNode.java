package tree;
import io.Logging;
import java.util.*;
import java.util.function.Consumer;
public class MNode<T> {
    public void preorder(Consumer<MNode<T>> consumer) {
        if(consumer!=null) consumer.accept(this);
        for(MNode<T> child:children) if(child!=null) child.preorder(consumer);
    }
    public static <T> void preorder(MNode<T> mNode2,Consumer<MNode<T>> consumer) {
        if(mNode2!=null) mNode2.preorder(consumer);
    }
    static <T> void relabel(MNode<T> node,final Iterator<T> i) {
        Consumer<MNode<T>> relabel=x-> { if(x!=null&&i!=null) x.data=i.hasNext()?i.next():null; };
        preorder(node,relabel);
    }
    @Override public String toString() { return "MNode2 [data="+data+"]"; }
    public MNode(T data,MNode<T> parent) {
        // maybe just use t as first argument?
        this.parent=parent;
        this.data=data;
    }
    public static <T> Node<T> from(MNode<T> mNode2) {
        if(mNode2==null) { return null; }
        boolean ok=processed.add(mNode2.data);
        if(!ok) Logging.mainLogger.info(mNode2.data+" MNode2 already processed!");
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
        Logging.mainLogger.info(indent+"+- "+(tree!=null?tree.data:"0"));
        indent+=last?"   ":"|  ";
        if(tree!=null) for(int i=0;i<tree.children.size();i++) {
            print(tree.children.get(i),indent,i==tree.children.size()-1);
        }
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
        // lambda?
        if(this==other) return true;
        else if(other==null) return false;
        if(ckeckEqual) {
            //Logging.mainLogger.info("cheching: "+this+" "+other);
            if(!equals(other)) return false;
        }
        if(children.size()!=other.children.size()) return false;
        for(int i=0;i<children.size();++i) {
            MNode<T> child=children.get(i);
            MNode<T> otherChild=other.children.get(i);
            if(!child.deepEquals_(otherChild,ckeckEqual)) return false;
        }
        return true;
    }
    public static <T> boolean deepEquals(MNode<T> node,MNode<T> other) {
        return node!=null?node.deepEquals_(other,true):other==null;
    }
    public static <T> boolean structureDeepEquals(MNode<T> node,MNode<T> other) {
        return node!=null?node.deepEquals_(other,false):other==null;
    }
    public static LinkedHashSet<Object> processed() { return processed; }
    MNode<T> parent;
    ArrayList<MNode<T>> children=new ArrayList<>();
    // add a set temporarily to see if we are adding stuff in twice?
    public T data;
    final int id=++ids;
    static int ids;
    static LinkedHashSet<Object> processed=new LinkedHashSet<>();
}
