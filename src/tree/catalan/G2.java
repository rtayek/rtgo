package tree.catalan;
import java.util.*;
import java.util.function.Consumer;
import utilities.*;
public class G2 {
    static class Node {
        private Node(int data) { this.data=data; }
        private Node(int data,Node left,Node right) { this.data=data; this.left=left; this.right=right; }
        Node left,right,parent;
        public final Integer data;
        String encoded;
        final int id=++ids;
        static int ids;
    }
    static void encode(StringBuffer sb,Node node) { // encode
        if(node==null) sb.append('0');
        else {
            sb.append('1');
            encode(sb,node.left);
            encode(sb,node.right);
            // append data
        }
    }
    static String encode(Node tree) { // to binary string
        // https://oeis.org/search?q=4%2C20%2C24%2C84%2C88%2C100%2C104%2C112&language=english&go=Search
        StringBuffer sb=new StringBuffer();
        encode(sb,tree);
        return sb.toString();
    }
    public static void preOrder(Node node,Consumer<Node> consumer) {
        if(node==null) return;
        consumer.accept(node);
        //System.out.println("1 "+node.data+" "+node.encoded);
        preOrder(node.left,consumer);
        preOrder(node.right,consumer);
    }
    public static void inOrder(Node root) {
        if(root==null) return;
        inOrder(root.left);
        System.out.println("x");
        inOrder(root.right);
    }
    public static void postOrder(Node root) {
        if(root==null) return;
        postOrder(root.left);
        postOrder(root.right);
        System.out.println("x");
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
    static void printStuff(ArrayList<ArrayList<Node>> all,int nodes) {
        ArrayList<Node> trees=all.get(nodes);
        System.out.println(nodes+" nodes.");
        for(int i=0;i<trees.size();++i) {
            System.out.println("tree: "+i);
            Node tree=trees.get(i);
            final Consumer<Node> p=x->pd(x);
            preOrder(tree,p);
            System.out.println();
        }
    }
    static ArrayList<ArrayList<Node>> generate(G2 g2,int nodes) {
        ArrayList<ArrayList<Node>> all=new ArrayList<>();
        Holder<Integer> data=new Holder<>(0);
        for(int i=0;i<=nodes;i++) {
            g2.et.reset();
            ArrayList<Node> trees=g2.all(i,data);
            System.out.println(i+" "+trees.size()+" "+g2.et);
            System.out.println(g2.map.keySet());
            all.add(trees);
            System.gc();
        }
        return all;
    }
    public static void main(String[] arguments) {
        G2 g2=new G2();
        if(arguments!=null&&arguments.length>0) g2.useMap=true;
        int nodes=3;
        ArrayList<ArrayList<Node>> all=generate(g2,nodes);
        printStuff(all,nodes);
        //ArrayList<Integer> data=collectData(node);
        //System.out.println(data);
    }
    boolean useMap;
    Et et=new Et();
    // put all here as an instance variable?
    SortedMap<Integer,ArrayList<Node>> map=new TreeMap<>();
}
