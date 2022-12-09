package tree;
import static tree.MNode.print;
import tree.G2.Characters;
public class RedBean {
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
    static MNode<Character> mway() {
        MNode<Character> root=new MNode<>('r',null);
        MNode<Character> a=new MNode<>('a',root);
        root.children.add(a);
        MNode<Character> b=new MNode<>('b',a);
        a.children.add(b);
        MNode<Character> c=new MNode<>('c',b);
        b.children.add(c);
        MNode<Character> d=new MNode<>('d',b);
        b.children.add(d);
        MNode<Character> e=new MNode<>('e',d);
        d.children.add(e);
        MNode<Character> f=new MNode<>('f',root);
        root.children.add(f);
        MNode<Character> g=new MNode<>('g',f);
        f.children.add(g);
        MNode<Character> j=new MNode<>('j',f);
        f.children.add(j);
        MNode<Character> h=new MNode<>('h',f);
        g.children.add(h);
        MNode<Character> i=new MNode<>('i',f);
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
        MNode<Character> mRoot=mway();
        i=new Characters();
        MNode.<Character> relabel(mRoot,i); // since i uses 'r' for the root.
        System.out.println("after relabel");
        print(mRoot,"",true);
        // since mway to binary seems to work,
        // let's see why binary to mway fails.
    }
    public static void main(String[] arguments) { example(); }
}
