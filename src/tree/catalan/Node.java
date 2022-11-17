package tree.catalan;
import static utilities.Utilities.implies;
import java.util.*;
import java.util.function.Consumer;
import sgf.MNode;
import utilities.Holder;
//https://en.wikipedia.org/wiki/Binary_tree#Encodings
// http://www.durangobill.com/BinTrees.html
// https://en.wikipedia.org/wiki/Binary_tree#Combinatorics
class Node {
    // seems to be a clone of sgf node.
    // not any more, added a lot of stuff
    // only used by the catalan class.
    // this uses the mnode class!
    public Node() {}
    public Node(int data) { this.data=data; }
    public Node(Node left,Node right) { this.left=left; this.right=right; }
    public Node(int data,Node left,Node right) { this.data=data; this.left=left; this.right=right; }
    public Node lastSibling() {
        Node node=null,last=this;
        siblings=0;
        for(node=right;node!=null;node=node.right) {
            // nodes.add(node);
            last=node;
            ++siblings;
        }
        return last;
    }
    public int siblings() { lastSibling(); return siblings; }
    public Node lastDescendant() {
        Node node=null,last=this;
        descendants=0;
        for(node=left;node!=null;node=node.left) {
            last=node;
            ++descendants;
        }
        return last;
    }
    public Node lastChild() { return left==null?null:left.lastSibling(); }
    public void addSibling(Node node) {
        // if(right==null) { right=node;
        // System.err.println("added node "+node.id+" as first sibling of node "+this.id);
        // return; }
        Node last=lastSibling();
        last.right=node;
        // System.err.println("added node "+node.id+" as sibling of node "+this.id);
    }
    public void addDescendant(Node node) {
        Node last=lastDescendant();
        last.left=node;
        // System.err.println("added node "+node.id+" as desendent of node "+last.id);
    }
    public int children() { if(left==null) return 0; left.lastSibling(); return siblings+1; }
    public void addChild(Node node) {
        // if(left==null)
        // System.err.println("added node "+node.id+" as first child of node "+this.id);
        if(left==null) { left=node; return; }
        Node last=left.lastSibling();
        if(last==null) throw new RuntimeException("last is null in addChild");
        last.right=node;
        // System.err.println("added node "+node.id+" as child of node "+this.id);
    }
    public static void preOrder(Node root,Consumer<Node> consumer) {
        if(root==null) return;
        consumer.accept(root);
        preOrder(root.left,consumer);
        preOrder(root.right,consumer);
    }
    public static void preOrder(Node root) { if(root==null) return; preOrder(root.left); preOrder(root.right); }
    public static void preOrderTraverse(Boolean isLeft,StringBuilder sb,String padding,String pointer,Node node) {
        if(node!=null) {
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.data);
            sb.append(' ');
            if(isLeft!=null) sb.append(isLeft?'L':'R');
            sb.append(' ');
            //sb.append(node.left!=null?'T':'F');
            //sb.append(' ');
            //sb.append(node.right!=null?'T':'F');
            sb.append('\n');
            StringBuilder paddingBuilder=new StringBuilder(padding);
            String bothPad="│  ";
            //bothPad="";
            paddingBuilder.append(bothPad);
            //paddingBuilder.append("   ");
            String paddingForBoth=paddingBuilder.toString();
            String rPad="└──";
            String lPad=(node.right!=null)?"├──":rPad;
            // maybe get rid of the both padding
            // and use spaces for a null node
            //if(node.left!=null) lPad="";
            preOrderTraverse(true,sb,paddingForBoth,lPad,node.left);
            preOrderTraverse(false,sb,paddingForBoth,rPad,node.right);
        }
    }
    public static void preOrderTraverse2(Boolean isLeft,StringBuilder sb,String padding,String pointer,Node node) {
        if(node!=null) {
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.data);
            sb.append(' ');
            if(isLeft!=null) sb.append(isLeft?'L':'R');
            else if(node.left!=null&&node.right!=null) {
                sb.append(' ');
                sb.append(node.left.data);
                sb.append(' ');
                sb.append(node.right.data);
            }
            //sb.append(node.left!=null?'T':'F');
            //sb.append(' ');
            //sb.append(node.right!=null?'T':'F');
            sb.append('\n');
            StringBuilder paddingBuilder=new StringBuilder(padding);
            String lPad="",rPad="    ";
            String bothPad="";
            if(node.left!=null) bothPad="";
            paddingBuilder.append(bothPad);
            String paddingForBoth=paddingBuilder.toString();
            if(node.left!=null) {
                //String bothPad="│  ";
                //lPad="";
                rPad="";
            }
            //String rPad="└──";
            //String lPad=(node.right!=null)?"├──":rPad;
            // maybe get rid of the both padding
            // and use spaces for a null node
            //if(node.left!=null) lPad="";
            preOrderTraverse2(true,sb,paddingForBoth,lPad,node.left);
            preOrderTraverse2(false,sb,paddingForBoth,rPad,node.right);
        }
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
    public boolean structureDeepEquals(Node other) {
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
    void fix_(Node other) {
        if(this==other);
        else if(other==null) throw new RuntimeException("can not fix!");
        else if(data!=other.data) other.data=data;
        if(left!=null) left.fix_(other.left);
        else if(other.left!=null) throw new RuntimeException("can not fix!");
        if(right!=null) right.fix(other.right);
        else if(other.right!=null) throw new RuntimeException("can not fix!");
    }
    void fix(Node node2) {
        boolean structureDeepEquals=structureDeepEquals(node2);
        if(structureDeepEquals) fix_(node2);
        else throw new RuntimeException("can not fix!");
    }
    public boolean deepEquals(Node other) {
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
    @Override public int hashCode() { return Objects.hash(data); }
    @Override public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null) return false;
        if(getClass()!=obj.getClass()) return false;
        Node other=(Node)obj;
        return data==other.data||data.equals(obj);
    }
    public boolean isStrange() { return isStrange(this); }
    public static boolean isStrange(Node binaryNode) {
        if(binaryNode==null) return false;
        if(binaryNode.left!=null) {
            if(isStrange(binaryNode.left)) return true;
            if(binaryNode.right!=null) if(isStrange(binaryNode.right)) return true;
        } else if(binaryNode.right!=null) return true;
        return false;
    }
    public static Node toBinaryTree(MNode parent) {
        if(parent==null) return null;
        ArrayList<MNode> children=parent.children;
        Node left=null,tail=null;
        for(int i=0;i<children.size();++i) {
            if(i==0) {
                left=tail=toBinaryTree(children.get(i));
                if(left.right!=null) throw new RuntimeException();
            } else {
                Node newRight=toBinaryTree(children.get(i));
                tail.right=newRight;
                tail=newRight;
            }
        }
        Node binaryNode=new Node(parent.data,left,null);
        return binaryNode;
    }
    private static MNode toGeneralTree(Node parent,MNode grandParent) {
        // these belong in some kind of MNode;
        // put in interface as static methods?
        if(parent==null) return null;
        MNode mNode=new MNode(null),child=null;
        mNode.data=parent.data;
        if(grandParent!=null) grandParent.children.add(mNode);
        else throw new RuntimeException("gradparent is null!");
        if(parent.left!=null) { child=toGeneralTree(parent.left,mNode); }
        if(parent.right!=null) {
            child=toGeneralTree(parent.right,grandParent);
            //System.out.println("strange!");
            //if(!isStrange(node)) System.out.println("isStrange() disagrees!");
        }
        return mNode;
    }
    public static MNode toGeneralTree(Node binaryNode) {
        if(binaryNode==null) return null;
        MNode mRoot=new MNode(null);
        mRoot.data=binaryNode.data;
        @SuppressWarnings("unused") MNode mNode=toGeneralTree(binaryNode,mRoot);
        return mRoot;
    }
    static void toXString(StringBuffer sb,Node node) { // encode
        if(node==null) sb.append('x');
        else {
            sb.append('(');
            toXString(sb,node.left);
            toXString(sb,node.right);
            sb.append(')');
        }
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
    static void toDataString(StringBuffer sb,Node node) { // encode
        if(node==null); //sb.append('x');
        else {
            sb.append(node.data);
            sb.append('(');
            toDataString(sb,node.left);
            toDataString(sb,node.right);
            sb.append(')');
        }
    }
    static String toXString(Node btree) {
        StringBuffer sb=new StringBuffer();
        toXString(sb,btree);
        return sb.toString();
    }
    static String encode(Node tree) { // to binary string
        // https://oeis.org/search?q=4%2C20%2C24%2C84%2C88%2C100%2C104%2C112&language=english&go=Search
        StringBuffer sb=new StringBuffer();
        encode(sb,tree);
        return sb.toString();
    }
    static String toDataString(Node tree) {
        StringBuffer sb=new StringBuffer();
        toDataString(sb,tree);
        return sb.toString();
    }
    public static String toLongString(Node tree) {
        StringBuilder sb=new StringBuilder();
        //preOrderTraverse(null,sb,"","",tree);
        preOrderTraverse2(null,sb,"","",tree);
        return sb.toString();
    }
    @Override public String toString() { return "Node [data="+data+", id="+id+"]"; }
    String toXString() { return toXString(this); }
    String encode() { return encode(this); }
    // add string writer and return the tree
    public static List<Boolean> encode(int b,int length) {
        List<Boolean> bits=new ArrayList<>();
        for(int i=length;i>=1;b/=2,--i) bits.add(b%2==1?true:false);
        Collections.reverse(bits);
        //while(bits.size()>0&&!bits.get(0)) bits.remove(0); // what is this?
        while(!implies(bits.size()>0,bits.get(0))) bits.remove(0); // what is this?
        if(bits.size()==0) System.out.println("no bits!");
        return bits;
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
    public static String roundTrip(String expected) {
        // add string writer and return the tree
        int n=Integer.parseInt(expected,2);
        List<Boolean> list=Node.encode(n,expected.length());
        List<Integer> data=new ArrayList<>(sequentialData);
        Node node2=Node.decode(list,data);
        String actual=node2.encode();
        return actual;
    }
    static Node roundTrip(Node tree) {
        MNode mNodes=toGeneralTree(tree);
        Node newTree=toBinaryTree(mNodes);
        return newTree.left;
    }
    static List<Node> allBinaryTrees_(int nodes,Holder<Integer> data) {
        // memoize this!
        // might have to get this and renumber?
        List<Node> list=new ArrayList<>();
        if(nodes==0) list.add(null);
        else for(int i=0;i<nodes;i++) {
            for(Node left:allBinaryTrees_(i,data)) {
                for(Node right:allBinaryTrees_(nodes-1-i,data)) {
                    ++data.t;
                    Node node=new Node(data.t,left,right);
                    final List<Integer> datas=new ArrayList<>();
                    Consumer<Node> add=x->datas.add(x.data);
                    preOrder(node,add);
                    //System.out.println("data values: "+datas);
                    list.add(node);
                }
            }
        }
        return list;
    }
    static List<Node> allBinaryTrees(int nodes,Holder<Integer> data) { return allBinaryTrees_(nodes,data); }
    static void makeTrees() { // fix these names so they are consistent.
        Node tree0=null;
        Node tree1=new Node(1);
        binaryTrees1[0]=tree1;
        Node tree21=new Node(1);
        tree21.left=new Node(2);
        binaryTrees2[0]=tree21;
        Node tree22=new Node(1);
        tree22.right=new Node(2);
        binaryTrees2[1]=tree22;
        Node tree31=new Node(1);
        tree31.left=new Node(2);
        tree31.left.left=new Node(3);
        binaryTrees3[0]=tree31;
        Node tree2=new Node(1);
        tree2.right=new Node(2);
        tree2.right.right=new Node(3);
        binaryTrees3[1]=tree2;
        Node tree3=new Node(1);
        tree3.left=new Node(2);
        tree3.right=new Node(3);
        binaryTrees3[2]=tree3;
        Node tree41=new Node(1);
        tree41.left=new Node(2);
        tree41.left.right=new Node(3);
        binaryTrees3[3]=tree41;
        Node tree5=new Node(1);
        tree5.right=new Node(2);
        tree5.right.left=new Node(3);
        binaryTrees3[4]=tree5;
    }
    // https://examradar.com/converting-m-ary-tree-general-tree-binary-tree/
    static void print(Node node) {
        System.out.println(" node: "+node);
        System.out.println(" left: "+node.left);
        System.out.println("right: "+node.right);
        if(node.left!=null) {
            System.out.println("left left: "+node.left.left);
            System.out.println("left right: "+node.left.right);
        }
        if(node.right!=null) {
            System.out.println("right left: "+node.right.left);
            System.out.println("right right: "+node.right.right);
        }
        System.out.println("end of node: "+node);
    }
    static void print(MNode mNode) {
        System.out.println("mnode: "+mNode);
        System.out.println("children: "+mNode.children);
        if(mNode.children!=null&&mNode.children.size()>0)
            System.out.println("grand kids: "+mNode.children.get(0).children);
    }
    static String bothToString(Node node) { return toXString(node)+" "+encode(node); }
    static void run(Node tree) {
        String string=encode(tree);
        int encoded=Integer.parseInt(string,2);
        System.out.println(bothToString(tree));
        System.out.println(Node.toLongString(tree));
        Node newTree=roundTrip(tree);
        System.out.println(bothToString(newTree));
        System.out.println(Node.toLongString(newTree));
        boolean ok=tree.toXString().equals(newTree.toXString());
        System.out.println(ok);
        if(!ok) throw new RuntimeException();
    }
    static List<String> report(Node tree) {
        List<String> lines=new ArrayList<>();
        lines.add(bothToString(tree));
        String longString=Node.toLongString(tree);
        String[] words=longString.split("\n");
        for(String word:words) lines.add(word);
        //System.out.println("short lines: "+lines);
        Node newTree=roundTrip(tree); // maybe losing ids here?
        lines.add(bothToString(newTree));
        longString=Node.toLongString(newTree);
        //System.out.println("long string: "+longString);
        words=longString.split("\n");
        //for(String word:words) lines.add(word);
        boolean ok=tree.toXString().equals(newTree.toXString());
        lines.add(ok?"good":"fail!");
        lines.add("|||");
        return lines;
    }
    private static List<List<String>> reports(List<Node> list) {
        List<List<String>> reports=new ArrayList<>();
        for(Node root:list) reports.add(report(root));
        return reports;
    }
    static void chop(List<List<String>> reports) {
        int width=0,depth=0;
        for(List<String> lines:reports) {
            int w=0;
            for(String line:lines) w=Math.max(w,line.length());
            width=Math.max(width,w);
            depth=Math.max(depth,lines.size());
        }
        System.out.println("depth: "+depth);
        String[] wideLines=new String[depth];
        for(int i=0;i<depth;++i) wideLines[i]="";
        for(List<String> report:reports) {
            //System.out.println("report: "+report);
            while(report.size()<depth) report.add("");
            int i=0;
            for(String line:report) {
                String padded=line;
                while(padded.length()<width) padded+=" ";
                wideLines[i]+=padded+"    ";
                ++i;
            }
        }
        System.out.println("wide lines:");
        for(String wideLine:wideLines) System.out.println(wideLine);
    }
    private static void handmade() {
        System.out.println("handmade");
        makeTrees();
        List<List<String>> handmade1=reports(Arrays.asList(binaryTrees1));
        System.out.println("report sfro 1 node: "+handmade1);
        System.out.println("chope 1");
        chop(handmade1);
        List<List<String>> handmade2=reports(Arrays.asList(binaryTrees2));
        System.out.println("reports for 2 nodes: "+handmade2);
        System.out.println("chope 2");
        chop(handmade2);
        List<List<String>> handmade3=reports(Arrays.asList(binaryTrees3));
        System.out.println("reports for 3 nodes: "+handmade3);
        System.out.println("string3: "+string3);
        System.out.println("chope 3");
        chop(handmade3);
        // ()()(),     ()(()),     (())(),     (()()),     ((()))
    }
    static void doRun(int nodes) {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=Node.allBinaryTrees(nodes,data);
        System.out.println("has: "+trees.size()+" trees.");
        List<List<String>> reports=reports(trees);
        if(false) {
            System.out.println("reports for "+nodes+" nodes: ");
            for(List<String> report:reports) System.out.println("report0: "+report);
        }
        if(nodes==3) System.out.println("string3: "+string3);
        chop(reports);
        System.out.println("after run.");
    }
    public static void main(String[] args) {
        // problems:
        // root node has value of an index
        if(false) handmade();
        //System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        if(false) {
            doRun(2);
        } else if(true) for(int nodes=1;nodes<=maxNodes;nodes++) {
            System.out.println("run "+nodes+" nodes. <<<<<<<<<<");
            doRun(nodes);
            System.out.println("run "+nodes+" nodes. >>>>>>>>>>");
        }
        if(true) return;
        Holder<Integer> data=new Holder<>(0);
        List<Node> list=Node.allBinaryTrees(2,data);
        for(Node tree:list) {
            //Node.ids=0;
            String string=encode(tree);
            int encoded=Integer.parseInt(string,2);
            System.out.println("\t"+encoded+" "+bothToString(tree));
            System.out.println("\t"+encoded+" "+toDataString(tree));
            //preOrder(tree); // was o, but too much data
            // why don't the data values do something reasonable?
            //break;
            run(tree);
        }
    }
    Node left,right,parent;
    public Integer data;
    public final int id=ids++;
    static String string3=" ((X*X)*X)*X, (X*(X*X))*X, (X*X)*(X*X), X*((X*X)*X), X*(X*(X*X))";
    static Node[] binaryTrees1=new Node[1]; // use catalan numbers!
    static Node[] binaryTrees2=new Node[2];
    static Node[] binaryTrees3=new Node[5];
    static int ids;
    static final int maxNodes=4; //11;
    static List<Integer> sequentialData=new ArrayList<>();
    static {
        for(int i=0;i<100;++i) sequentialData.add(i);
    }
    transient int siblings,descendants; // dangerous!
}
