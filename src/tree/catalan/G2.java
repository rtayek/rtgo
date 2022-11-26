package tree.catalan;
import static tree.catalan.G2.Node.*;
import static utilities.Utilities.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Consumer;
import utilities.*;
public class G2 {
    public static class Node {
        public Node(Integer data) { this.data=data; }
        public Node(Integer data,Node left,Node right) { this.data=data; this.left=left; this.right=right; }
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
        public static void preorder(Node node,Consumer<Node> consumer) {
            if(node==null) return;
            node.preorder(consumer);
        }
        public static void inorder(Node node,Consumer<Node> consumer) { if(node==null) return; node.inorder(consumer); }
        public static void postorder(Node node,Consumer<Node> consumer) {
            if(node==null) return;
            node.postorder(consumer);
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
                if(data!=null) data.add(null); // maybe not?
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
            while(!implies(bits.size()>0,bits.get(0))) { bits.remove(0); }
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
            if(bits.size()==0) return null;
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
        /*
        function encoding(node n, bitstring s, array data){
            if(n == NULL){
                append 0 to s;
            }
            else{
                append 1 to s;
                append n.data to data;
                encoding(n.left, s, data);
                encoding(n.right, s, data);
            }
        }
        function decoding(bitstring s, array data){
            append first bit of s to x and remove it
            if(x==1){
                create new node n
                remove first element of data and put in n.data
                n.left = decoding(s, data)
                n.right = decoding(s,data)
                return n
            else{
                return null
            }
            }
        }
        node *decoding(list<bool> &s, list<int> &data){
        if(s.size()==0)
        return NULL;
        else{
        bool b = s.front();
        s.pop_front();
        if(b==1){
            int val = data.front();
            data.pop_front();
            node *root=newnode(val);
            root->left = decoding(s,data);
            root->right = decoding(s,data);
            return root;
        }
        return NULL;
        }
        }

         */
        static Node decode_(List<Character> binaryString,List<Integer> data) {
            // lambda?
            if(binaryString.size()==0) return null;
            //System.out.println("decodingL "+binaryString);
            boolean b=binaryString.remove(0)=='1';
            if(b) {
                Integer d=data!=null?data.remove(0):null;
                Node node=new Node(d);
                node.left=decode_(binaryString,data);
                node.right=decode_(binaryString,data);
                node.encoded=encode(node,null);
                return node;
            }
            Integer d=data!=null?data.remove(0):null;
            return null;
        }
        static Node decode(String binaryString,List<Integer> data) {
            List<Character> characters=Arrays.asList(toObjects(binaryString.toCharArray()));
            return decode_(new ArrayList<>(characters),data);
        }
        @Override public int hashCode() { return Objects.hash(data); }
        @Override public boolean equals(Object obj) {
            if(this==obj) return true;
            if(obj==null) return false;
            if(getClass()!=obj.getClass()) return false;
            Node other=(Node)obj;
            boolean equal=data.equals(other.data);
            if(!equal) System.out.println(data+" "+other.data);
            return equal;
        }
        public boolean deepEquals_(Node other,boolean ckeckEqual) {
            // lambda?
            if(this==other) return true;
            else if(other==null) return false;
            if(ckeckEqual) if(!equals(other)) return false;
            if(left!=null) {
                boolean isEqual=left.deepEquals_(other.left,ckeckEqual);
                if(!isEqual) return false;
            } else if(other.left!=null) return false;
            if(right!=null) {
                boolean isEqual=right.deepEquals_(other.right,ckeckEqual);
                if(!isEqual) return false;
            } else if(other.right!=null) return false;
            return true;
        }
        public static Node copy(Node node) {
            if(node==null) return null;
            Node copy=new Node(node.data,node.left,node.right);
            copy.left=(node.left);
            copy.right=(node.right);
            return copy;
        }
        public static boolean deepEquals(Node node,Node other) {
            return node!=null?node.deepEquals_(other,true):other==null;
        }
        public static boolean structureDeepEquals(Node node,Node other) {
            return node!=null?node.deepEquals_(other,false):other==null;
        }
        static int check(Node expected) {
            int n=0;
            //System.out.println("check: "+expected);
            Node actual=roundTrip(expected);
            if(!structureDeepEquals(expected,actual)) { ++n; System.out.println("0 "+expected+"!="+actual); }
            //if(true) return n;
            ArrayList<Integer> data=new ArrayList<>();
            String expectedEncoded=encode(expected,data);
            if(expectedEncoded.length()!=data.size()) System.out.println("encoded length!=data size!");
            String actualEncoded=roundTripLong(expectedEncoded);
            if(!expectedEncoded.equals(actualEncoded)) { ++n; System.out.println("1 "+expectedEncoded+"!="+actualEncoded); }
            String actualEncoded2=roundTrip(expectedEncoded,data);
            if(data.size()>0) System.out.println("data size is >0!");
            if(!expectedEncoded.equals(actualEncoded2)) {
                ++n;
                System.out.println("2 "+expectedEncoded+"!="+actualEncoded);
            }
            return n;
        }

        Node left,right,parent;
        public final Integer data;
        String encoded;
        final int id=++ids;
        static int ids;
    }
    public static Node roundTrip(Node expected) {
        // add string writer and return the tree
        ArrayList<Integer> data=new ArrayList<>();
        String actualEncoded=encode(expected,data);
        Node actual=decode(actualEncoded,data);
        if(data.size()>0) System.out.println("leftoverdata: "+data);
        return actual;
    }
    public static String roundTrip(String expected,ArrayList<Integer> data) {
        ArrayList<Integer> data0=new ArrayList<>(data);
        Node decoded=decode(expected,data);
        ArrayList<Integer> data2=new ArrayList<>();
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
        Node node2=decode(list,data);
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
    static class MyConsumer implements Consumer<Node> {
        @Override public void accept(Node node) { //
            if(node!=null) { Node newNode=new Node(node.data); copy=newNode; }
        }
        Node copy,left,right;
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
        preorder(node,add);
        return datas;
    }
    static void p(Node x) { // instance?
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
            preorder(tree,p);
            System.out.println();
        }
        System.out.println("end of nodes "+nodes);
    }
    static public void print(String prefix,Node node,boolean isLeft) {
        if(node!=null) {
            System.out.println(prefix+(isLeft?"|-- ":"\\-- ")+node.data);
            print(prefix+(isLeft?"|   ":"    "),node.left,true);
            print(prefix+(isLeft?"|   ":"    "),node.right,false);
        }
    }
    public static void print(String prefi,Node node) {
        if(node!=null) print(prefi,node,false);
        else System.out.println("0");
    }
    public ArrayList<Node> all(int nodes,Holder<Integer> data) { // https://www.careercup.com/question?id=14945787
        if(useMap) if(map.containsKey(nodes)) return map.get(nodes);
        ArrayList<Node> trees=new ArrayList<>();
        if(nodes==0) trees.add(null);
        else for(int i=0;i<nodes;i++) {
            for(Node left:all(i,data)) {
                for(Node right:all(nodes-1-i,data)) {
                    if(data!=null) ++data.t;
                    Node node=new Node(data.t,left,right);
                    node.encoded=encode(node,null); // ?
                    trees.add(node);
                }
            }
        }
        if(useMap) if(map.put(nodes,trees)!=null) System.out.println(nodes+" is already in map!");
        return trees;
    }
    ArrayList<ArrayList<Node>> generate(int nodes) {
        ArrayList<ArrayList<Node>> all=new ArrayList<>();
        // lost of duplicate work here
        Holder<Integer> data=new Holder<>(0);
        for(int i=0;i<=nodes;i++) {
            et.reset();
            ArrayList<Node> trees=all(i,data);
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
    static void foo(Node tree) {
        ArrayList<Node> trees;
        int n;
        print("",tree);
        print(tree);
        ArrayList<Integer> data=collectData(tree);
        System.out.println("collect data: "+data);
        StringBuffer stringBuffer=new StringBuffer();
        toDataString(stringBuffer,tree);
        System.out.println("to data string: "+stringBuffer);
        String expected=encode(tree,null);
        MyConsumer c2=new MyConsumer();
        postorder(tree,c2);
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
        int nodes=9;
        ArrayList<ArrayList<Node>> all=g2.generate(nodes);
        System.out.println(nodes+" nodes.");
        if(false) { check(all.get(2).get(0)); return; }
        for(int i=0;i<=nodes;i++) {
            System.out.println("check "+i+" nodes.");
            ArrayList<Node> trees=all.get(i);
            int n=0;
            for(Node expected:trees) {
                check(expected);
            }
        }
        //if(true) return;
        ArrayList<Node> trees=all.get(nodes);
        int n=trees.size();
        Node tree=trees.get(n/2);
        foo(tree);
    }
    boolean useMap;
    Et et=new Et();
    // put all here as an instance variable?
    SortedMap<Integer,ArrayList<Node>> map=new TreeMap<>();
    static List<Integer> sequentialData=new ArrayList<>();
    static final int maxNodes=100;
    static {
        ArrayList<Integer> data=new ArrayList<>();
        for(int i=0;i<maxNodes;++i) data.add(i); // start at 1?
        sequentialData=Collections.unmodifiableList(data);
    }
}
