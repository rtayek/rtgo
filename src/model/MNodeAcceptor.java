package model;
import java.util.*;
import io.Logging;
import sgf.*;
interface Acceptor<T extends Enumeration<T>> {
    // maybe we can generalize like this?
    // maybe hace preorder, inorder, postorder for the enumertors
    void accept(T t,Traverser<T> traverser);
    static class Traverser<T extends Enumeration<T>> { // half of a visitor
        // looks like it lists all of the variations?
        // and may be all of the games?
        Traverser(Acceptor<T> acceptor) { this.acceptor=acceptor; }
        void visit(T t) {
            stack.push(t);
            acceptor.accept(t,this);
            //for(MNode child:node.children) visit(child);
            stack.pop();
        }
        Acceptor<T> acceptor;
        Stack<T> stack=new Stack<>();
    }
    static class AcceptorImpl<T extends Enumeration<T>> implements Acceptor<T> {
        @Override public void accept(T t,Traverser<T> traverser) {
            // TODO Auto-generated method stub
        }
    }
    class GenericPrintAcceptor<T extends Enumeration<T>> implements Acceptor<T> {
        // aparently not used.
        //renamed because of a name collision.
        @Override public void accept(T t,Traverser<T> traverser) {
            Logging.mainLogger.info(t.toString());
            // maybe if enumeration just goes over children?
            // this is confused!
            //for(Property property:node.properties) Logging.mainLogger.info(property.p().getClass().getName()+" ");
            Logging.mainLogger.info(""+" "+"");
        }
    }
}
public interface MNodeAcceptor {
    // accept mnodes, maybe try to accept either mnode or sgf node?
    void accept(MNode node,Traverser traverser);
    static class Traverser { // half of a visitor
        // looks like it lists all of the variations?
        // and may be all of the games?
        Traverser(MNodeAcceptor acceptor) { this.acceptor=acceptor; }
        void visit(MNode node) {
            stack.push(node);
            acceptor.accept(node,this);
            if(node!=null) for(MNode child:node.children) visit(child);
            stack.pop();
        }
        MNodeAcceptor acceptor;
        Stack<MNode> stack=new Stack<>();
    }
    static class MakeList implements MNodeAcceptor {
        @Override public void accept(MNode games,Traverser traverser) { nodes.add(games); }
        public static List<MNode> toList(MNode games) {
            MakeList makeList=new MakeList();
            Traverser traverser=new Traverser(makeList);
            traverser.visit(games);
            return makeList.nodes;
        }
        List<MNode> nodes=new ArrayList<>();
    }
    abstract class MNodeAcceptorABC implements MNodeAcceptor {}
    // http://www.csse.monash.edu.au/~lloyd/tildeProgLang/PL-Block/
    public static class PrintAcceptor implements MNodeAcceptor {
        @Override public void accept(MNode node,Traverser traverser) {
            Logging.mainLogger.info(node+" "+node.properties.size()+" properties. ");
            for(SgfProperty property:node.properties) Logging.mainLogger.info(property.p().getClass().getName()+" ");
            Logging.mainLogger.info(""+" "+"");
        }
    }
    public static class MNodeFinder implements MNodeAcceptor {
        // seems like this is only used for least common ancestor
        MNodeFinder(MNode target) { this.target=target; }
        @Override public void accept(MNode node,Traverser traverser) {
            if(node!=null) {
                if(node.equals(target)) if(found==null) { //just the first one
                    found=node;
                    ancestors.addAll(traverser.stack);
                    // ancestors.add(node); // add in the target!
                } else Logging.mainLogger.warning(""+" "+"found another: "+found+" "+node);
            } else System.out.println("in accept node is null!");
        }
        public static MNodeFinder find(MNode target,MNode games) {
            MNodeFinder finder=new MNodeFinder(target);
            Traverser traverser=new Traverser(finder);
            traverser.visit(games);
            if(!finder.ancestors.contains(target))
                Logging.mainLogger.warning(""+" "+"ancesters does not contain target!");
            return finder;
        }
        final MNode target;
        public final List<MNode> ancestors=new ArrayList<>();
        MNode found;
    }
}
