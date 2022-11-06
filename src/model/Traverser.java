package model;
import static sgf.Parser.getSgfData;
import java.io.*;
import java.util.Stack;
import io.*;
import sgf.*;
class Traverser { // half of a visitor
    // also have in in sgf/
    // this is not ised anywhere else
    Traverser(MNodeAcceptor acceptor) { this.acceptor=acceptor; }
    // moved this into acceptor.
    void visit(MNode node) {
        if(node==null) return;
        stack.push(node);
        //acceptor.accept(node,this);
        for(MNode child:node.children) visit(child);
        stack.pop();
    }
    Stack<MNode> stack() { return stack; }
    public static void main(String[] args) {
        // this is broken. fix it!
        File file=null;
        MNodeAcceptor acceptor=new MNodeAcceptor.PrintAcceptor();
        Traverser traverser=new Traverser(acceptor);
        String expectedSgf=getSgfData("simpleWithVariations");
        SgfNode games_=new Parser().parse(expectedSgf);
        MNode games=MNode.toGeneralTree(games_);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        if(true) return;
        file=new File("sgf","1635215-056-rtayek-Sighris.sgf");
        games_=new Parser().parse(IO.toReader(file));
        games=MNode.toGeneralTree(games_);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        expectedSgf=getSgfData("oneMoveAtA1");
        games_=new Parser().parse(new StringReader(expectedSgf));
        games=MNode.toGeneralTree(games_);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        file=new File("sgf","ray-SmartGo-2022-01-07.sgf");
        games_=new Parser().parse(IO.toReader(file));
        Logging.mainLogger.warning(file+" "+games_);
        games=MNode.toGeneralTree(games_);
        Logging.mainLogger.warning(file+" "+games);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        expectedSgf=getSgfData("empty");
        games_=new Parser().parse(new StringReader(expectedSgf));
        Logging.mainLogger.warning(file+" "+games_);
        games=MNode.toGeneralTree(games_);
        Logging.mainLogger.warning(file+" "+games);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        file=new File("sgf","reallyempty.sgf");
        games_=new Parser().parse(IO.toReader(file));
        System.out.println(games_);
        games=MNode.toGeneralTree(games_);
        System.out.println(games);
        traverser.visit(games);
    }
    MNodeAcceptor acceptor;
    Stack<MNode> stack=new Stack<>();
}
