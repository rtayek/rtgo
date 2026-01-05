package model;
import static sgf.Parser.*;
import java.io.*;
import java.util.Stack;
import io.*;
import io.IOs;

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
        for(MNode child:node.children()) visit(child);
        stack.pop();
    }
    Stack<MNode> stack() { return stack; }
    public static void main(String[] args) {
        // this is broken. fix it!
        File file=null;
        MNodeAcceptor acceptor=new MNodeAcceptor.PrintAcceptor();
        Traverser traverser=new Traverser(acceptor);
        String expectedSgf=getSgfData("simpleWithVariations");
        SgfNode games_=restoreSgf(IOs.toReader(expectedSgf));
        if(games_!=null) if(games_.right!=null) System.out.println(" 2 more than one game!");
        MNode games=MNode.toGeneralTree(games_);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        if(true) return;
        file=new File(sgfPath,"1635215-056-rtayek-Sighris.sgf");
        games_=restoreSgf(IOs.toReader(file));
        games=MNode.toGeneralTree(games_);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        expectedSgf=getSgfData("oneMoveAtA1");
        games_=restoreSgf(IOs.toReader(expectedSgf));
        games=MNode.toGeneralTree(games_);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        file=new File(sgfPath,"ray-SmartGo-2022-01-07.sgf");
        games_=restoreSgf(IOs.toReader(file));
        Logging.mainLogger.warning(file+" "+games_);
        games=MNode.toGeneralTree(games_);
        Logging.mainLogger.warning(file+" "+games);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        expectedSgf=getSgfData("empty");
        games_=restoreSgf(IOs.toReader(expectedSgf));
        Logging.mainLogger.warning(file+" "+games_);
        games=MNode.toGeneralTree(games_);
        Logging.mainLogger.warning(file+" "+games);
        traverser.visit(games);
        Logging.mainLogger.info(""+" "+"|||");
        file=new File(sgfPath,"reallyempty.sgf");
        games_=restoreSgf(IOs.toReader(file));
        System.out.println(games_);
        games=MNode.toGeneralTree(games_);
        System.out.println(games);
        traverser.visit(games);
    }
    MNodeAcceptor acceptor;
    Stack<MNode> stack=new Stack<>();
}
