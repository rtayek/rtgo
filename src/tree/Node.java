package tree;
import static tree.G2.*;
import static utilities.Utilities.*;
import java.util.*;
import io.Logging;
import tree.G2.CountingConsumer;
public class Node<T> {
    public Node(T data) { this.data=data; }
    public Node(T data,Node<T> left,Node<T> right) { this.data=data; this.left=left; this.right=right; }
    public static <T> Node<T> copy(Node<T> node) {
        if(node==null) return null;
        Node<T> copy=new Node<>(node.data,node.left,node.right);
        copy.left=(copy(node.left));
        copy.right=(copy(node.right));
        return copy;
    }
    public static <T,U> Node<U> reLabelCopy_(Node<T> node,Iterator<U> iterator) {
        if(node==null) return null;
        U data=iterator!=null&&iterator.hasNext()?iterator.next():null;
        Node<U> copy=new Node<U>(data);
        copy.left=reLabelCopy_(node.left,iterator);
        copy.right=reLabelCopy_(node.right,iterator);
        return copy;
    }
    public static <T,U> Node<U> reLabelCopy(Node<T> node,Iterator<U> iterator) {
        if(node==null) return null;
        Node<U> copy=reLabelCopy_(node,iterator);
        return copy;
    }
    public static <T> void mirror(Node<T> root) {
        if(root==null) return;
        mirror(root.left);
        mirror(root.right);
        Node<T> temp=root.left;
        root.left=root.right;
        root.right=temp;
    }
    private static <T> void encode_(StringBuilder sb,Node<T> node,ArrayList<T> data) {
        // lambda?
        // use consumer of node instead of array list of node?
        boolean isNotNull=node!=null;
        if(isNotNull) {
            if(data!=null) data.add(node.data);
            sb.append('1');
            encode_(sb,node.left,data);
            encode_(sb,node.right,data);
        } else {
            sb.append('0');
            //if(data!=null) data.add(null); // maybe not?
        }
    }
    public static <T> String encode(Node<T> tree,ArrayList<T> data) { // to binary string
        // https://oeis.org/search?q=4%2C20%2C24%2C84%2C88%2C100%2C104%2C112&language=english&go=Search
        StringBuilder sb=new StringBuilder();
        encode_(sb,tree,data);
        return sb.toString();
    }
    public static List<Boolean> bits(long b,int length) {
        List<Boolean> bits=new ArrayList<>();
        for(int i=length;i>=1;b/=2,--i) bits.add(b%2==1?true:false);
        Collections.reverse(bits);
        while(!implies(bits.size()>0,bits.get(0))) { bits.remove(0); }
        if(bits.size()==0) Logging.mainLogger.info("no bits!");
        return bits;
    }
    static <T> void toDataString(StringBuilder sb,Node<T> node) { // encode
        // lambda?
        if(node==null) sb.append('0');
        else {
            sb.append(node.data);
            sb.append('(');
            toDataString(sb,node.left);
            toDataString(sb,node.right);
            sb.append(')');
        }
    }
    static <T> Node<T> decode(List<Boolean> bits,List<T> data) {
        return decode(new BooleanListReader(bits),data,false);
    }
    static <T> Node<T> decode_(List<Character> binaryString,List<T> data) {
        return decode(new CharListReader(binaryString),data,true);
    }
    public static <T> Node<T> decode(String binaryString,List<T> data) {
        List<Character> characters=Arrays.asList(toObjects(binaryString.toCharArray()));
        return decode_(new ArrayList<>(characters),data);
    }
    private static <T> Node<T> decode(BitReader bits,List<T> data,boolean setEncoded) {
        if(!bits.hasNext()) return null;
        boolean b=bits.next();
        if(b) {
            T d=data!=null?data.remove(0):null;
            Node<T> node=new Node<>(d);
            node.left=decode(bits,data,setEncoded);
            node.right=decode(bits,data,setEncoded);
            if(setEncoded) node.encoded=encode(node,null);
            return node;
        }
        return null;
    }
    private interface BitReader {
        boolean hasNext();
        boolean next();
    }
    private static final class BooleanListReader implements BitReader {
        private final List<Boolean> bits;
        private BooleanListReader(List<Boolean> bits) { this.bits=bits; }
        @Override public boolean hasNext() { return bits.size()>0; }
        @Override public boolean next() { return bits.remove(0); }
    }
    private static final class CharListReader implements BitReader {
        private final List<Character> bits;
        private CharListReader(List<Character> bits) { this.bits=bits; }
        @Override public boolean hasNext() { return bits.size()>0; }
        @Override public boolean next() { return bits.remove(0)=='1'; }
    }
    @Override public int hashCode() { return Objects.hash(data); }
    @Override public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null) return false;
        if(getClass()!=obj.getClass()) return false;
        @SuppressWarnings("unchecked") Node<T> other=(Node<T>)obj;
        if((data==null)!=(other.data==null)) return false;
        boolean equal=data.equals(other.data);
        if(!equal) if(verbose) Logging.mainLogger.info(data+" "+other.data);
        return equal;
    }
    public boolean deepEquals_(Node<T> other,boolean ckeckEqual) {
        return TreeSupport.deepEquals(this,other,ckeckEqual,(a,b) -> a.equals(b),binaryChildren());
    }
    @Override public String toString() { return "Node [data="+data+"]"; }
    public static <T> boolean deepEquals(Node<T> node,Node<T> other) {
        return node!=null?node.deepEquals_(other,true):other==null;
    }
    public static <T> int count(Node<T> node) {
        CountingConsumer<T> consumer=new CountingConsumer<>();
        BinaryTreeSupport.postorder(node,x -> x.left,x -> x.right,consumer);
        return consumer.n;
    }
    public static <T> boolean structureDeepEquals(Node<T> node,Node<T> other) {
        return node!=null?node.deepEquals_(other,false):other==null;
    }
    static <T> int check(Node<T> expected) {
        int n=0;
        //Logging.mainLogger.info("check: "+expected);
        Node<T> actual=encodeDecode(expected);
        if(!structureDeepEquals(expected,actual)) { ++n; Logging.mainLogger.info("0 "+expected+"!="+actual); }
        //if(true) return n;
        ArrayList<T> data=new ArrayList<>();
        String expectedEncoded=encode(expected,data);
        if(expectedEncoded.length()!=data.size()) Logging.mainLogger.info("encoded length!=data size!");
        String actualEncoded=roundTripLong(expectedEncoded);
        if(!expectedEncoded.equals(actualEncoded)) { ++n; Logging.mainLogger.info("1 "+expectedEncoded+"!="+actualEncoded); }
        String actualEncoded2=decodeEncode(expectedEncoded,data);
        if(data.size()>0) Logging.mainLogger.info("data size is >0!");
        if(!expectedEncoded.equals(actualEncoded2)) {
            ++n;
            Logging.mainLogger.info("2 "+expectedEncoded+"!="+actualEncoded);
        }
        return n;
    }
    // Given a general tree with ordered but not indexed children,
    // encode the first child as the left child of its parent,
    // and each other node as a right child of its (former) sibling.
    // Given a binary tree with distinguished left and right children,
    // read the left child of a node as its first child and the right child as its next sibling.
    private static <T> MNode<T> from_(Node<T> node,MNode<T> grandParent) {
        if(node==null) { if(grandParent!=null) grandParent.children.add(null); return null; }
        //Logging.mainLogger.info("processing: "+node.data+" <<<<<<");
        boolean ok=TreeSupport.markProcessed(processed,node.data,
                node.data+" "+node.id+"  node already processed!");
        if(!ok) return null;
        MNode<T> parent=new MNode<T>(node.data,grandParent);
        if(grandParent!=null) grandParent.children.add(parent);
        else Logging.mainLogger.info("gradparent is null!");
        if(node.left!=null) {
            for(Node<T> n=node.left;n!=null;n=n.right) {
                if(verbose) if(processed.contains(n.data)) Logging.mainLogger.info("1 will be already processed"+n);
                MNode<T> newMNode2=from_(n,parent);
            }
        } else if(node.right!=null) Logging.mainLogger.info("can not be a game!");
        // this seems to work, but it's different from my MNode's!
        if(node.right!=null) {
            if(verbose)
                if(processed.contains(node.right.data)) Logging.mainLogger.info("2 will be already processed"+node.right);
            MNode<T> newMNode2=from_(node.right,grandParent);
        }
        //Logging.mainLogger.info("end processing: "+node.data+" >>>>>>");
        return parent;
    }
    public static <T> MNode<T> from(Node<T> node) {
        //if(node==null) return null; // maybe return and empty root! (my MNode root)
        if(node!=null&&node.right!=null) { Logging.mainLogger.info("binaryNode.right is non null!"); }
        Node<T> extra=new Node<>(null);
        extra.left=node; // might be null
        MNode<T> extraMNode2=new MNode<>(null,null);
        clearProcessed();
        MNode<T> mNode2=from_(extra.left,extraMNode2);
        //mroot.data=0l;
        return extraMNode2;
    }
    static <T> void relabel(Node<T> node,final Iterator<T> i) {
        TreeSupport.relabel(node,i,(target,data) -> target.data=data,
                (root,consumer) -> BinaryTreeSupport.preorder(root,x -> x.left,x -> x.right,consumer));
    }
    Node<T> left,right;
    public T data;
    String encoded;
    final int id=++ids;
    static int ids;
    static Set<Object> processed=new LinkedHashSet<>();
    public static Set<Object> processed() { return processed; }
    public static void clearProcessed() { TreeSupport.clearProcessed(processed); }
    @SuppressWarnings("rawtypes")
    private static final TreeSupport.ChildrenAccess<Node> BINARY_CHILDREN=new TreeSupport.ChildrenAccess<Node>() {
        @Override public int size(Node node) { return 2; }
        @Override public Node childAt(Node node,int index) { return index==0?node.left:node.right; }
    };
    @SuppressWarnings("unchecked")
    private static <T> TreeSupport.ChildrenAccess<Node<T>> binaryChildren() {
        return (TreeSupport.ChildrenAccess<Node<T>>)BINARY_CHILDREN;
    }
}
