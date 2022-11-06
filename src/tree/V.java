package tree;
import java.util.List;
// https://softwareengineering.stackexchange.com/questions/224583/design-for-a-tree-using-a-visitor-pattern-how-to-implement-different-kinds-of-t
public interface V {
    interface Acceptor { void accept(Visitor visitor); }
    interface Visitor {
        //void visit(H h);
        //void visit (T t);
        //void visit(M m);
        void visit(Body body);
        void visit(Composit car);
        void visit(Engine engine);
        void visit(Wheel wheel);
    }
    class Wheel implements Acceptor {
        private final String name;
        public Wheel(final String name) { this.name=name; }
        public String getName() { return name; }
        @Override public void accept(Visitor visitor) { visitor.visit(this); }
    }
    class Body implements Acceptor { @Override public void accept(Visitor visitor) { visitor.visit(this); } }
    class Engine implements Acceptor {
        @Override public void accept(Visitor visitor) { visitor.visit(this); }
    }
    class Composit implements Acceptor {
        private final List<Acceptor> elements;
        public Composit() {
            this.elements=List.of(new Wheel("front left"),new Wheel("front right"),new Wheel("back left"),
                    new Wheel("back right"),new Body(),new Engine());
        }
        @Override public void accept(Visitor visitor) {
            // will be preorder, postorder, and inorder traversals
            for(Acceptor element:elements) { element.accept(visitor); }
            visitor.visit(this);
        }
    }
    class CarElementDoVisitor implements Visitor {
        @Override public void visit(Body body) { System.out.println("Moving my body"); }
        @Override public void visit(Composit car) { System.out.println("Starting my car"); }
        @Override public void visit(Wheel wheel) { System.out.println("Kicking my "+wheel.getName()+" wheel"); }
        @Override public void visit(Engine engine) { System.out.println("Starting my engine"); }
    }
    class CarElementPrintVisitor implements Visitor {
        @Override public void visit(Body body) { System.out.println("Visiting body"); }
        @Override public void visit(Composit car) { System.out.println("Visiting car"); }
        @Override public void visit(Engine engine) { System.out.println("Visiting engine"); }
        @Override public void visit(Wheel wheel) { System.out.println("Visiting "+wheel.getName()+" wheel"); }
    }
    public static void main(String[] args) {
        Composit car=new Composit();
        car.accept(new CarElementPrintVisitor());
        car.accept(new CarElementDoVisitor());
    }
}
