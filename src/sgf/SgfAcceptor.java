package sgf;
import static sgf.Parser.*;
import java.io.*;
import java.util.*;
import io.*;
import io.IOs;

public interface SgfAcceptor {
    void accept(SgfNode node);
    Stack<SgfNode> nodes();
    void setTraverser(Traverser traverser);
}
abstract class SgfAcceptorImpl implements SgfAcceptor {
    @Override public Stack<SgfNode> nodes() { return nodes; }
    @Override public void setTraverser(Traverser traverser) { this.traverser=traverser; }
    Stack<SgfNode> nodes=new Stack<>();
    Traverser traverser;
}
// http://www.csse.monash.edu.au/~lloyd/tildeProgLang/PL-Block/
class SgfPrintAcceptor extends SgfAcceptorImpl {
    @Override public void accept(SgfNode node) {
        if(node==null) return;
        // this feels wrong. maybe visit() should do the work
        // maybe not, maybe the parameter should be the visitor.
        System.out.print("accepting: "+node+" "+node.sgfProperties.size()+" properties. ");
        for(SgfProperty property:node.sgfProperties) { System.out.print(property.p().getClass().getName()+" "); }
        System.out.println();
    }
}
class SgfNoOpAcceptor extends SgfAcceptorImpl { @Override public void accept(SgfNode node) {} }
class SgfMovesPath extends SgfAcceptorImpl {
    // this may work to find the move list?
    @Override public void accept(SgfNode sgfNode) {
        boolean isAMove=false;
        for(SgfProperty property:sgfNode.sgfProperties) if((property.p().equals(P.W)||property.p().equals(P.B))) {
            isAMove=true;
            moves.add(sgfNode);
            i++;
        }
        if(!isAMove) System.out.println(sgfNode+" is not a move.");
    }
    int i;
    final ArrayList<SgfNode> moves=new ArrayList<>();
}
class GetAllSGFNodes extends SgfAcceptorImpl {
    @Override public void accept(SgfNode node) { allNodes.add(node); }
    public static List<SgfNode> findAllSgfNodes(SgfNode target,SgfNode games) {
        GetAllSGFNodes getAllSGFNodes=new GetAllSGFNodes();
        Traverser traverser=new Traverser(getAllSGFNodes);
        traverser.visit(games);
        return getAllSGFNodes.nodes;
    }
    final ArrayList<SgfNode> allNodes=new ArrayList<>();
}
class SgfNodeFinder extends SgfAcceptorImpl {
    SgfNodeFinder(SgfNode target) { this.target=target; }
    @Override public void accept(SgfNode node) {
        if(node.equals(target)) if(found==null) {
            found=node;
            pathToTarget.addAll(traverser.nodes);
        } else Logging.mainLogger.warning("found another: "+found+" "+node);
    }
    void checkMove() {
        for(SgfNode node:pathToTarget) if(node.hasAMove||node.hasAMoveType) //
            ; //
        else Logging.mainLogger.warning(node+" not a move or move type!");
    }
    public static SgfNodeFinder finder(SgfNode target,SgfNode root) {
        SgfNodeFinder finder=new SgfNodeFinder(target);
        Traverser traverser=new Traverser(finder);
        finder.setTraverser(traverser);
        traverser.visit(root);
        return finder;
    }
    final SgfNode target;
    public final ArrayList<SgfNode> pathToTarget=new ArrayList<>();
    SgfNode found;
}
class Traverser {
    Traverser(SgfAcceptor acceptor) { this.acceptor=acceptor; }
    /*
    void visit(Node node) {
        stack.push(node);
        acceptor.accept(node,this);
        for(Node child:node.children) visit(child);
        stack.pop();
    }
     */
    void visit(SgfNode sgfNode) {
        if(sgfNode==null) return;
        // does not accept sgfNode.right!
        nodes.push(sgfNode);
        acceptor.accept(sgfNode);
        /*
        SgfNode node=sgfNode.left; // down
        for(;node!=null&&node.right==null;node=node.left) {
            acceptor.accept(node);
        }
        for(;node!=null;node=node.right) { visit(node); }
         */
        visit(sgfNode.left);
        visit(sgfNode.right);
        SgfNode x=nodes.pop();
        if(!x.equals(sgfNode)) throw new RuntimeException();
    }
    void visitLeft(SgfNode SgfNode) { // main line, no variations
        nodes.push(SgfNode);
        acceptor.accept(SgfNode);
        SgfNode node=SgfNode.left;
        for(;node!=null;node=node.left) acceptor.accept(node);
        SgfNode x=nodes.pop();
        if(!x.equals(node)) throw new RuntimeException();
    }
    public static void main(String[] args) {
        SgfAcceptor sgfAcceptor=new SgfPrintAcceptor();
        Traverser traverser=new Traverser(sgfAcceptor);
        File dir=new File(sgfPath);
        System.out.println("||||");
        File file=new File(dir,"1635215-056-rtayek-Sighris.sgf");
        SgfNode games=restoreSgf(IOs.toReader(file));
        traverser.visit(games);
        System.out.println("||||");
        String sgfString=getSgfData("oneMoveAtA1");
        games=restoreSgf(new StringReader(sgfString));
        traverser.visit(games);
    }
    SgfAcceptor acceptor;
    Stack<SgfNode> nodes=new Stack<>();
}
