package tree.catalan;
import static tree.catalan.G2.Node.*;
import static utilities.Utilities.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Consumer;
import tree.catalan.RedBean.MNode2;
import utilities.Et;
public class G2 {
    public static class Integers implements Iterator<Integer> {
        @Override public boolean hasNext() { return n<Integer.MAX_VALUE; }
        @Override public Integer next() { return n++; }
        Integer n=0;
    }
    public static class Characters implements Iterator<Character> {
        @Override public boolean hasNext() { return character<Character.MAX_VALUE; }
        @Override public Character next() { return ++character; }
        Character character='a';
    }
    /*
    Given a general tree with ordered but not indexed children,
    encode the first child as the left child of its parent,
    and each other node as a right child of its (former) sibling.

    The reverse is: Given a binary tree with distinguished left
    and right children, read the left child of a node as its
    first child and the right child as its next sibling.                // is this throwing if there is a variation on the first move in the game?
     */
    public static class Node<T> {
        public Node(T data) { this.data=data; }
        public Node(T data,Node<T> left,Node<T> right) { this.data=data; this.left=left; this.right=right; }
        public void preorder(Consumer<Node<T>> consumer) {
            if(consumer!=null) consumer.accept(this);
            if(left!=null) left.preorder(consumer);
            if(right!=null) right.preorder(consumer);
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
            boolean isNotNull=node!=null;
            if(isNotNull) {
                if(data!=null) data.add(node.data);
                sb.append('1');
                encode_(sb,node.left,data);
                encode_(sb,node.right,data);
            } else {
                sb.append('0');
                if(data!=null) data.add(null); // maybe not?
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
            T d=data!=null?data.remove(0):null;
            return null;
        }
        static <T> Node<T> decode(String binaryString,List<T> data) {
            List<Character> characters=Arrays.asList(toObjects(binaryString.toCharArray()));
            return decode_(new ArrayList<>(characters),data);
        }
        @Override public int hashCode() { return Objects.hash(data); }
        @Override public boolean equals(Object obj) {
            if(this==obj) return true;
            if(obj==null) return false;
            if(getClass()!=obj.getClass()) return false;
            Node<T> other=(Node<T>)obj;
            boolean equal=data.equals(other.data);
            if(!equal) if(verbose) System.out.println(data+" "+other.data);
            return equal;
        }
        public boolean deepEquals_(Node<T> other,boolean ckeckEqual) {
            // lambda?
            if(this==other) return true;
            else if(other==null) { System.out.println(data+" othe ris null!"); return false; }
            if(ckeckEqual) if(!equals(other)) { if(verbose) System.out.println(data+" "+other.data); return false; }
            if(left!=null) {
                boolean isEqual=left.deepEquals_(other.left,ckeckEqual);
                if(!isEqual) { if(verbose) System.out.println(left.data+"!="+other.left.data); return false; }
                if(verbose) System.out.println(left.data+"=="+other.left.data);
            } else if(other.left!=null) { if(verbose) System.out.println(data+" othe left is null!"); return false; }
            if(right!=null) {
                boolean isEqual=right.deepEquals_(other.right,ckeckEqual);
                if(!isEqual) { System.out.println(right.data+"!="+other.right.data); return false; }
                if(verbose) System.out.println(right.data+"=="+other.right.data);
            } else if(other.right!=null) {
                if(verbose) System.out.println(data+" othe right is not null!");
                if(verbose) System.out.println("other right "+other.right.data);
                return false;
            }
            return true;
        }
        @Override public String toString() { return "Node [data="+data+"]"; }
        public static <T> Node<T> copy(Node<T> node) {
            if(node==null) return null;
            Node<T> copy=new Node<>(node.data,node.left,node.right);
            copy.left=(node.left);
            copy.right=(node.right);
            return copy;
        }
        public static <T> boolean deepEquals(Node<T> node,Node<T> other) {
            return node!=null?node.deepEquals_(other,true):other==null;
        }
        public static <T> boolean structureDeepEquals(Node<T> node,Node<T> other) {
            return node!=null?node.deepEquals_(other,false):other==null;
        }
        static <T> int check(Node<T> expected) {
            int n=0;
            //System.out.println("check: "+expected);
            Node<T> actual=roundTrip(expected);
            if(!structureDeepEquals(expected,actual)) { ++n; System.out.println("0 "+expected+"!="+actual); }
            //if(true) return n;
            ArrayList<T> data=new ArrayList<>();
            String expectedEncoded=encode(expected,data);
            if(expectedEncoded.length()!=data.size()) System.out.println("encoded length!=data size!");
            String actualEncoded=roundTripLong(expectedEncoded);
            if(!expectedEncoded.equals(actualEncoded)) {
                ++n;
                System.out.println("1 "+expectedEncoded+"!="+actualEncoded);
            }
            String actualEncoded2=roundTrip(expectedEncoded,data);
            if(data.size()>0) System.out.println("data size is >0!");
            if(!expectedEncoded.equals(actualEncoded2)) {
                ++n;
                System.out.println("2 "+expectedEncoded+"!="+actualEncoded);
            }
            return n;
        }
        public static <T> MNode2<T> from_(Node<T> node,MNode2<T> grandParent) {
            if(node==null) return null;
            //System.out.println("processing: "+node.data);
            boolean ok=processed.add((Character)node.data);
            if(!ok) {
                System.out.println(node.data+" already processed!");
                //return null;
            }
            MNode2<T> parent=new MNode2<T>(node.data,grandParent);
            if(grandParent!=null) grandParent.children.add(parent);
            //else throw new RuntimeException("gradparent is null!");
            if(node.left!=null) {
                for(Node<T> n=node.left;n!=null;n=n.right) {
                    if(false&&n.data.equals('d')) System.out.println("d1, parent is: "+parent.data);
                    MNode2<T> newMNode2=from_(n,parent);
                    //parent.children.add(newMNode2);
                }
            }
            // this seems to work, but it's different from my MNode's!
            if(node.right!=null) {
                //System.out.println("rigt!=null");
                MNode2<T> newMNode2=from_(node.right,parent);
                if(node.right.data.equals('d')) System.out.println("d2, parent is: "+parent.data);
            }
            return parent;
        }
        public static <T> MNode2<T> from(Node<T> node) {
            processed.clear();
            MNode2<T> extra=new MNode2<T>(null,null);
            //if(node.right!=null) throw new RuntimeException("node.right!=null");
            MNode2<T> mNode2=from_(node,extra);
            return extra;
        }
        Node<T> left,right,parent;
        public T data;
        String encoded;
        final int id=++ids;
        static int ids;
        static LinkedHashSet<Character> processed=new LinkedHashSet<>();
    }
    public static <T> Node<T> roundTrip(Node<T> expected) {
        // add string writer and return the tree
        ArrayList<T> data=new ArrayList<>();
        String actualEncoded=encode(expected,data);
        Node<T> actual=decode(actualEncoded,data);
        if(data.size()>0) System.out.println("leftoverdata: "+data);
        return actual;
    }
    public static <T> String roundTrip(String expected,ArrayList<T> data) {
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
        ArrayList<Integer> data=new ArrayList<>(sequentialData);
        // maybe add data as parameter?
        Node<Integer> node2=decode(list,data);
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
    public static <T> void print(String prefix,Node<T> node,boolean isLeft) {
        if(node!=null) {
            System.out.println(prefix+(isLeft?"|-- ":"\\-- ")+node.data);
            print(prefix+(isLeft?"|   ":"    "),node.left,true);
            print(prefix+(isLeft?"|   ":"    "),node.right,false);
        }
    }
    public static <T> void print(String prefi,Node<T> node) {
        if(node!=null) print(prefi,node,false);
        else System.out.println("0");
    }
    // all wants a generator
    // encode wants an empty list
    // use a new map
    // and checkfor duplicates not using map?
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
        public static <T> ArrayList<Node<T>> all(int nodes,Iterator<T> iterator,boolean useMap) {
            Generator<T> generator=new Generator<>(useMap);
            ArrayList<Node<T>> trees=generator.all(nodes,iterator);
            return trees;
        }
        public static ArrayList<ArrayList<Node<Integer>>> all(int nodes) {
            ArrayList<ArrayList<Node<Integer>>> all=new ArrayList<>();
            Iterator<Integer> iterator=new G2.Integers();
            for(int i=0;i<=nodes;++i) {
                ArrayList<Node<Integer>> trees=Generator.all(i,iterator,false);
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
    static void foo(Node<Integer> tree) {
        ArrayList<Node<Integer>> trees;
        int n;
        print("",tree);
        print(tree);
        ArrayList<Integer> data=collectData(tree);
        System.out.println("collect data: "+data);
        StringBuffer stringBuffer=new StringBuffer();
        toDataString(stringBuffer,tree);
        System.out.println("to data string: "+stringBuffer);
        String expected=encode(tree,null);
        MyConsumer<Integer> c2=new MyConsumer<Integer>();
        Node.<Integer> postorder(tree,c2);
        String actual=encode(c2.copy,null);
        System.out.println("ac: "+actual);
        if(!expected.equals(actual)) System.out.println("copy failure!");
        ArrayList<Integer> data2=collectData(c2.copy);
        System.out.println("collect data2: "+data2);
    }
    public static void main(String[] arguments) {
        List<String> x=ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println(x);
        System.out.println("in eclipse: "+inEclipse());
        G2 g2=new G2();
        if(arguments!=null&&arguments.length>0) g2.useMap=true;
        if(inEclipse()) g2.useMap=true;
        //g2.useMap=false;
        int nodes=10;
        ArrayList<ArrayList<Node<Integer>>> all=G2.Generator.all(nodes);
        System.out.println(nodes+" nodes.");
        if(false) { check(all.get(2).get(0)); return; }
        for(int i=0;i<=nodes;i++) {
            System.out.println("check "+i+" nodes.");
            ArrayList<Node<Integer>> trees=all.get(i);
            int n=0;
            for(Node<Integer> expected:trees) { check(expected); }
        }
        //if(true) return;
        ArrayList<Node<Integer>> trees=all.get(nodes);
        int n=trees.size();
        Node<Integer> tree=trees.get(n/2);
        foo(tree);
    }
    boolean useMap;
    Et et=new Et();
    // put all here as an instance variable?
    SortedMap<Integer,ArrayList<Node<Integer>>> map=new TreeMap<>();
    static List<Integer> sequentialData=new ArrayList<>();
    static final int maxNodes=100;
    static Boolean verbose=false;
    static {
        ArrayList<Integer> data=new ArrayList<>();
        for(int i=0;i<maxNodes;++i) data.add(i); // start at 1?
        sequentialData=Collections.unmodifiableList(data);
        System.out.println("end static init.");
    }
}
