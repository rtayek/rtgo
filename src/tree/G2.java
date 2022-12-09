package tree;
import static tree.Node.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Consumer;
import utilities.Et;
public class G2 {
    public static class Longs implements Iterator<Long> {
        @Override public boolean hasNext() { return n<Long.MAX_VALUE; }
        @Override public Long next() { return n++; }
        Long n=2l; // -1?
    }
    public static class Characters implements Iterator<Character> {
        @Override public boolean hasNext() { return character<Character.MAX_VALUE; }
        @Override public Character next() { return ++character; }
        Character character='a'+2;
    }
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
        if(data.size()>0) System.out.println("leftoverdata: "+data);
        return actual;
    }
    public static <T> String decodeEncode(String expected,ArrayList<T> data) {
        ArrayList<T> data0=new ArrayList<>(data);
        Node<T> decoded=decode(expected,data);
        ArrayList<T> data2=new ArrayList<>();
        String actual=encode(decoded,data2);
        if(data!=null) {
            if(data.size()>0) {
                System.out.println("leftover data: "+data);
                //throw new RuntimeException("data us not epty!");
            }
            if(!data0.equals(data2)) {
                System.out.println("d0 "+data0+"!="+data2);
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
        preorder(node,add);
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
        System.out.println(sb);
    }
    static <T> void pd(Node<T> x) {
        StringBuffer sb=new StringBuffer();
        sb.append(' ').append(x.data);
        System.out.print(sb);
    }
    public static <T> void print(Node<T> tree) {
        if(tree==null) return;
        Consumer<Node<T>> p=x->System.out.print(x.data+" ");
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
    static <T> void printStuff(ArrayList<ArrayList<Node<T>>> all,int nodes) {
        ArrayList<Node<T>> trees=all.get(nodes);
        System.out.println(nodes+" nodes.");
        for(int i=0;i<trees.size();++i) {
            System.out.print("tree "+i+": ");
            Node<T> tree=trees.get(i);
            final Consumer<Node<T>> p=x->pd(x);
            preorder(tree,p);
            System.out.println();
        }
        System.out.println("end of nodes "+nodes);
    }
    private static <T> void print(Node<T> node,String prefix,boolean isLeft) {
        // lambda
        if(node!=null) {
            System.out.println(prefix+(isLeft?"|-- ":"\\-- ")+node.data);
            print(node.left,prefix+(isLeft?"|   ":"    "),true);
            print(node.right,prefix+(isLeft?"|   ":"    "),false);
        }
    }
    public static <T> void print(Node<T> node,String prefix) {
        if(node!=null) print(node,prefix,true);
        else System.out.println("0");
    }
    // all wants a generator
    // encode wants an empty list
    // use a new map
    // and check for duplicates not using map?
    // new map is same as old map
    // figure this out later
    // maybe always generate with long and relabel?
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
            if(useMap) if(map.put(nodes,trees)!=null) System.out.println(nodes+" is already in map!");
            return trees;
        }
        public static <T> ArrayList<Node<T>> one(int nodes,Iterator<T> iterator,boolean useMap) {
            Generator<T> generator=new Generator<>(useMap);
            ArrayList<Node<T>> trees=generator.all(nodes,iterator);
            return trees;
        }
        public static ArrayList<ArrayList<Node<Long>>> all(int nodes) {
            ArrayList<ArrayList<Node<Long>>> all=new ArrayList<>();
            Iterator<Long> iterator=new G2.Longs();
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
        System.out.println("collect data: "+data);
        StringBuffer stringBuffer=new StringBuffer();
        toDataString(stringBuffer,tree);
        System.out.println("to data string: "+stringBuffer);
        String expected=encode(tree,null);
        MyConsumer<Long> c2=new MyConsumer<Long>();
        Node.<Long> postorder(tree,c2);
        String actual=encode(c2.copy,null);
        System.out.println("ac: "+actual);
        if(!expected.equals(actual)) System.out.println("copy failure!");
        ArrayList<Long> data2=collectData(c2.copy);
        System.out.println("collect data2: "+data2);
    }
    public static void main(String[] arguments) {
        List<String> x=ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println(x);
        System.out.println("in eclipse: "+inEclipse());
        G2 g2=new G2();
        if(arguments!=null&&arguments.length>0) g2.useMap=true;
        if(inEclipse()) g2.useMap=true;
        g2.useMap=false;
        int nodes=3;
        ArrayList<ArrayList<Node<Long>>> all=G2.Generator.all(nodes);
        System.out.println(nodes+" nodes.");
        if(false) { check(all.get(2).get(0)); return; }
        for(int i=0;i<=nodes;i++) {
            System.out.println("check "+i+" nodes.");
            ArrayList<Node<Long>> trees=all.get(i);
            int n=0;
            for(Node<Long> expected:trees) {
                check(expected);
                G2.print(expected,"");
                //G2.print(expected);
            }
        }
        //if(true) return;
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
        System.out.println("end static init.");
    }
}
