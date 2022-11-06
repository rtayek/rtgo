package tree;
import java.util.*;
import tree.H.T.M;
interface H {
    Object data();
    interface Acceptor { void accept(Visitor visitor); }
    interface T extends H,Acceptor { //
        interface B extends T { //
            B left();
            B right();
            static B create(B left,B right) { return new BImpl(left,right); }
        }
        interface M extends T,Acceptor { //
            Set<M> children();
            void add(M m);
            static M create() { return new MImpl(); }
        }
        class BImpl implements B {
            BImpl(B left,B right) { this.left=left; this.right=right; }
            @Override public B left() { return left; }
            @Override public B right() { return right; }
            B left,right;
            @Override public Object data() { return data; }
            @Override public void accept(Visitor visitor) { System.out.println("acepting: "+visitor); }
            Object data;
        }
        class MImpl implements M,Acceptor { //
            @Override public Set<M> children() { return children; }
            @Override public void add(M m) { children.add(m); }
            Set<M> children=new LinkedHashSet<>();
            @Override public Object data() { return data; }
            @Override public void accept(Visitor visitor) { System.out.println("acepting: "+visitor); }
            Object data;
        }
    }
    interface Visitor {
        void visit(H h); // maybe we can't do this?
        void visit(T t);
        void visit(M m);
        void visit(Composit car);
        void visit(Engine engine);
    }
    class Engine implements Acceptor { @Override public void accept(Visitor visitor) { visitor.visit(this); } }
    class Composit implements Acceptor {
        private final List<Acceptor> elements;
        public Composit() { this.elements=List.of(
                // new Body(),new Engine()
                );
        }
        @Override public void accept(Visitor visitor) {
            // will be preorder, postorder, and inorder traversals
            for(Acceptor element:elements) { element.accept(visitor); }
            visitor.visit(this);
        }
    }
    class CarElementDoVisitor implements Visitor {
        @Override public void visit(Composit car) { System.out.println("Starting my car"); }
        @Override public void visit(Engine engine) { System.out.println("Starting my engine"); }
        @Override public void visit(H h) { System.out.println("do visitor: "+h); }
        @Override public void visit(T t) { System.out.println("do visitor: "+t); }
        @Override public void visit(M m) { System.out.println("do visitor: "+m); }
    }
    class CarElementPrintVisitor implements Visitor {
        @Override public void visit(Composit car) { System.out.println("Visiting car"); }
        @Override public void visit(Engine engine) { System.out.println("Visiting engine"); }
        @Override public void visit(H h) { System.out.println("print visitor: "+h); }
        @Override public void visit(T t) { System.out.println("print visitor: "+t); }
        @Override public void visit(M m) { System.out.println("print visitor: "+m); }
    }
    public static void main(String[] args) {
        Composit car=new Composit();
        car.accept(new CarElementPrintVisitor());
        car.accept(new CarElementDoVisitor());
    }
}
