package tree.catalan;
import static tree.catalan.G2.Node.*;
import static tree.catalan.RedBean.MNode2.print;
import java.util.*;
import tree.catalan.G2.Node;
public class RedBean {
    public static class MNode2<T> {
        @Override public String toString() { return "MNode2 [data="+data+"]"; }
        public MNode2(T data,MNode2<T> parent) {
            // maybe just use t as first argument?
            this.parent=parent;
            this.data=data;
        }
        public static <T> Node<T> oldFrom(MNode2<T> mNode2) {
            // this is broken.
            //System.out.println("processing: "+mNode2.data);
            boolean ok=processed.add((Character)mNode2.data);
            if(!ok) System.out.println(mNode2.data+" already processed!");
            Node<T> left=null,tail=null;
            for(int i=0;i<mNode2.children.size();++i) {
                if(i==0) {
                    left=tail=oldFrom(mNode2.children.get(i));
                    //System.out.println("added "+left.data);
                    // is this throwing if there is a variation on the first move in the game?
                    if(left.right!=null) {
                        //ystem.out.println("wierdness at: "+left.data);
                        // maybe not so weird after a;l?
                        //throw new RuntimeException("wierdness!");
                    }
                } else {
                    Node<T> newRight=oldFrom(mNode2.children.get(i));
                    //System.out.println("added "+newRight.data);
                    tail.right=newRight;
                    tail=newRight;
                }
            }
            Node<T> binaryNode=new Node<>(mNode2.data,left,null); // first child
            return binaryNode;
        }
        public static <T> void print(MNode2<T> tree,String indent,boolean last) {
            if(tree==null) return;
            System.out.println(indent+"+- "+tree.data);
            indent+=last?"   ":"|  ";
            for(int i=0;i<tree.children.size();i++) { print(tree.children.get(i),indent,i==tree.children.size()-1); }
        }
        MNode2<T> parent;
        ArrayList<MNode2<T>> children=new ArrayList<>();
        // add a set temporarily to see if we are adding stuff in twice?
        public T data;
        final int id=++ids;
        static int ids;
        static LinkedHashSet<Character> processed=new LinkedHashSet<>();
    }
    static Node<Character> sample() {
        Node<Character> e=new Node<>('e');
        Node<Character> d=new Node<>('d',e,null);
        Node<Character> c=new Node<>('c');
        Node<Character> b=new Node<>('b',c,d);
        Node<Character> i=new Node<>('i');
        Node<Character> h=new Node<>('h',i,null);
        Node<Character> g=new Node<>('g',h,null);
        Node<Character> a=new Node<>('a',b,null);
        Node<Character> j=new Node<>('j');
        Node<Character> f=new Node<>('f',g,j);
        Node<Character> root=new Node<>('r',a,f);
        return root;
    }
    static MNode2<Character> sample2() {
        MNode2<Character> root=new MNode2<>('r',null);
        MNode2<Character> a=new MNode2<>('a',root);
        root.children.add(a);
        MNode2<Character> b=new MNode2<>('b',a);
        a.children.add(b);
        MNode2<Character> c=new MNode2<>('c',b);
        b.children.add(c);
        MNode2<Character> d=new MNode2<>('d',b);
        b.children.add(d);
        MNode2<Character> e=new MNode2<>('e',d);
        d.children.add(e);
        MNode2<Character> f=new MNode2<>('f',root);
        root.children.add(f);
        MNode2<Character> g=new MNode2<>('g',f);
        f.children.add(g);
        MNode2<Character> j=new MNode2<>('j',f);
        f.children.add(j);
        MNode2<Character> h=new MNode2<>('h',f);
        g.children.add(h);
        MNode2<Character> i=new MNode2<>('i',f);
        h.children.add(i);
        return root;
    }
    public static void example() { // https://www.red-bean.com/sgf/var.html
        Node<Character> bRoot=sample();
        System.out.println("coded binary sample");
        G2.print("",bRoot);
        G2.print(bRoot);
        System.out.println("coded mway sample");
        MNode2<Character> mRoot=sample2();
        print(mRoot,"",true);
        System.out.println("converted mway sample");
        Node<Character> bRoot2=MNode2.oldFrom(mRoot);
        System.out.println(deepEquals(bRoot,bRoot2));
        System.out.println(structureDeepEquals(bRoot,bRoot2));
        G2.print("",bRoot2);
        G2.print(bRoot2);
        if(true) return;
        System.out.println("-----------------");
        print(mRoot,"  ",true);
        System.out.println("convert to general");
        MNode2<Character> mway=from(bRoot);
        print(mway,"  ",true);
        System.out.println("mway has "+mway.children.size()+" children.");
        System.out.println("convert back to binary");
        MNode2.processed.clear();
        Node<Character> root2=MNode2.oldFrom(mway);
        G2.print("",root2);
        G2.print(root2);
        System.out.println(deepEquals(bRoot,root2));
        System.out.println(structureDeepEquals(bRoot,root2));
    }
    public static void main(String[] arguments) { example(); }
}
