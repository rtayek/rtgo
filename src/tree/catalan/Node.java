package tree.catalan;
import static utilities.Utilities.implies;
import java.util.*;
import java.util.function.*;
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
    }
    public void addDescendant(Node node) { Node last=lastDescendant(); last.left=node; }
    public int children() { if(left==null) return 0; left.lastSibling(); return siblings+1; }
    public void addChild(Node node) {
        if(left==null) { left=node; return; }
        Node last=left.lastSibling();
        if(last==null) throw new RuntimeException("last is null in addChild");
        last.right=node;
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
    // https://www.baeldung.com/java-print-binary-tree-diagram
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
    public static void printSubtree(Boolean isLeft,int indent,Node node) {
        // https://stackoverflow.com/questions/8964279/coding-a-basic-pretty-printer-for-trees-in-java
        for(int i=0;i<indent;++i) { System.out.print(" "); }
        int elemWidth=4;
        if(node!=null&&(node.left!=null||node.right!=null)) { // maybe just not a leaf?
            System.out.println("("+node.data);
            printSubtree(isLeft,indent+elemWidth,node.left); //this is a recursive call, alternatively use the indent formula above if you don't use recursion
            printSubtree(isLeft,indent+elemWidth,node.right);
            //we have a new line so print the indent again
            for(int i=0;i<indent;++i) { System.out.print(" "); }
            System.out.println(")");
        } else if(node!=null) {
            System.out.println(node.data);
        } else { //empty/non existing node
            //System.out.println("()");
        }
    }
    public static void printx(String prefix,Node n,boolean isLeft) {
        //https://stackoverflow.com/a/55153851/51292
        if(n!=null) {
            printx(prefix+"     ",n.right,false);
            System.out.println(prefix+("|-- ")+n.data);
            printx(prefix+"     ",n.left,true);
        }
    }
    /**
     * Print a tree structure in a pretty ASCII fromat.
     * @param prefix Currnet previx. Use "" in initial call!
     * @param node The current node. Pass the root node of your tree in initial call.
     * @param getChildrenFunc A {@link Function} that returns the children of a given node.
     * @param isTail Is node the last of its sibblings. Use true in initial call. (This is needed for pretty printing.)
     * @param <T> The type of your nodes. Anything that has a toString can be used.
     */
    static void printTreeRec(String prefix,Node node,Function<Node,List<Node>> getChildrenFunc,boolean isTail) {
        String nodeName=""+node.data;
        String nodeConnection=isTail?"└── ":"├── ";
        System.out.println(prefix+nodeConnection+nodeName);
        List<Node> children=getChildrenFunc.apply(node);
        for(int i=0;i<children.size();i++) {
            String newPrefix=prefix+(isTail?"    ":"│   ");
            printTreeRec(newPrefix,children.get(i),getChildrenFunc,i==children.size()-1);
            System.out.println();
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
    public static List<Boolean> bits(long b,int length) {
        List<Boolean> bits=new ArrayList<>();
        for(int i=length;i>=1;b/=2,--i) bits.add(b%2==1?true:false);
        Collections.reverse(bits);
        while(!implies(bits.size()>0,bits.get(0))) bits.remove(0);
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
    //int number=Integer.parseInt(expected,2);
    public static List<Boolean> roundTrip(List<Boolean> list,List<Integer> data) {
        Node node2=Node.decode(list,data);
        String actual=encode(node2);
        // have ths return the binary string.
        return null;
    }
    public static String roundTrip(String expected) {
        // add string writer and return the tree
        long number=Long.parseLong(expected,2);
        List<Boolean> list=Node.bits(number,expected.length());
        List<Integer> data=new ArrayList<>(sequentialData);
        Node node2=Node.decode(list,data);
        String actual=encode(node2);
        return actual;
    }
    static Node roundTrip(Node tree) {
        MNode mNodes=toGeneralTree(tree);
        Node newTree=toBinaryTree(mNodes);
        return newTree.left;
    }
    static ArrayList<Node> allBinaryTrees_(int nodes,Holder<Integer> data) {
        System.out.println("enter");
        ArrayList<Node> trees=new ArrayList<>();
        ArrayList<String> binaryStrings=null;
        boolean done=false;
        if(usingMap2) binaryStrings=new ArrayList<>();
        if(true&&nodes==0) {
            trees.add(null);
            if(!usingMap2); // put trees in map? yes
            else binaryStrings.add(null);
            done=true;
            //return trees; // get rid of this!
            // the above works if we return
            // so we are returning an empty list.
        } else {
            if(!usingMap2) {
                if(map.containsKey(nodes)) { trees=map.get(nodes); done=true; }
            } else {
                if(map2.containsKey(nodes)) { // get and restore trees
                    binaryStrings=map2.get(nodes);
                    // convert to list of list of nodes
                    for(String string:binaryStrings) {
                        Node tree=Node.decode(string,sequentialData);
                        String actual=encode(tree);
                        if(!actual.equals(string)) System.out.println("1 badness!");
                        trees.add(tree);
                        System.out.println("\tgot: "+tree);
                    }
                    done=true;
                }
            }
            if(done) {
                for(Node tree:trees) System.out.println("\tgot: "+tree+" "+encode(tree));
                System.out.println("\tgot "+trees.size()+" trees.");
                if(usingMap2) System.out.println("\tgot "+binaryStrings.size()+" binary strings: "+binaryStrings);
            } else {
                System.out.println("building trees with "+nodes+" nodes.");
                for(int i=0;i<nodes;i++) { // this will fall through if nodes=0!
                    //System.gc();
                    System.out.println("i: "+i);
                    for(Node left:allBinaryTrees_(i,data)) {
                        System.out.println("\tleft  i: "+i);
                        for(Node right:allBinaryTrees_(nodes-1-i,data)) {
                            System.out.println("\tright nodes-i: "+(nodes-i));
                            ++data.t;
                            Node node=new Node(data.t,left,right);
                            System.out.println("created nod: "+node+" "+encode(node));
                            final List<Integer> datas=new ArrayList<>();
                            Consumer<Node> add=x->datas.add(x.data);
                            preOrder(node,add);
                            // not using datas?
                            //System.out.println("data values: "+datas);
                            trees.add(node);
                            String encoded=encode(node);
                            System.out.println(encoded+" "+node);
                            System.out.println(node);
                            if(usingMap2) {
                                // encode and add to list of binary strings
                                encoded=encode(node);
                                System.out.println(encoded+" "+node);
                                Node actual=Node.decode(encoded,sequentialData);
                                if(!node.structureDeepEquals(actual)) {
                                    System.out.println("structures are different!");
                                    System.out.println(node);
                                    System.out.println(actual);
                                }
                                binaryStrings.add(encoded);
                            }
                        }
                    }
                }
            } // at least one node
        }
        if(nodes!=0) { // do this earlier
            ArrayList<String> encoded=new ArrayList<>();
            for(Node tree:trees)  encoded.add(encode(tree));
            System.out.println("putting encoded trees: "+encoded);
            if(!usingMap2) {
                System.out.println("\tputting: "+trees.size()+" trees.");
                map.put(nodes,trees);
            } else {
                System.out.println("\tputting: "+binaryStrings);
                map2.put(nodes,binaryStrings);
            }
        }
        System.out.println("exit");
        return trees;
    }
    static ArrayList<Node> allBinaryTrees(int nodes,Holder<Integer> data) {
        //usingMap2=true;
        return allBinaryTrees_(nodes,data);
    }
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
        String encoded=encode(tree);
        long number=Long.parseLong(encoded,2);
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
        boolean ok=tree.toXString().equals(newTree.toXString());
        if(!ok) {
            lines.add("---");
            lines.add(bothToString(newTree));
            longString=Node.toLongString(newTree);
            //System.out.println("long string: "+longString);
            words=longString.split("\n");
            for(String word:words) lines.add(word);
        }
        //lines.add("|||");
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
    static List<Node> getChildrenOf(Node node) {
        ArrayList<Node> nodes=new ArrayList<>();
        if(node.left!=null) nodes.add(node.left);
        if(node.right!=null) nodes.add(node.right);
        return nodes;
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
        for(Node node:trees) {
            //printSubtree(false,4,node);
            //printx("    ",node,true);
            getChildrenOf(node);
            Function<Node,List<Node>> getChildrenFunc=x->getChildrenOf(x);
            //System.out.println("-----");
            //printTreeRec("    ",node,getChildrenFunc,true);
            //System.out.println("------------------------");
        }
        //if(nodes==3) System.out.println("string3: "+string3);
        chop(reports);
    }
    public static void main(String[] args) {
        // problems:
        // root node has value of an index
        usingMap2=args!=null&&args.length>0?true:false;
        if(false) handmade();
        //System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        if(false) {
            doRun(2);
        } else if(true) for(int nodes=1;nodes<=2;nodes++) {
            System.out.println("run "+nodes+" nodes. <<<<<<<<<<");
            doRun(nodes);
            System.out.println("---");
            System.out.println("keys: "+(!usingMap2?map.keySet():map2.keySet()));
            //doRun(nodes);
            System.out.println("run "+nodes+" nodes. >>>>>>>>>>");
        }
        if(true) return;
        Holder<Integer> data=new Holder<>(0);
        List<Node> list=Node.allBinaryTrees(2,data);
        for(Node tree:list) {
            //Node.ids=0;
            String encoded=encode(tree);
            long number=Long.parseLong(encoded,2);
            System.out.println("\t"+number+" "+bothToString(tree));
            System.out.println("\t"+number+" "+toDataString(tree));
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
    static final int maxNodes=10; //11;
    static List<Integer> sequentialData=new ArrayList<>();
    static {
        for(int i=0;i<100;++i) sequentialData.add(i);
    }
    static SortedMap<Integer,ArrayList<Node>> map=new TreeMap<>();
    static SortedMap<Integer,ArrayList<String>> map2=new TreeMap<>();
    static {
        System.out.println("static init");
        for(int i=0;i<0;++i) {
            Holder<Integer> data=new Holder<>(0);
            ArrayList<Node> trees=allBinaryTrees(i,data);
            map.put(i,trees);
            for(Node tree:trees) {
                String encoded=encode(tree);
                long number=Long.parseLong(encoded,2);
                System.out.println(i+" "+encoded+" "+number);
                if(true) break;
            }
        }
    }
    transient int siblings,descendants; // dangerous!
    static boolean usingMap2=false;
}
