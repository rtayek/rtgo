package model;
import static org.junit.Assert.*;
import static sgf.Parser.getSgfData;
import java.io.*;
import java.util.*;
import org.junit.*;
import equipment.*;
import equipment.Board.Topology;
import io.IO;
import model.Model.MoveResult;
import model.Move.MoveImpl;
import sgf.*;
import utilities.MyTestWatcher;
public class OldModelTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    // random seems to work!
    @Test public void testResignOutOfOrder() {
        model.move(new MoveImpl(Stone.black,new Point()));
        model.move(Move.blackPass);
    }
    @Test public void testHash() {
        model.move(new MoveImpl(Stone.black,new Point()));
        model.move(Move.whitePass);
        model.move(Move.blackPass);
        // test for collision problem here
        // how?
    }
    @Test public void testgenerateAndMakeMoveTurn() {
        Stone who=model.turn();
        String move=model.generateAndMakeMove();
        if(move!=null) assertEquals(who.otherColor(),model.turn());
        else assertEquals(who,model.turn());
    }
    @Test public void testgenerateAndMakeMovesPreserveTurn() {
        Stone who=model.turn();
        Model.generateAndMakeMoves(model,1);
        assertEquals(who.otherColor(),model.turn());
    }
    @Test public void testgenerateAndMakeMovesPreserveTurn2() {
        Stone who=model.turn();
        Model.generateAndMakeMoves(model,2);
        assertEquals(who,model.turn());
    }
    @Test public void testgenerateAndMakeMovesPreserveTurn3() {
        model.setRoot(5,5);
        Stone who=model.turn();
        Model.generateAndMakeMoves(model,3);
        assertEquals(who.otherColor(),model.turn());
    }
    @Test public void testgenerateMoves() {
        model.setRoot(5,5);
        int n=model.movesToGenerate();
        Model.generateAndMakeMoves(model,n);
        //List<Move> expectedMoves=Model.movesToCurrentState(model);
        assertEquals(n,model.moves());
    }
    @Test public void testgenerateMovesTime() {
        for(int i=5;i<=19;i++) {
            model.setRoot(i,i);
            int n=model.movesToGenerate();
            Model.generateAndMakeMoves(model,n);
            List<Move> expectedMoves=model.movesToCurrentState();
            Model actual=new Model();
            actual.setRoot(i,i);
            actual.makeMoves(expectedMoves);
            model.board().isEqual(actual.board());
            List<Move> actualMoves=actual.movesToCurrentState();
            assertEquals(expectedMoves,actualMoves); // round trip
        }
    }
    @Test public void testGenerateSomeSillyMoves() { Model.generateSillyMoves(model,2); }
    @Test public void testGenerateAndReplayAllSillyMoves() {
        int n=model.board().width()*model.board().depth();
        Model.generateSillyMoves(model,n-1);
        List<Move> expectedMoves=model.movesToCurrentState();
        System.out.println(expectedMoves);
        Model actual=new Model();
        actual.makeMoves(expectedMoves);
        model.board().isEqual(actual.board());
        List<Move> actualMoves=actual.movesToCurrentState();
        assertEquals(expectedMoves,actualMoves); // round trip
    }
    @Test public void testGenerateSillyMovesTime() {
        for(int i=5;i<=19;i++) {
            if(i>5) break; // debuging
            model.setRoot(i,i);
            int n=model.movesToGenerate();
            System.out.println("start");
            Model.generateSillyMoves(model,n);
            List<Move> expectedMoves=model.movesToCurrentState();
            System.out.println(expectedMoves);
            Model actual=new Model();
            actual.setRoot(i,i);
            actual.makeMoves(expectedMoves);
            model.board().isEqual(actual.board());
            List<Move> actualMoves=actual.movesToCurrentState();
            assertEquals(expectedMoves,actualMoves); // round trip
        }
    }
    @Test public void testReplayMoves() {
        model.setRoot(19,19);
        int n=model.movesToGenerate();
        Model.generateAndMakeMoves(model,n);
        List<Move> expectedMoves=model.movesToCurrentState();
        Model actual=new Model();
        actual.setRoot(19,19);
        actual.makeMoves(expectedMoves);
        model.board().isEqual(actual.board());
        List<Move> actualMoves=actual.movesToCurrentState();
        assertEquals(expectedMoves,actualMoves); // round trip
    }
    @Test public void testRandomMove() throws Exception {
        Model model=new Model();
        model.setRoot(5,5);
        int n=model.movesToGenerate();
        for(int i=0;i<n;++i) {
            Point point=model.generateRandomMove();
            model.move(i%2==0?Stone.black:Stone.white,point);
            // how to test?
        }
    }
    @Test public void testReplayRandomMoves() throws Exception {
        Model model=new Model();
        model.setRoot(5,5);
        int n=model.movesToGenerate();
        Model.generateRandomMoves(model,n);
        List<Move> moves=model.movesToCurrentState();
        Model actual=new Model();
        actual.setRoot(5,5);
        actual.makeMoves(moves);
        model.board().isEqual(actual.board());
    }
    @Test public void testReplayRandomMovesList() throws Exception {
        Model model=new Model();
        model.setRoot(5,5);
        int n=model.movesToGenerate();
        List<Point> points=Model.generateRandomMovesInList(model,n);
        Model actual=new Model();
        actual.setRoot(5,5);
        for(Point point:points) model.move(new MoveImpl(model.turn(),point));
        model.board().isEqual(actual.board());
    }
    @Test public void testPushAndPop() {
        model.setBoard(Board.factory.create(9));
        int expected=model.board().id();
        model.push();
        // model.state.node=model.state.node.children.get(0);
        assertNotEquals("we got a new board",expected,model.board().id());
        model.pop();
        int actual=model.board().id();
        assertEquals(expected,actual);
    }
    @Test public void testListMoves() {
        Model model=new Model();
        // 19x19?
        List<Move> moves=model.movesToCurrentState();
        assertTrue(moves==null||moves.size()==0||moves.size()==1&&moves.get(0).equals(Move.nullMove));
        model.setRoot(5,5);
        moves=model.movesToCurrentState();
        assertTrue(moves==null||moves.size()==0||moves.size()==1&&moves.get(0).equals(Move.nullMove));
        model.move(model.turn(),new Point());
        // confused. problem when first node or top is wierd?
        moves=model.movesToCurrentState();
        assertTrue(moves.size()==1);
    }
    @Test public void testListMoves2() {
        model.setRoot(5,5);
        model.move(Stone.black,new Point());
        model.move(Stone.white,new Point(1,1));
        model.move(Stone.black,new Point(2,2));
        List<Move> moves=model.movesToCurrentState();
        List<Move> expected=Arrays.asList(new MoveImpl(Stone.black,new Point(0,0)),
                new MoveImpl(Stone.white,new Point(1,1)),new MoveImpl(Stone.black,new Point(2,2))); //"[move (0,0), move (1,1), move (2,2)]";
        assertEquals(expected,moves);
    }
    @Test public void testCopyConstructorWithEmpty() {
        // this is failing.
        // ex: (;FF[4]GM[1]AP[RTGO]C[root]SZ[19])
        // ac: (;RT[Tgo root];FF[4]GM[1]AP[RTGO]C[root]SZ[19])
        // similar to variations below.
        Model model=new Model();
        model.restore(new StringReader(Parser.empty));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue("first save fails",ok);
        String expected=stringWriter.toString();
        Model copy=new Model(model,model.name);
        stringWriter=new StringWriter();
        ok=copy.save(stringWriter);
        assertTrue("second save fails",ok);
        String actual=stringWriter.toString();
        assertEquals(expected,actual);
    }
    @Test public void testCopyConstructorWithVariationOfAVariation() {
        // this is failing.
        // actual has 2 copyies of ;RT[Tgo root] in the first node?
        Model model=new Model();
        model.restore(new StringReader(Parser.variationOfAVariation));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue("first save fails",ok);
        String expected=stringWriter.toString();
        Model copy=new Model(model,model.name);
        stringWriter=new StringWriter();
        ok=copy.save(stringWriter);
        assertTrue("second save fails",ok);
        String actual=stringWriter.toString();
        System.out.println("ex: "+expected);
        System.out.println("ac: "+actual);
        assertEquals(expected,actual);
    }
    @Test public void testSquares() {
        for(int width=3;width<=Model.LargestBoardSize;width++) for(int depth=3;depth<=Model.LargestBoardSize;depth++) {
            Board board=Board.factory.create(width,depth,Topology.normal);
            List<Point> points=Board.squares(1,width,depth);
            for(Point point:points) assertTrue(board.isOnBoard(point));
        }
    }
    @Test public void testHole() {
        for(int n=0;n<=Board.standard/2;++n) {
            List<Point> points=Board.holeInCenter(n,Board.standard,Board.standard);
            assertEquals((2*n+1)*(2*n+1),points.size());
            // maybe only works for odd sizes?
        }
    }
    @Test public void testSave() {
        File file=new File("tmp/saved.sgf");
        if(file.exists()) file.delete();
        boolean ok=model.save(IO.toWriter(file));
        assertTrue(ok);
        assertTrue(file.exists());
    }
    @Test public void testIsLegalMove() {
        model.setRoot();
        Move move=new MoveImpl(model.turn(),new Point());
        MoveResult actual=model.isLegalMove(move);
        assertEquals(MoveResult.legal,actual);
    }
    @Test public void testRole() {
        for(Model.Role role:Model.Role.values()) {
            model.setRole(role);
            assertEquals(role,model.role());
        }
    }
    @Test public void testWithSgfFile() {
        // hangs when ignore is remove from test go to node!
        String key="manyFacesTwoMovesAtA1AndR16";
        String sgfString=getSgfData(key);
        MNode root=MNode.quietLoad(new StringReader(sgfString));
        model.setRoot(root);
        if(model.board()!=null) {
            int expected=model.board().id();
            model.push();
            model.do_(model.currentNode().children.get(0));
            assertNotEquals(expected,model.board().id());
            model.pop();
            int actual=model.board().id();
            // what is this actually testing?
            assertEquals("we get the same board id.",expected,actual);
        } else System.out.println(key+" board is null.");
    }
    @Test public void testRTWithNoMoves() {
        Model model=new Model();
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String sgfString=stringWriter.toString();
        assertEquals("RT root node is removed","",sgfString);
        System.out.println("sgf from new model: '"+sgfString+"'");
    }
    @Test public void testRT() {
        Model model=new Model();
        model.move(Stone.black,new Point());
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String sgfString=stringWriter.toString();
        assertEquals("RT root node is removed","(;B[as])",sgfString);
    }
    /*@Test*/ public void testSgfFileFromLittleGolem() {
        // Node root=quietLoad(new File("url.sgf"));
        Model model=new Model();
        model.restore(IO.toReader(new File("url.sgf")));
    }
    Model model=new Model();
}
