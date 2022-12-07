package tree.catalan;
import static tree.catalan.RedBean.MNode2.print;
import java.util.*;
import java.util.function.Consumer;
import tree.catalan.G2.*;
public class RedBean {
    public static class MNode2<T> {
        public void preorder(Consumer<MNode2<T>> consumer) {
            if(consumer!=null) consumer.accept(this);
            for(MNode2<T> child:children) if(child!=null) child.preorder(consumer);
        }
        public static <T>void preorder(MNode2<T> mNode2,Consumer<MNode2<T>> consumer) {
            if(mNode2!=null) mNode2.preorder(consumer);
        }
        static <T> void relabel(MNode2<T> node,final Iterator<T> i) {
            Consumer<MNode2<T>> relabel=x-> { if(x!=null&&i!=null) x.data=i.hasNext()?i.next():null; };
            preorder(node,relabel);
        }
        
        @Override public String toString() { return "MNode2 [data="+data+"]"; }
        public MNode2(T data,MNode2<T> parent) {
            // maybe just use t as first argument?
            this.parent=parent;
            this.data=data;
        }
        public static <T> Node<T> oldFrom(MNode2<T> mNode2) {
            if(mNode2==null) { return null; }
            // this is broken. maybe not
            //System.out.println("processing: "+mNode2.data);
            // did we remove the extra node we added?
            boolean ok=processed.add(mNode2.data);
            if(!ok) System.out.println(mNode2.data+" MNode2 already processed!");
            Node<T> left=null,tail=null;
            for(int i=0;i<mNode2.children.size();++i) {
                if(i==0) {
                    MNode2<T> child=mNode2.children.get(i);
                    if(child!=null) {
                        //System.out.println("first child: "+child.data);
                        left=tail=oldFrom(mNode2.children.get(i));
                        //System.out.println("left: "+left.data+" first "+left.data);
                        // is this throwing if there is a variation on the first move in the game?
                        if(left.right!=null) {
                            //ystem.out.println("wierdness at: "+left.data);
                            // maybe not so weird after a;l?
                            //throw new RuntimeException("wierdness!");
                        }
                    }
                } else {
                    MNode2<T> child=mNode2.children.get(i);
                    //System.out.println("left: "+left.data+" child: "+child.data);
                    Node<T> newRight=oldFrom(mNode2.children.get(i));
                    //System.out.println("left: "+left.data+" added "+newRight.data);
                    //System.out.println("left: "+left.data+" tail "+tail.data);
                    tail.right=newRight;
                    tail=newRight;
                    //System.out.println("new tail "+tail.data);
                }
            }
            Node<T> binaryNode=new Node<>(mNode2.data,left,null); // first child
            return binaryNode;
        }

        public static <T> void print(MNode2<T> tree,String indent,boolean last) {
            System.out.println(indent+"+- "+(tree!=null?tree.data:"0"));
            indent+=last?"   ":"|  ";
            if(tree!=null) for(int i=0;i<tree.children.size();i++) {
                print(tree.children.get(i),indent,i==tree.children.size()-1);
            }
        }
        private boolean deepEquals_(MNode2<T> other,boolean ckeckEqual) {
            // lambda?
            if(this==other) return true;
            else if(other==null) return false;
            if(ckeckEqual) if(!equals(other)) return false;
            if(children.size()!=other.children.size()) return false;
            for(int i=0;i<children.size();++i) {
                MNode2<T> child=children.get(i);
                MNode2<T> otherChild=other.children.get(i);
                if(!child.deepEquals_(otherChild,ckeckEqual)) return false;
            }
            return true;
        }
        public static <T> boolean deepEquals(MNode2<T> node,MNode2<T> other) {
            return node!=null?node.deepEquals_(other,true):other==null;
        }
        public static <T> boolean structureDeepEquals(MNode2<T> node,MNode2<T> other) {
            return node!=null?node.deepEquals_(other,false):other==null;
        }
        public static <T> LinkedHashSet<T> processed() {
            return processed;
        }
        MNode2<T> parent;
        ArrayList<MNode2<T>> children=new ArrayList<>();
        // add a set temporarily to see if we are adding stuff in twice?
        public T data;
        final int id=++ids;
        static int ids;
        static LinkedHashSet processed=new LinkedHashSet<>();
    }
    static Node<Character> binary() {
        // this may not be coded up correctly
        Node<Character> e=new Node<>('e');
        Node<Character> d=new Node<>('d',e,null);
        Node<Character> c=new Node<>('c',null,d);
        Node<Character> b=new Node<>('b',c,null);
        Node<Character> i=new Node<>('i');
        Node<Character> h=new Node<>('h',i,null);
        Node<Character> j=new Node<>('j');
        Node<Character> g=new Node<>('g',h,j);
        Node<Character> f=new Node<>('f',g,null);
        Node<Character> a=new Node<>('a',b,f);
        Node<Character> root=new Node<>('r',a,null);
        return root;
    }
    static MNode2<Character> mway() {
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
        Node<Character> bRoot=binary();
        System.out.println("coded binary sample");
        //G2.print(bRoot,"");
        Characters i=new Characters();
        Node.<Character> relabel(bRoot,i); // since i uses 'r' for the root.
        System.out.println("after relabel");
        G2.print(bRoot,"");
        System.out.println("coded mway sample");
        MNode2<Character> mRoot=mway();
        i=new Characters();
        MNode2.<Character> relabel(mRoot,i); // since i uses 'r' for the root.
        System.out.println("after relabel");
        print(mRoot,"",true);
        // since mway to binary seems to work,
        // let's see why binary to mway fails.
        
    }
    public static void main(String[] arguments) { example(); }
}
