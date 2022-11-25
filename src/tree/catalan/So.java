package tree.catalan;
import java.util.ArrayList;
import java.util.function.Consumer;
import utilities.Holder;
public class So {
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
            System.out.println(prefix+(isLeft?"|-- ":"\\-- ")+node.data);
            print(prefix+(isLeft?"|   ":"    "),node.left,true);
            print(prefix+(isLeft?"|   ":"    "),node.right,false);
        }
    }
    public static void print(String prefi,Node node) {
        if(node!=null) print(prefi,node,false);
        else System.out.println("0");
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
            System.out.println("trees with "+nodes+" nodes.");
            Node[] trees=all[nodes];
            for(int i=0;i<trees.length;++i) {
                System.out.println("tree "+(i+1)+":");
                System.out.println("\toriginal:");
                Node node=trees[i];
                print("\t\t",node);
                System.out.println("\tcopy:");
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
