package tree.catalan;
import static tree.catalan.G2.Node.*;
import static utilities.Utilities.implies;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Consumer;
import utilities.*;
public class G2 {
    public static class Node {
        private Node(int data) { this.data=data; }
        private Node(int data,Node left,Node right) { this.data=data; this.left=left; this.right=right; }
        public void preorder(Consumer<Node> consumer) {
            if(consumer!=null) consumer.accept(this);
            //System.out.println("1 "+node.data+" "+node.encoded);
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
        private static void encode(StringBuffer sb,Node node) { // encode
            if(node==null) sb.append('0');
            else {
                sb.append('1');
                encode(sb,node.left);
                encode(sb,node.right);
                // append data
            }
        }
        public static String encode(Node tree) { // to binary string
            // https://oeis.org/search?q=4%2C20%2C24%2C84%2C88%2C100%2C104%2C112&language=english&go=Search
            StringBuffer sb=new StringBuffer();
            encode(sb,tree);
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
            if(bits.size()<=0) return null;
            boolean b=bits.get(0);
            bits.remove(0);
            if(b) {
                int d=data.get(0);
                data.remove(0);
                Node root=new Node(d); // lambda?
                root.left=decode(bits,data);
                root.right=decode(bits,data);
                return root;
            }
            return null;
        }
        static Node decode(String binaryString,List<Integer> data) {
            if(binaryString.equals("")) return null;
            boolean b=binaryString.charAt(0)=='1';
            binaryString=binaryString.substring(1); // remoce
            if(b) {
                int d=data.get(0); // not changing!
                data.remove(0);
                Node root=new Node(d);
                root.left=decode(binaryString,data);
                root.right=decode(binaryString,data);
                return root;
            }
            return null;
        }
        Node left,right,parent;
        public final Integer data;
        String encoded;
        final int id=++ids;
        static int ids;
    }
    static Node decode(List<Boolean> bits,List<Integer> data) {
        if(bits.size()<=0) return null;
        boolean b=bits.get(0);
        bits.remove(0);
        if(b) {
            int d=data.get(0);
            data.remove(0);
            Node root=new Node(d);
            root.left=decode(bits,data);
            root.right=decode(bits,data);
            return root;
        }
        return null;
    }
    static Node decode(String binaryString,List<Integer> data) {
        if(binaryString.equals("")) return null;
        boolean b=binaryString.charAt(0)=='1';
        binaryString=binaryString.substring(1); // remoce
        if(b) {
            int d=data.get(0); // not changing!
            data.remove(0);
            Node root=new Node(d);
            root.left=decode(binaryString,data);
            root.right=decode(binaryString,data);
            return root;
        }
        return null;
    }
    public static Node roundTrip(Node expected) {
        // add string writer and return the tree
        String actual=encode(expected);
        List<Integer> data=new ArrayList<>(sequentialData);
        // need real data!
        Node node2=decode(actual,data);
        return node2;
    }
    public static String roundTrip(String expected) {
        // add string writer and return the tree
        long number=Long.parseLong(expected,2);
        List<Boolean> list=bits(number,expected.length());
        List<Integer> data=new ArrayList<>(sequentialData);
        Node node2=decode(list,data);
        String actual=encode(node2);
        return actual;
    }
    public Node preOrderCopy(Node node) {
        if(node==null) return null;
        Node copy=new Node(node.data);
        copy.left=preOrderCopy(node.left);
        copy.right=preOrderCopy(node.right);
        return copy;
        // do left=, right=, return new node
    }
    public static void preOrder(Node node,Consumer<Node> consumer) {
        if(node==null) return;
        if(consumer!=null) consumer.accept(node);
        //System.out.println("1 "+node.data+" "+node.encoded);
        preOrder(node.left,consumer);
        preOrder(node.right,consumer);
        //  lookslike it need to be postorder to use a lambda
        // otherwise preorder meeds to return a value.
    }
    public static void inOrder(Node node,Consumer<Node> consumer) {
        if(node==null) return;
        inOrder(node.left,consumer);
        System.out.println("x");
        inOrder(node.right,consumer);
    }
    public static void postOrder(Node node,Consumer<Node> consumer) {
        if(node==null) return;
        postOrder(node.left,consumer);
        postOrder(node.right,consumer);
        //System.out.println("x");
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
        Consumer<Node> add=x->datas.add(x.data);
        preOrder(node,add);
        return datas;
    }
    ArrayList<Node> all(int n,Holder<Integer> data) { // https://www.careercup.com/question?id=14945787
        if(useMap) if(map.containsKey(n)) return map.get(n);
        ArrayList<Node> trees=new ArrayList<>();
        if(n==0) trees.add(null);
        else for(int i=0;i<n;i++) {
            for(Node left:all(i,data)) {
                for(Node right:all(n-1-i,data)) {
                    ++data.t;
                    Node node=new Node(data.t,left,right);
                    node.encoded=encode(node);
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
            preOrder(tree,p);
            System.out.println();
        }
        System.out.println("end of nodes "+nodes);
    }
    static ArrayList<ArrayList<Node>> generate(G2 g2,int nodes) {
        ArrayList<ArrayList<Node>> all=new ArrayList<>();
        Holder<Integer> data=new Holder<>(0);
        for(int i=0;i<=nodes;i++) {
            g2.et.reset();
            ArrayList<Node> trees=g2.all(i,data);
            //System.out.println(i+" "+trees.size()+" "+g2.et);
            //System.out.println(g2.map.keySet());
            all.add(trees);
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
        trees=all.get(nodes);
        n=trees.size();
        tree=trees.get(n/2);
        ArrayList<Integer> data=collectData(tree);
        System.out.println();
        System.out.println("collect data: "+data);
        StringBuffer stringBuffer=new StringBuffer();
        toDataString(stringBuffer,tree);
        System.out.println("to data string: "+stringBuffer);
        String expected=encode(tree);
        System.out.println("ex: "+expected);
        Node decoded=decode(expected,data);
        System.out.println("round trip: "+encode(decoded));
        class MyConsumer implements Consumer<Node> {
            @Override public void accept(Node node) { Node newNode=new Node(node.data); copy=newNode; }
            Node copy;
        }
        MyConsumer c2=new MyConsumer();
        postOrder(tree,c2);
        System.out.println("copy: "+c2.copy);
        String actual=encode(c2.copy);
        System.out.println("ac: "+actual);
    }
    public static void main(String[] arguments) {
        List<String> x=ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println(x);
        System.out.println("in eclipse: "+inEclipse());
        G2 g2=new G2();
        if(arguments!=null&&arguments.length>0) g2.useMap=true;
        //if(inEclipse()) g2.useMap=true;
        int nodes=3;
        ArrayList<ArrayList<Node>> all=generate(g2,nodes);
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
        for(int i=0;i<100;++i) sequentialData.add(i);
    }
}
