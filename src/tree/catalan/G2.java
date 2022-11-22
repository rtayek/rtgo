package tree.catalan;
import static tree.catalan.G2.Node.*;
import static utilities.Utilities.implies;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Consumer;
import utilities.*;
public class G2 {
    public static class Node {
        private Node(Integer data) { this.data=data; }
        private Node(Integer data,Node left,Node right) { this.data=data; this.left=left; this.right=right; }
        public void preorder(Consumer<Node> consumer) {
            if(consumer!=null) consumer.accept(this);
            if(left!=null) left.preorder(consumer);
            if(right!=null) right.preorder(consumer);
        }
        public void inorder(Consumer<Node> consumer) {
            if(left!=null) left.inorder(consumer);
            if(consumer!=null) consumer.accept(this);
            if(right!=null) right.inorder(consumer);
        }
        public void postorder(Consumer<Node> consumer) {
            if(left!=null) left.postorder(consumer);
            if(right!=null) right.postorder(consumer);
            if(consumer!=null) consumer.accept(this);
        }
        private static void encode_(StringBuffer sb,Node node,ArrayList<Integer> data) {
            // lambda?
            boolean isNotNull=node!=null;
            if(isNotNull) {
                if(data!=null) data.add(node.data);
                sb.append('1');
                encode_(sb,node.left,data);
                encode_(sb,node.right,data);
            } else {
                sb.append('0');
                if(data!=null) data.add(null);
            }
        }
        public static String encode(Node tree,ArrayList<Integer> data) { // to binary string
            // https://oeis.org/search?q=4%2C20%2C24%2C84%2C88%2C100%2C104%2C112&language=english&go=Search
            StringBuffer sb=new StringBuffer();
            encode_(sb,tree,data);
            return sb.toString();
        }
        public static List<Boolean> bits(long b,int length) {
            List<Boolean> bits=new ArrayList<>();
            for(int i=length;i>=1;b/=2,--i) bits.add(b%2==1?true:false);
            Collections.reverse(bits);
            while(!implies(bits.size()>0,bits.get(0))) bits.remove(0);
            if(bits.size()==0) System.out.println("no bits!");
            return bits;
        }
        static void toDataString(StringBuffer sb,Node node) { // encode
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
        static Node decode(List<Boolean> bits,List<Integer> data) {
            // lambda?
            // get rid of this!
            if(bits.size()<=0) return null;
            boolean b=bits.get(0);
            bits.remove(0);
            if(b) {
                Integer d=data!=null?data.remove(0):null;
                Node root=new Node(d); // lambda?
                root.left=decode(bits,data);
                root.right=decode(bits,data);
                return root;
            }
            return null;
        }
        static Node decode(String binaryString,List<Integer> data) {
            // lambda?
            if(binaryString.equals("")) return null;
            boolean b=binaryString.charAt(0)=='1';
            binaryString=binaryString.substring(1);
            if(b) {
                Integer d=null;
                if(data.size()==0) { System.out.println("data: "+data); d=data!=null?data.remove(0):null; }
                Node root=new Node(d);
                root.left=decode(binaryString,data);
                root.right=decode(binaryString,data);
                return root;
            }
            return null;
        }
        @Override public int hashCode() { return Objects.hash(data); }
        @Override public boolean equals(Object obj) {
            if(this==obj) return true;
            if(obj==null) return false;
            if(getClass()!=obj.getClass()) return false;
            Node other=(Node)obj;
            boolean equal=data==other.data||data.equals(obj);
            if(!equal) System.out.println(data+" "+other.data);
            return data==other.data||data.equals(obj);
        }
        public boolean deepEquals(Node other) {
            // lambda?
            if(this==other) return true;
            else if(other==null) return false;
            else if(!equals(other)) return false;
            if(left!=null) {
                boolean isEqual=left.deepEquals(other.left);
                if(!isEqual) return false;
            } else if(other.left!=null) return false;
            if(right!=null) {
                boolean isEqual=right.deepEquals(other.right);
                if(!isEqual) return false;
            } else if(other.right!=null) return false;
            return true;
        }
        public boolean structureDeepEquals(Node other) {
            // lambda?
            if(this==other) return true;
            else if(other==null) { System.out.println("other is null!"); return false; }
            //else if(!equals(other)) return false;
            if(left!=null) {
                boolean isEqual=left.deepEquals(other.left);
                if(!isEqual) { System.out.println("other.left is not equal!"); return false; }
            } else if(other.left!=null) { System.out.println("other.left is not null!"); return false; }
            if(right!=null) {
                boolean isEqual=right.deepEquals(other.right);
                if(!isEqual) { System.out.println("other.right is not equal!"); return false; }
            } else if(other.right!=null) { System.out.println("other.right is not null!"); return false; }
            return true;
        }
        Node left,right,parent;
        public final Integer data;
        String encoded;
        final int id=++ids;
        static int ids;
    }
    public static Node encodeDecode(Node expected) {
        // add string writer and return the tree
        ArrayList<Integer> data=new ArrayList<>();
        String actualEncoded=encode(expected,data);
        System.out.println("encode tree: "+actualEncoded);
        System.out.println("data: "+data);
        // need real data!
        Node actual=decode(actualEncoded,data);
        return actual;
    }
    public static String decodeEncode(String expected,ArrayList<Integer> data) {
        Node decoded=decode(expected,data);
        String actual=encode(decoded,data);
        if(data!=null) if(data.size()>0) {
            System.out.println("leftover data: "+data);
            //throw new RuntimeException("data us not epty!");
        }
        return actual;
    }
    static String roundTripLong(String expected,long number) {
        List<Boolean> list=bits(number,expected.length());
        ArrayList<Integer> data=new ArrayList<>(sequentialData);
        Node node2=decode(list,data);
        String actual=encode(node2,data);
        return actual;
    }
    public static String roundTripLong(String expected) {
        // add string writer and return the tree
        long number=Long.parseLong(expected,2);
        return roundTripLong(expected,number);
    }
    public Node preOrderCopy(Node node) {
        // lambda?
        if(node==null) return null;
        Node copy=new Node(node.data);
        copy.left=preOrderCopy(node.left);
        copy.right=preOrderCopy(node.right);
        return copy;
        // do left=, right=, return new node
    }
    public static void preOrderx(Node node,Consumer<Node> consumer) {
        if(consumer!=null) consumer.accept(node);
        if(node==null) return;
        //System.out.println("1 "+node.data+" "+node.encoded);
        preOrderx(node.left,consumer);
        preOrderx(node.right,consumer);
        //  lookslike it need to be postorder to use a lambda
        // otherwise preorder meeds to return a value.
    }
    static class MyConsumer implements Consumer<Node> {
        @Override public void accept(Node node) { Node newNode=new Node(node.data); copy=newNode; }
        Node copy,left,right;
    }
    public static void postOrder(Node node,Consumer<Node> consumer) {
        if(node==null) return;
        postOrder(node.left,consumer);
        postOrder(node.right,consumer);
        if(consumer!=null) consumer.accept(node);
    }
    public static void mirror(Node root) {
        if(root==null) return;
        mirror(root.left);
        mirror(root.right);
        Node temp=root.left;
        root.left=root.right;
        root.right=temp;
    }
    static ArrayList<Integer> collectData(Node node) {
        // nodes are getting data set, but this only returns 1!
        final ArrayList<Integer> datas=new ArrayList<>();
        Consumer<Node> add=x->datas.add(x!=null?x.data:null);
        preOrderx(node,add);
        return datas;
    }
    public ArrayList<Node> all(int n,Holder<Integer> data) { // https://www.careercup.com/question?id=14945787
        if(useMap) if(map.containsKey(n)) return map.get(n);
        ArrayList<Node> trees=new ArrayList<>();
        if(n==0) trees.add(null);
        else for(int i=0;i<n;i++) {
            for(Node left:all(i,data)) {
                for(Node right:all(n-1-i,data)) {
                    if(data!=null) ++data.t;
                    Node node=new Node(data.t,left,right);
                    node.encoded=encode(node,null);
                    //System.out.println("all "+node.id+" "+node.data+" "+node.encoded);
                    trees.add(node);
                }
            }
        }
        if(useMap) map.put(n,trees);
        return trees;
    }
    static void p(Node x) { // instance?
        StringBuffer sb=new StringBuffer();
        sb.append("pre ").append(x.id);
        sb.append(' ').append(x.data);
        sb.append(' ').append(x.encoded);
        sb.append(' ').append(x.left!=null?x.left.encoded:"null");
        sb.append(' ').append(x.right!=null?x.right.encoded:"null");
        System.out.println(sb);
    }
    static void pd(Node x) { StringBuffer sb=new StringBuffer(); sb.append(' ').append(x.data); System.out.print(sb); }
    private static void print(Node tree) {
        if(tree==null) return;
        Consumer<Node> p=x->System.out.print(x.data+" ");
        System.out.print("preorder:  ");
        tree.preorder(p);
        System.out.println();
        System.out.print("inorder:   ");
        tree.inorder(p);
        System.out.println();
        System.out.print("postorder: ");
        tree.postorder(p);
        System.out.println();
    }
    static void printStuff(ArrayList<ArrayList<Node>> all,int nodes) {
        ArrayList<Node> trees=all.get(nodes);
        System.out.println(nodes+" nodes.");
        for(int i=0;i<trees.size();++i) {
            System.out.print("tree "+i+": ");
            Node tree=trees.get(i);
            final Consumer<Node> p=x->pd(x);
            preOrderx(tree,p);
            System.out.println();
        }
        System.out.println("end of nodes "+nodes);
    }
    ArrayList<ArrayList<Node>> generate(int nodes) {
        ArrayList<ArrayList<Node>> all=new ArrayList<>();
        Holder<Integer> data=new Holder<>(0);
        for(int i=0;i<=nodes;i++) {
            et.reset();
            ArrayList<Node> trees=all(i,data);
            //System.out.println(i+" "+trees.size()+" "+g2.et);
            //System.out.println(g2.map.keySet());
            all.add(trees); // stop doing this!
            System.gc();
        }
        return all;
    }
    public static boolean inEclipse() {
        String string=System.getProperty("notEclipse");
        boolean notEclipse="true".equalsIgnoreCase(string);
        return !notEclipse;
    }
    static void foo(int nodes,ArrayList<ArrayList<Node>> all,Node tree) {
        ArrayList<Node> trees;
        int n;
        print(tree);
        trees=all.get(nodes); // can change this
        n=trees.size();
        tree=trees.get(n/2);
        System.out.println("encoded tree: "+encode(tree,null));
        ArrayList<Integer> data=collectData(tree);
        System.out.println("collect data: "+data);
        Node oneWay=encodeDecode(tree);
        if(!tree.deepEquals(oneWay)) {
            System.out.println("ex: "+encode(tree,null));
            System.out.println("ac: "+encode(oneWay,null));
            System.out.println("one way round trip failure!");
        }
        StringBuffer stringBuffer=new StringBuffer();
        toDataString(stringBuffer,tree);
        System.out.println("to data string: "+stringBuffer);
        String expected=encode(tree,null);
        System.out.println("ex: "+expected);
        String theOtherWay=decodeEncode(expected,data);
        if(!expected.equals(theOtherWay)) System.out.println("the other way round trip failure!");
        // the above used data
        MyConsumer c2=new MyConsumer();
        postOrder(tree,c2);
        System.out.println("copy: "+encode(c2.copy,null));
        String actual=encode(c2.copy,null);
        System.out.println("ac: "+actual);
        if(!expected.equals(actual)) System.out.println("copy failurefailure!");
        ArrayList<Integer> data2=collectData(c2.copy);
        System.out.println("collect data2: "+data2);
    }
    public static void main(String[] arguments) {
        List<String> x=ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println(x);
        System.out.println("in eclipse: "+inEclipse());
        G2 g2=new G2();
        if(arguments!=null&&arguments.length>0) g2.useMap=true;
        //if(inEclipse()) g2.useMap=true;
        int nodes=2;
        ArrayList<ArrayList<Node>> all=g2.generate(nodes);
        System.out.println(nodes+" nodes.");
        //for(int i=0;i<all.size();++i) printStuff(all,i);
        ArrayList<Node> trees=all.get(nodes);
        int n=trees.size();
        Node tree=trees.get(n/2);
        foo(nodes,all,tree);
    }
    boolean useMap;
    Et et=new Et();
    // put all here as an instance variable?
    SortedMap<Integer,ArrayList<Node>> map=new TreeMap<>();
    static List<Integer> sequentialData=new ArrayList<>();
    static {
        ArrayList<Integer> data=new ArrayList<>();
        for(int i=0;i<100;++i) data.add(i);
        sequentialData=Collections.unmodifiableList(data);
    }
}
