package tree;
import io.Logging;
import static tree.Node.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Consumer;
import tree.BinaryTreeSupport;
import utilities.Et;
import utilities.Iterators.Longs;
public class G2 {
    /*
    Given a general tree with ordered but not indexed children,
    encode the first child as the left child of its parent,
    and each other node as a right child of its (former) sibling.

    The reverse is: Given a binary tree with distinguished left
    and right children, read the left child of a node as its
    first child and the right child as its next sibling.                // is this throwing if there is a variation on the first move in the game?
     */
    public static <T> Node<T> encodeDecode(Node<T> expected) {
        // add string writer and return the tree
        ArrayList<T> data=new ArrayList<>();
        String actualEncoded=encode(expected,data);
        Node<T> actual=decode(actualEncoded,data);
        if(data.size()>0) Logging.mainLogger.info("leftoverdata: "+data);
        return actual;
    }
    public static <T> String decodeEncode(String expected,ArrayList<T> data) {
        ArrayList<T> data0=new ArrayList<>(data);
        Node<T> decoded=decode(expected,data);
        ArrayList<T> data2=new ArrayList<>();
        String actual=encode(decoded,data2);
        if(data!=null) {
            if(data.size()>0) {
                Logging.mainLogger.info("leftover data: "+data);
                //throw new RuntimeException("data us not epty!");
            }
            if(!data0.equals(data2)) {
                Logging.mainLogger.info("d0 "+data0+"!="+data2);
                //throw new RuntimeException(data0+"!="+data2);
            }
        }
        return actual;
    }
    static String roundTripLong(String expected,long number) {
        List<Boolean> list=bits(number,expected.length());
        ArrayList<Long> data=new ArrayList<>(sequentialData);
        // maybe add data as parameter?
        Node<Long> node2=decode(list,data);
        String actual=encode(node2,data);
        return actual;
    }
    public static String roundTripLong(String expected) {
        // add string writer and return the tree
        if(expected.equals("0")) // hack
            return expected;
        // do we really need this long stuff?s
        long number=Long.parseLong(expected,2);
        return roundTripLong(expected,number);
    }
    static class CountingConsumer<T> implements Consumer<Node<T>> {
        @Override public void accept(Node<T> node) { //
            if(node!=null) ++n;
        }
        int n;
    }
    static class MyConsumer<T> implements Consumer<Node<T>> {
        @Override public void accept(Node<T> node) { //
            if(node!=null) { Node<T> newNode=new Node<>(node.data); copy=newNode; }
        }
        Node<T> copy,left,right;
    }
    static <T> ArrayList<T> collectData(Node<T> node) {
        // nodes are getting data set, but this only returns 1!
        final ArrayList<T> datas=new ArrayList<>();
        Consumer<Node<T>> add=x->datas.add(x!=null?x.data:null);
        // make this check for duplicates!
        BinaryTreeSupport.preorder(node,x -> x.left,x -> x.right,add);
        return datas;
    }
    static <T> void p(Node<T> x) { // instance?
        StringBuffer sb=new StringBuffer();
        if(x!=null) {
            sb.append("pre id: ").append(x.id);
            sb.append(", data: ").append(x.data);
            sb.append(", encoded: ").append(x.encoded);
            sb.append(", left: ").append(x.left!=null?(x.left.encoded+" "+x.left.id):"null");
            sb.append(", right: ").append(x.right!=null?(x.right.encoded+" "+x.right.id):"null");
        }
        Logging.mainLogger.info(String.valueOf(sb));
    }
    static <T> void pd(Node<T> x) {
        StringBuffer sb=new StringBuffer();
        sb.append(' ').append(x.data);
        Logging.mainLogger.info(String.valueOf(sb));
    }
    public static <T> void print(Node<T> tree) {
        if(tree==null) return;
        Consumer<Node<T>> p=x->Logging.mainLogger.info(x.data+" ");
        Logging.mainLogger.info("preorder:  ");
        BinaryTreeSupport.preorder(tree,x -> x.left,x -> x.right,p);
        Logging.mainLogger.info("");
        Logging.mainLogger.info("inorder:   ");
        BinaryTreeSupport.inorder(tree,x -> x.left,x -> x.right,p);
        Logging.mainLogger.info("");
        Logging.mainLogger.info("postorder: ");
        BinaryTreeSupport.postorder(tree,x -> x.left,x -> x.right,p);
        Logging.mainLogger.info("");
    }
    public static <T> void printStuff(ArrayList<Node<T>> trees) {
        for(int i=0;i<trees.size();++i) {
            Logging.mainLogger.info("tree "+i+": ");
            Node<T> tree=trees.get(i);
            final Consumer<Node<T>> p=x->pd(x);
            BinaryTreeSupport.preorder(tree,x -> x.left,x -> x.right,p);
            Logging.mainLogger.info("");
        }
    }
    public static <T> void printStuff(ArrayList<ArrayList<Node<T>>> all,int nodes) {
        ArrayList<Node<T>> trees=all.get(nodes);
        Logging.mainLogger.info(nodes+" nodes.");
        printStuff(trees);
        Logging.mainLogger.info("end of nodes "+nodes);
    }
    private static <T> void print(Node<T> node,String prefix,boolean isLeft) {
        // lambda
        if(node!=null) {
            Logging.mainLogger.info(prefix+(isLeft?"|-- ":"\\-- ")+node.data);
            print(node.left,prefix+(isLeft?"|   ":"    "),true);
            print(node.right,prefix+(isLeft?"|   ":"    "),false);
        }
    }
    public static <T> void print(Node<T> node,String prefix) {
        if(node!=null) print(node,prefix,true);
        else Logging.mainLogger.info("0");
    }
    public static <T> void pPrint(Node<T> root,StringBuffer sb) {
        if(root==null) return;
        sb.append(root.data);
        if(root.left==null&&root.right==null) return;
        sb.append('(');
        pPrint(root.left,sb);
        sb.append(')');
        // only if right child is present to
        // avoid extra parenthesis
        if(root.right!=null) { sb.append('('); pPrint(root.right,sb); sb.append(')'); }
    }
    public static <T> String pPrint(Node<T> root) {
        StringBuffer sb=new StringBuffer();
        pPrint(root,sb);
        return sb.toString();
    }
    public static class Generator<T> {
        public Generator(boolean useMap) { this.useMap=useMap; }
        // maybe add useMap flag to all? and make all_?
        private ArrayList<Node<T>> all(int nodes,Iterator<T> iterator) { // https://www.careercup.com/question?id=14945787
            if(useMap) if(map.containsKey(nodes)) return map.get(nodes);
            ArrayList<Node<T>> trees=new ArrayList<>();
            if(nodes==0) trees.add(null);
            else for(int i=0;i<nodes;i++) {
                for(Node<T> left:all(i,iterator)) {
                    for(Node<T> right:all(nodes-1-i,iterator)) {
                        T data=iterator!=null&&iterator.hasNext()?iterator.next():null;
                        Node<T> node=new Node<>(data,left,right);
                        node.encoded=encode(node,null); // ?
                        trees.add(node);
                    }
                }
            }
            if(useMap) if(map.put(nodes,trees)!=null) Logging.mainLogger.info(nodes+" is already in map!");
            return trees;
        }
        public static <T> ArrayList<Node<T>> one(int nodes,Iterator<T> iterator,boolean useMap) {
            Generator<T> generator=new Generator<>(useMap);
            ArrayList<Node<T>> trees=generator.all(nodes,iterator);
            return trees;
        }
        public static ArrayList<ArrayList<Node<Long>>> all(int nodes) {
            ArrayList<ArrayList<Node<Long>>> all=new ArrayList<>();
            Iterator<Long> iterator=new Longs();
            for(int i=0;i<=nodes;++i) {
                ArrayList<Node<Long>> trees=Generator.one(i,iterator,false);
                all.add(trees);
            }
            return all;
        }
        final boolean useMap;
        final TreeMap<Integer,ArrayList<Node<T>>> map=new TreeMap<>();
    }
    public static boolean inEclipse() {
        String string=System.getProperty("notEclipse");
        boolean notEclipse="true".equalsIgnoreCase(string);
        return !notEclipse;
    }
    static void foo(Node<Long> tree) {
        ArrayList<Node<Long>> trees;
        int n;
        print(tree,"");
        print(tree);
        ArrayList<Long> data=collectData(tree);
        Logging.mainLogger.info("collect data: "+data);
        StringBuffer stringBuffer=new StringBuffer();
        toDataString(stringBuffer,tree);
        Logging.mainLogger.info("to data string: "+stringBuffer);
        String expected=encode(tree,null);
        MyConsumer<Long> c2=new MyConsumer<Long>();
        BinaryTreeSupport.postorder(tree,x -> x.left,x -> x.right,c2);
        String actual=encode(c2.copy,null);
        Logging.mainLogger.info("ac: "+actual);
        if(!expected.equals(actual)) Logging.mainLogger.info("copy failure!");
        ArrayList<Long> data2=collectData(c2.copy);
        Logging.mainLogger.info("collect data2: "+data2);
    }
    public static void main(String[] arguments) {
        List<String> x=ManagementFactory.getRuntimeMXBean().getInputArguments();
        Logging.mainLogger.info(String.valueOf(x));
        Logging.mainLogger.info("in eclipse: "+inEclipse());
        G2 g2=new G2();
        if(arguments!=null&&arguments.length>0) g2.useMap=true;
        if(inEclipse()) g2.useMap=true;
        g2.useMap=false;
        int nodes=3;
        ArrayList<ArrayList<Node<Long>>> all=G2.Generator.all(nodes);
        Logging.mainLogger.info(nodes+" nodes.");
        if(false) { check(all.get(2).get(0)); return; }
        if(false) for(int i=0;i<=nodes;i++) {
            Logging.mainLogger.info("check "+i+" nodes.");
            ArrayList<Node<Long>> trees=all.get(i);
            int n=0;
            for(Node<Long> expected:trees) {
                check(expected);
                G2.print(expected,"");
                //G2.print(expected);
            }
        }
        ArrayList<Node<Long>> some=all.get(3);
        for(int i=0;i<some.size();++i) {
            Logging.mainLogger.info("tree "+i+": ");
            Node<Long> tree=some.get(i);
            final Consumer<Node<Long>> p=x1->pd(x1);
            BinaryTreeSupport.preorder(tree,x -> x.left,x -> x.right,p);
            Logging.mainLogger.info("");
            print(tree,"");
            Logging.mainLogger.info(String.valueOf(pPrint(tree)));
        }
        Node<Character> redBean=RedBean.binary();
        Logging.mainLogger.info(String.valueOf(pPrint(redBean)));
        if(true) return;
        ArrayList<Node<Long>> trees=all.get(nodes);
        int n=trees.size();
        Node<Long> tree=trees.get(n/2);
        foo(tree);
    }
    boolean useMap; // set in main and never used.
    Et et=new Et();
    // put all here as an instance variable?
    SortedMap<Integer,ArrayList<Node<Integer>>> map=new TreeMap<>();
    static List<Long> sequentialData=new ArrayList<>();
    static final int maxNodes=100;
    static Boolean verbose=false;
    static {
        ArrayList<Long> data=new ArrayList<>();
        for(int i=0;i<maxNodes;++i) data.add((long)i); // start at 1?
        sequentialData=Collections.unmodifiableList(data);
        //Logging.mainLogger.info("end static init.");
    }
}
