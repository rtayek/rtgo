package tree;
import io.Logging;
import java.util.ArrayList;
import java.util.function.Consumer;
import utilities.Holder;
public class So {
    public static class Node {
        public Node(Integer data) { this.data=data; }
        public Node(Integer data,Node left,Node right) { this.data=data; this.left=left; this.right=right; }
        public void preorder(Consumer<Node> consumer) {
            BinaryTreeSupport.preorder(this,node -> node.left,node -> node.right,consumer);
        }
        public void inorder(Consumer<Node> consumer) {
            BinaryTreeSupport.inorder(this,node -> node.left,node -> node.right,consumer);
        }
        public void postorder(Consumer<Node> consumer) {
            BinaryTreeSupport.postorder(this,node -> node.left,node -> node.right,consumer);
        }
        public static Node copy(Node node) {
            if(node==null) return null;
            Node copy=new Node(node.data,node.left,node.right);
            copy.left=node.left;
            copy.right=node.right;
            return copy;
        }
        Node left,right,parent;
        Integer data;
    }
    public ArrayList<Node> all(int nodes,Holder<Integer> data) { // https://www.careercup.com/question?id=14945787
        ArrayList<Node> trees=new ArrayList<>();
        if(nodes==0) trees.add(null);
        else for(int i=0;i<nodes;i++) {
            for(Node left:all(i,data)) {
                for(Node right:all(nodes-1-i,data)) {
                    if(data!=null) ++data.t;
                    Node node=new Node(data.t,left,right);
                    trees.add(node);
                }
            }
        }
        return trees;
    }
    static public void print(String prefix,Node node,boolean isLeft) {
        if(node!=null) {
            Logging.mainLogger.info(prefix+(isLeft?"|-- ":"\\-- ")+node.data);
            print(prefix+(isLeft?"|   ":"    "),node.left,true);
            print(prefix+(isLeft?"|   ":"    "),node.right,false);
        }
    }
    public static void print(String prefi,Node node) {
        if(node!=null) print(prefi,node,false);
        else Logging.mainLogger.info("0");
    }
    static class MyConsumer implements Consumer<Node> {
        @Override public void accept(Node node) { //
            copy=new Node(node.data);
            // what do we do here?
        }
        Node copy,left,right;
    }
    static void makeTrees() { // fix these names so they are consistent.
        Node tree0=null;
        Node tree1=new Node(1);
        trees1[0]=tree1;
        Node tree21=new Node(1);
        tree21.left=new Node(2);
        trees2[0]=tree21;
        Node tree22=new Node(1);
        tree22.right=new Node(2);
        trees2[1]=tree22;
        Node tree31=new Node(1);
        tree31.left=new Node(2);
        tree31.left.left=new Node(3);
        trees3[0]=tree31;
        Node tree2=new Node(1);
        tree2.right=new Node(2);
        tree2.right.right=new Node(3);
        trees3[1]=tree2;
        Node tree3=new Node(1);
        tree3.left=new Node(2);
        tree3.right=new Node(3);
        trees3[2]=tree3;
        Node tree41=new Node(1);
        tree41.left=new Node(2);
        tree41.left.right=new Node(3);
        trees3[3]=tree41;
        Node tree5=new Node(1);
        tree5.right=new Node(2);
        tree5.right.left=new Node(3);
        trees3[4]=tree5;
    }
    public static void main(String[] arguments) {
        makeTrees();
        MyConsumer c2=new MyConsumer();
        for(int nodes=0;nodes<all.length;++nodes) {
            Logging.mainLogger.info("trees with "+nodes+" nodes.");
            Node[] trees=all[nodes];
            for(int i=0;i<trees.length;++i) {
                Logging.mainLogger.info("tree "+(i+1)+":");
                Logging.mainLogger.info("\toriginal:");
                Node node=trees[i];
                print("\t\t",node);
                Logging.mainLogger.info("\tcopy:");
                Node copy=Node.copy(node);
                print("\t\t",copy);
                //node.postorder(c2);
            }
        }
    }
    static Node[] trees0=new Node[] {null};
    static Node[] trees1=new Node[1];
    static Node[] trees2=new Node[2];
    static Node[] trees3=new Node[5];
    static Node[][] all=new Node[][] {trees0,trees1,trees2,trees3,};
}
