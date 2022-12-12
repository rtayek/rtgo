package tree;
import static tree.G2.*;
import static utilities.Utilities.*;
import java.util.*;
import java.util.function.Consumer;
import io.Logging;
import tree.G2.CountingConsumer;
public class Node<T> {
    public Node(T data) { this.data=data; }
    public Node(T data,Node<T> left,Node<T> right) { this.data=data; this.left=left; this.right=right; }
    public void preorder(Consumer<Node<T>> consumer) {
        if(consumer!=null) consumer.accept(this);
        if(left!=null) left.preorder(consumer);
        if(right!=null) right.preorder(consumer);
    }
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
    public void inorder(Consumer<Node<T>> consumer) {
        if(left!=null) left.inorder(consumer);
        if(consumer!=null) consumer.accept(this);
        if(right!=null) right.inorder(consumer);
    }
    public void postorder(Consumer<Node<T>> consumer) {
        if(left!=null) left.postorder(consumer);
        if(right!=null) right.postorder(consumer);
        if(consumer!=null) consumer.accept(this);
    }
    public static <T> void preorder(Node<T> node,Consumer<Node<T>> consumer) {
        if(node==null) return;
        node.preorder(consumer);
    }
    public static <T> void inorder(Node<T> node,Consumer<Node<T>> consumer) {
        if(node==null) return;
        node.inorder(consumer);
    }
    public static <T> void postorder(Node<T> node,Consumer<Node<T>> consumer) {
        if(node==null) return;
        node.postorder(consumer);
    }
    public static <T> void mirror(Node<T> root) {
        if(root==null) return;
        mirror(root.left);
        mirror(root.right);
        Node<T> temp=root.left;
        root.left=root.right;
        root.right=temp;
    }
    private static <T> void encode_(StringBuffer sb,Node<T> node,ArrayList<T> data) {
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
        StringBuffer sb=new StringBuffer();
        encode_(sb,tree,data);
        return sb.toString();
    }
    public static List<Boolean> bits(long b,int length) {
        List<Boolean> bits=new ArrayList<>();
        for(int i=length;i>=1;b/=2,--i) bits.add(b%2==1?true:false);
        Collections.reverse(bits);
        while(!implies(bits.size()>0,bits.get(0))) { bits.remove(0); }
        if(bits.size()==0) System.out.println("no bits!");
        return bits;
    }
    static <T> void toDataString(StringBuffer sb,Node<T> node) { // encode
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
        // lambda?
        // get rid of this!
        if(bits.size()==0) return null;
        boolean b=bits.get(0);
        bits.remove(0);
        if(b) {
            T d=data!=null?data.remove(0):null;
            Node<T> root=new Node<>(d); // lambda?
            root.left=decode(bits,data);
            root.right=decode(bits,data);
            return root;
        }
        return null;
    }
    static <T> Node<T> decode_(List<Character> binaryString,List<T> data) {
        // lambda?
        if(binaryString.size()==0) return null;
        //System.out.println("decodingL "+binaryString);
        boolean b=binaryString.remove(0)=='1';
        if(b) {
            T d=data!=null?data.remove(0):null;
            Node<T> node=new Node<>(d);
            node.left=decode_(binaryString,data);
            node.right=decode_(binaryString,data);
            node.encoded=encode(node,null);
            return node;
        }
        //T d=data!=null?data.remove(0):null;
        return null;
    }
    public static <T> Node<T> decode(String binaryString,List<T> data) {
        List<Character> characters=Arrays.asList(toObjects(binaryString.toCharArray()));
        return decode_(new ArrayList<>(characters),data);
    }
    @Override public int hashCode() { return Objects.hash(data); }
    @Override public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null) return false;
        if(getClass()!=obj.getClass()) return false;
        @SuppressWarnings("unchecked") Node<T> other=(Node<T>)obj;
        if((data==null)!=(other.data==null)) return false;
        boolean equal=data.equals(other.data);
        if(!equal) if(verbose) System.out.println(data+" "+other.data);
        return equal;
    }
    public boolean deepEquals_(Node<T> other,boolean ckeckEqual) {
        // lambda?
        if(this==other) return true;
        else if(other==null) { if(verbose) System.out.println(data+" othe ris null!"); return false; }
        if(ckeckEqual) if(!equals(other)) { if(verbose) System.out.println(data+" "+other.data); return false; }
        if(left!=null) {
            boolean isEqual=left.deepEquals_(other.left,ckeckEqual);
            if(!isEqual) { if(verbose) System.out.println(left.data+"!="+other.left.data); return false; }
            if(verbose) System.out.println(left.data+"=="+other.left.data);
        } else if(other.left!=null) { if(verbose) System.out.println(data+" othe left is null!"); return false; }
        if(right!=null) {
            boolean isEqual=right.deepEquals_(other.right,ckeckEqual);
            if(!isEqual) { if(verbose) System.out.println(right.data+"!="+other.right.data); return false; }
            if(verbose) System.out.println(right.data+"=="+other.right.data);
        } else if(other.right!=null) {
            if(verbose) System.out.println(data+" othe right is not null!");
            if(verbose) System.out.println("other right "+other.right.data);
            return false;
        }
        return true;
    }
    @Override public String toString() { return "Node [data="+data+"]"; }
    public static <T> boolean deepEquals(Node<T> node,Node<T> other) {
        return node!=null?node.deepEquals_(other,true):other==null;
    }
    public static <T> int count(Node<T> node) {
        CountingConsumer<T> consumer=new CountingConsumer<>();
        if(node!=null) Node.postorder(node,consumer);
        return consumer.n;
    }
    public static <T> boolean structureDeepEquals(Node<T> node,Node<T> other) {
        return node!=null?node.deepEquals_(other,false):other==null;
    }
    static <T> int check(Node<T> expected) {
        int n=0;
        //System.out.println("check: "+expected);
        Node<T> actual=encodeDecode(expected);
        if(!structureDeepEquals(expected,actual)) { ++n; System.out.println("0 "+expected+"!="+actual); }
        //if(true) return n;
        ArrayList<T> data=new ArrayList<>();
        String expectedEncoded=encode(expected,data);
        if(expectedEncoded.length()!=data.size()) System.out.println("encoded length!=data size!");
        String actualEncoded=roundTripLong(expectedEncoded);
        if(!expectedEncoded.equals(actualEncoded)) { ++n; System.out.println("1 "+expectedEncoded+"!="+actualEncoded); }
        String actualEncoded2=decodeEncode(expectedEncoded,data);
        if(data.size()>0) System.out.println("data size is >0!");
        if(!expectedEncoded.equals(actualEncoded2)) {
            ++n;
            System.out.println("2 "+expectedEncoded+"!="+actualEncoded);
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
        //System.out.println("processing: "+node.data+" <<<<<<");
        boolean ok=processed.add(node.data);
        if(!ok) { System.out.println(node.data+" "+node.id+"  node already processed!"); return null; }
        MNode<T> parent=new MNode<T>(node.data,grandParent);
        if(grandParent!=null) grandParent.children.add(parent);
        else System.out.println("gradparent is null!");
        if(node.left!=null) {
            for(Node<T> n=node.left;n!=null;n=n.right) {
                if(verbose) if(processed.contains(n.data)) System.out.println("1 will be already processed"+n);
                MNode<T> newMNode2=from_(n,parent);
            }
        } else if(node.right!=null) System.out.println("can not be a game!");
        // this seems to work, but it's different from my MNode's!
        if(node.right!=null) {
            if(verbose)
                if(processed.contains(node.right.data)) System.out.println("2 will be already processed"+node.right);
            MNode<T> newMNode2=from_(node.right,grandParent);
        }
        //System.out.println("end processing: "+node.data+" >>>>>>");
        return parent;
    }
    public static <T> MNode<T> from(Node<T> node) {
        //if(node==null) return null; // maybe return and empty root! (my MNode root)
        if(node!=null&&node.right!=null) { Logging.mainLogger.info("binaryNode.right is non null!"); }
        Node<T> extra=new Node<>(null);
        extra.left=node; // might be null
        MNode<T> extraMNode2=new MNode<>(null,null);
        processed.clear();
        MNode<T> mNode2=from_(extra.left,extraMNode2);
        //mroot.data=0l;
        return extraMNode2;
    }
    static <T> void relabel(Node<T> node,final Iterator<T> i) {
        Consumer<Node<T>> relabel=x-> { if(x!=null&&i!=null) x.data=i.hasNext()?i.next():null; };
        preorder(node,relabel);
    }
    Node<T> left,right,parent;
    public T data;
    String encoded;
    final int id=++ids;
    static int ids;
    static LinkedHashSet processed=new LinkedHashSet<>();
}
