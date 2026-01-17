package sgf;

import io.Logging;
import io.IOs;
import static io.IOs.noIndent;
import static io.IOs.standardIndent;
import static io.Logging.parserLogger;
import static org.junit.Assert.*;
import static model.MNodeAcceptor.MNodeFinder.*;
import static sgf.HexAscii.*;
import static sgf.Parser.restoreSgf;
import static sgf.SgfNode.SgfOptions.*;
import static utilities.Utilities.addFiles;
import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import core.formats.sgf.SgfNodeMapping;
import equipment.Board;
import equipment.Board.Topology;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import model.Model;
import model.MNodeAcceptor.MNodeFinder;
import model.MNodeAcceptor.MakeList;
import model.Move2;
import model.Move2.MoveType;
import model.MNodeTestIo;
import sgf.combine.Combine;
import io.Tee;
import utilities.TestKeys;
import utilities.Iterators.Longs;
import utilities.MyTestWatcher;

public class SgfUnitTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testSmokeRoundTrip() {
        List<Object> keys=List.of(
                TestKeys.emptyWithSemicolon,
                TestKeys.oneMoveAtA1,
                TestKeys.simpleWithVariations,
                TestKeys.manyFacesTwoMovesAtA1AndR16OnA9by9Board,
                new File(Parser.sgfPath,"variation.sgf")
        );
        for(Object key:keys) {
            String expectedSgf=prepareExpectedSgf(key);
            SgfHarness.assertSgfSaveAndRestore(key,expectedSgf);
            SgfHarness.assertSgfRoundTrip(key,expectedSgf);
            SgfHarness.assertRoundTripTwice(key,expectedSgf);
            SgfHarness.assertSgfCannonical(key,expectedSgf);
            SgfHarness.assertModelRoundTripTwice(expectedSgf);
        }
    }

    @Test public void testMultipleGamesRoundTrip() {
        for(Object[] params:SgfHarness.multipleGameParameters()) {
            Object key=params[0];
            String expectedSgf=prepareExpectedSgf(key);
            SgfHarness.assertMNodeRoundTrip(key,expectedSgf,SgfRoundTrip.MNodeSaveMode.standard,true);
            SgfHarness.assertMNodeRoundTrip(key,expectedSgf,SgfRoundTrip.MNodeSaveMode.direct,false);
        }
    }

    @Ignore @Test public void testOgsRoundTripIgnored() {
        Set<Object> objects=new LinkedHashSet<>(Parser.sgfFiles("ogs"));
        for(Object key:objects) {
            String expectedSgf=prepareExpectedSgf(key);
            SgfHarness.assertRoundTripTwice(key,expectedSgf);
        }
    }

    @Test public void testModelRoundTrip() {
        Model original=new Model();
        Model restored=new Model();
        SgfHarness.RoundTrip roundTrip=SgfHarness.roundTrip(original,restored);
        assertEquals(roundTrip.expected(),roundTrip.actual());
    }

    @Test public void testLizzie() {
        String id="LZ";
        P p=P.idToP.get(id);
        assertNotNull(p);
    }

    @Test public void testOblong() {
        String expectedSgf=SgfHarness.loadExpectedSgf(new java.io.File("ogs/lecoblong.sgf"));
        MNode games=SgfHarness.restoreMNode(expectedSgf);
        Model model=new Model("oblong");
        model.setRoot(games); // does this really trash everything correctly?
        model.down(0);
    }

    @Test public void testNoMoves() throws java.io.IOException {
        assertRoundTrip("new way",false);
    }

    @Test public void testNOneovesTheOldWay() throws java.io.IOException {
        assertRoundTrip("old way",true);
    }

    @Test public void testContainsQuotedControlCharacters() {
        String key="foo\\nba\r",string=key;
        boolean bad=containsQuotedControlCharacters(key,string);
        if(bad) {
            String actual=removeQuotedControlCharacters(string);
            boolean bad2=containsQuotedControlCharacters(key,actual);
            assertFalse(bad2);
        }
        assertTrue(bad);
    }

    @Test public void testRestoreNullReader() {
        assertRestoresNull(SgfHarness.restore((Reader)null));
    }

    @Test public void testRestoreEmpty() {
        assertRestoresNull(SgfHarness.restore(""));
    }

    @Test public void testSample() {
        SgfNode root=sample();
        String expected=SgfHarness.save(root,noIndent);
        Logging.mainLogger.info("sample sgf: "+expected);
        SgfHarness.assertSgfRestoreSaveStable(expected);
    }

    @Test public void testNybble() {
        for(byte expected=0;expected<ascii.length;expected++) {
            char c=encode(expected);
            byte actual=decode(c);
            assertEquals(""+expected,expected,actual);
        }
    }

    @Test public void testEncode15() {
        assertEncodedByte((byte)0x0f,"0f");
    }

    @Test public void testEncode16() {
        assertEncodedByte((byte)0x10,"10");
    }

    @Test public void testOneByte() {
        byte b=16;
        String expected=encode(new byte[] {b});
        byte[] bytes=decode(expected);
        String actual=encode(bytes);
        assertEquals(""+b,expected,actual);
    }

    @Test public void testByte() {
        assertRoundTripForAllBytes();
    }

    @Test public void testOneCharacterString() {
        assertRoundTripForAllBytes();
    }

    @Test public void testTwoCharacterString() {
        String s="a1";
        String encoded=encode(s.getBytes());
        byte[] decoded=decode(encoded);
        String actual=new String(decoded);
        assertEquals(s,s,actual);
    }

    @Test public void testString() {
        byte[] expected=testString.getBytes();
        String encodedBytes=encode(expected);
        byte[] actual=decode(encodedBytes);
        boolean ok=Arrays.equals(expected,actual);
        assertTrue(testString,ok);
        String newString=new String(actual);
        assertEquals(testString,testString,newString);
    }

    @Test public void testStringFast() {
        byte[] expected=testString.getBytes();
        String encodedBytes=encodeFast(expected);
        byte[] actual=decode(encodedBytes);
        boolean ok=Arrays.equals(expected,actual);
        assertTrue(testString,ok);
        String newString=new String(actual);
        assertEquals(testString,testString,newString);
    }

    @Test public void testUnknownPropertiesArePreserved() {
        // Build properties with IDs not known to P2 to force them into extras.
        P customXX=new P("XX","1234","unknown","none",""){};
        P customYY=new P("YY","1234","unknown","none",""){};
        SgfProperty keepRoot=SgfHarness.property(customXX,"keepme");
        SgfProperty keepChild=SgfHarness.property(customYY,"alsokeep");
        SgfProperty rootComment=SgfHarness.property(P.C,"hello");

        // Manually build MNode tree to avoid parser dropping unknown IDs
        MNode root=nodeWith(rootComment,keepRoot);
        MNode child=nodeWith(root,SgfHarness.property(P.B,"aa"),keepChild);
        root.children().add(child);

        Model model=new Model();

        SgfNodeMapping rootMapping=mapNode(root,model);
        assertEquals(List.of(rootComment,keepRoot),rootMapping.extras());
        rootMapping.applyExtrasTo(root);
        assertEquals(List.of(rootComment,keepRoot),root.extraProperties());

        SgfNodeMapping childMapping=mapNode(child,model);
        assertEquals(List.of(keepChild),childMapping.extras());
        childMapping.applyExtrasTo(child);
        assertEquals(List.of(keepChild),child.extraProperties());
    }

    @Test public void testMalformedPropertiesBecomeExtrasAndMappingIsPure() {
        SgfProperty badMove=SgfHarness.property(P.B,"a");
        SgfProperty emptyMove=new SgfProperty(P.W,List.of());
        SgfProperty badSize=SgfHarness.property(P.SZ,"x");

        MNode node=nodeWith(badMove,emptyMove,badSize);
        SgfNodeMapping mapping=mapNode(node);

        assertTrue(mapping.actions().isEmpty());
        assertEquals(List.of(badMove,emptyMove,badSize),mapping.extras());
        assertEquals(List.of(badMove,emptyMove,badSize),node.sgfProperties());
        assertTrue(node.extraProperties().isEmpty());

        mapping.applyExtrasTo(node);
        assertTrue(node.sgfProperties().isEmpty());
        assertEquals(List.of(badMove,emptyMove,badSize),node.extraProperties());
    }

    @Test public void testMoveAtA2() throws Exception {
        assertMove(Move2.whiteMoveAtA2);
        logMoves();
    }

    @Test public void testPass() throws Exception {
        assertMove(Move2.whitePass);
    }

    @Test public void testResign() throws Exception {
        assertMove(Move2.whiteResign);
    }

    @Test public void testSgfMakeMove() {
        Stone color=Stone.black;
        Point point=new Point(0,0);
        String expected=Coordinates.toGtpCoordinateSystem(point,sgfModel.board().width(),sgfModel.board().depth());
        assertEquals(Stone.vacant,sgfModel.board().at(point));
        assertEquals(0,sgfModel.moves());
        assertEquals(null,sgfModel.lastMoveGTP());
        sgfModel.sgfMakeMove(color,point);
        assertEquals(color,sgfModel.board().at(point));
        assertEquals(1,sgfModel.moves());
        assertEquals(expected,sgfModel.lastMoveGTP());
    }

    @Test public void testSgfUnmakeMove() {
        Point point=new Point(0,0);
        Stone expected=sgfModel.board().at(point);
        sgfModel.sgfMakeMove(Stone.black,point);
        sgfModel.sgfUnmakeMove(point);
        assertEquals(expected,sgfModel.board().at(point));
    }

    @Test public void testResignOutOfOrder() {
        model.move(new Move2(MoveType.move,Stone.black,new Point()));
        model.move(Move2.blackPass);
    }

    @Test public void testHash() {
        model.move(new Move2(MoveType.move,Stone.black,new Point()));
        model.move(Move2.whitePass);
        model.move(Move2.blackPass);
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
        assertGenerateAndMakeMovesPreserveTurn(null,1);
        assertGenerateAndMakeMovesPreserveTurn(null,2);
        assertGenerateAndMakeMovesPreserveTurn(5,3);
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
            assertGeneratedMovesRoundTrip(i,false,true,false);
        }
    }

    @Test public void testGenerateSomeSillyMoves() { Model.generateSillyMoves(model,2); }

    @Test public void testGenerateAndReplayAllSillyMoves() {
        int n=model.board().width()*model.board().depth();
        Model.generateSillyMoves(model,n-1);
        List<Move2> expectedMoves=model.movesToCurrentState();
        Logging.mainLogger.info(String.valueOf(expectedMoves));
        Model actual=new Model();
        assertMovesRoundTrip(model,actual,expectedMoves,false);
    }

    @Test public void testGenerateSillyMovesTime() {
        for(int i=5;i<=19;i++) {
            if(i>5) break; // debuging
            assertGeneratedMovesRoundTrip(i,true,false,true);
        }
    }

    @Test public void testReplayMoves() {
        model.setRoot(19,19);
        int n=model.movesToGenerate();
        Model.generateAndMakeMoves(model,n);
        List<Move2> expectedMoves=model.movesToCurrentState();
        model.ensureBoard();
        Model actual=newModelWithRoot(19,false);
        assertMovesRoundTrip(model,actual,expectedMoves,false);
    }

    @Test public void testRandomMove() throws Exception {
        Model model=newModelWithRoot(5,false);
        int n=model.movesToGenerate();
        for(int i=0;i<n;++i) {
            Point point=model.generateRandomMove();
            model.move(i%2==0?Stone.black:Stone.white,point);
            // how to test?
        }
    }

    @Test public void testReplayRandomMoves() throws Exception {
        Model model=newModelWithRoot(5,true);
        int n=model.movesToGenerate();
        Model.generateRandomMoves(model,n);
        List<Move2> moves=model.movesToCurrentState();
        Model actual=newModelWithRoot(5,true);
        actual.makeMoves(moves);
        model.board().isEqual(actual.board());
    }

    @Test public void testReplayRandomMovesList() throws Exception {
        Model model=newModelWithRoot(5,true);
        int n=model.movesToGenerate();
        List<Point> points=Model.generateRandomMovesInList(model,n);
        Model actual=newModelWithRoot(5,false);
        for(Point point:points) model.move(new Move2(MoveType.move,model.turn(),point));
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
        List<Move2> moves=model.movesToCurrentState();
        assertTrue(moves==null||moves.size()==0||moves.size()==1&&moves.get(0).equals(Move2.nullMove));
        model.setRoot(5,5);
        moves=model.movesToCurrentState();
        assertTrue(moves==null||moves.size()==0||moves.size()==1&&moves.get(0).equals(Move2.nullMove));
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
        List<Move2> moves=model.movesToCurrentState();
        List<Move2> expected=Arrays.asList(new Move2(MoveType.move,Stone.black,new Point(0,0)),
                new Move2(MoveType.move,Stone.white,new Point(1,1)),
                new Move2(MoveType.move,Stone.black,new Point(2,2))); //"[move (0,0), move (1,1), move (2,2)]";
        assertEquals(expected,moves);
    }

    @Test public void testCopyConstructorWithEmpty() {
        // this is failing.
        // ex: (;FF[4]GM[1]AP[RTGO]C[root]SZ[19])
        // ac: (;RT[Tgo root];FF[4]GM[1]AP[RTGO]C[root]SZ[19])
        // similar to variations below.
        assertCopyConstructorRoundTrip(Parser.empty,false);
    }

    @Test public void testCopyConstructorWithVariationOfAVariation() {
        // this is failing.
        // actual has 2 copyies of ;RT[Tgo root] in the first node?
        assertCopyConstructorRoundTrip(Parser.variationOfAVariation,true);
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
        boolean ok=model.save(IOs.toWriter(file));
        assertTrue(ok);
        assertTrue(file.exists());
    }

    @Test public void testIsLegalMove() {
        model.setRoot();
        Move2 move=new Move2(MoveType.move,model.turn(),new Point());
        Model.MoveResult actual=model.isLegalMove(move);
        assertEquals(Model.MoveResult.legal,actual);
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
        String sgfString=SgfHarness.loadExpectedSgf(key);
        MNode root=SgfHarness.quietLoadMNode(sgfString);
        model.setRoot(root);
        if(model.board()!=null) {
            int expected=model.board().id();
            model.push();
            model.do_(model.currentNode().children().get(0));
            assertNotEquals(expected,model.board().id());
            model.pop(); 
            // fails because do2 does not do a push  
            // first child is a setup node
            // "(;GM[1]FF[4]VW[]AP[Many Faces of Go:12.022]SZ[19]HA[0]ST[0]PB[ray]PW[ray]DT[2015-03-31]KM[7.5]RU[Chinese]BR[2 Dan]WR[2 Dan];B[as]BL[50]WL[60];W[qd]BL[1800]WL[1727])";
            int actual=model.board().id();
            // what is this actually testing?
            assertEquals("we get the same board id.",expected,actual);
        } else Logging.mainLogger.info(key+" board is null.");
    }

    @Test public void testRTWithNoMoves() {
        Model model=new Model();
        assertRoundTripHasRt(model,false,true);
    }

    @Test public void testRT() {
        Model model=new Model();
        model.move(Stone.black,new Point());
        assertRoundTripHasRt(model,false,true);
    }

    @Ignore @Test public void testCombineSimple() {
        assertCombine("simple.sgf");
    }

    @Ignore @Test public void testCombineFf4() {
        assertCombine("ff4_ex.sgf");
    }

    @Ignore @Test public void testCombineOldAnnotated() throws Exception {
        boolean ok=SgfHarness.roundTripTwice(new File(new File(Combine.pathToOldGames,"annotated"),"test.sgf"));
        if(!ok) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
    }

    @Ignore @Test public void testCombineMain() throws Exception {
        Tee.tee(new File(Combine.sgfOutputFilename));
        boolean ok=SgfHarness.roundTripTwice(new File(Combine.pathToHere,"ff4_ex.sgf"));
        if(!ok) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
        ok=SgfHarness.roundTripTwice(new File(new File(Combine.pathToOldGames,"annotated"),"test.sgf"));
        if(!ok) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
        if(!testCombine("test.sgf")) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
    }

    @Ignore @Test public void testKogosJosekiDictionary() throws Exception {
        withIgnoreMoveAndSetupFlags(true,() -> {
            File file=SgfHarness.firstExistingFile(
                    new File(Combine.pathToHere,kogoFilename),
                    new File("sgf",kogoFilename)
            );
            if(file==null) {
                assertTrue(kogoFilename+" not found",false);
                return;
            }
            boolean ok=SgfHarness.roundTripTwiceWithLogging(file);
            assertTrue(ok);
        });
    }

    @Ignore @Test public void testWierd() throws Exception {
        withIgnoreMoveAndSetupFlags(true,() -> {
            List<File> files=loadStrangeFiles();
            failFast=false;
            for(File file:files) try {
                SgfHarness.roundTripTwiceWithLogging(file);
            } catch(Exception e) {
                parserLogger.warning(this+" caught: "+e);
            }
        });
    }

    @Ignore @Test public void testFirstNodeOfWierd() throws Exception {
        withIgnoreMoveAndSetupFlags(true,() -> {
            List<File> files=loadStrangeFiles();
            failFast=false;
            List<SgfNode> all=new ArrayList<>();
            for(File file:files) try {
                SgfNode games=restoreSgf(IOs.toReader(file));
                all.add(games);
            } catch(Exception e) {
                Logging.mainLogger.info(this+" caught: "+e); //
            }
            parserLogger.warning(all.size()+" games in "+strangeDir);
        });
    }

    @Test public void testSgfCoordinates() {
        SgfNode expected=SgfHarness.nodeWithProperty(P.B,"AB"); // what is AB?
        String string=expected.sgfProperties.get(0).list().get(0);
        Point point=Coordinates.fromSgfCoordinates(string,Board.standard);
        String string2=Coordinates.toSgfCoordinates(point,Board.standard);
        SgfNode actual=SgfHarness.nodeWithProperty(P.B,string2);
        assertEquals(expected,actual);
    }

    @Test public void testHasAMoveFlag() {
        assertMoveFlags(P.B,true,true,true);
    }

    @Test public void testHasAMoveTypeFlag() {
        assertMoveFlags(P.BM,true,false,false);
    }

    @Test public void testBothFlagsFalse() {
        assertMoveFlagsOnRoot(P.AB,false,false);
    }

    @Test public void testConstructor() {
        SgfNode sgfNode=new SgfNode();
        sgfNode.sgfProperties=new ArrayList<>();
    }

    @Test public void testRTStructure() {
        MNode mRoot=new MNode(null);
        try {
            mRoot.sgfProperties().add(SgfHarness.property(P.RT,"Tgo root"));
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        SgfNode sgfNode=mRoot.toBinaryTree();
        Logging.mainLogger.info(String.valueOf(sgfNode));
        // this does not test anything yet.
    }

    @Test public void testNodeNotInTree() {
        ensureStructure();
        MNode mNode=new MNode(null);
        assertNotFound(mNode,root2);
    }

    @Test public void testFindSelfWithEqualsPredicate() {
        ensureStructure();
        assertFound(root1,root1,equalsPredicate);
    }

    @Test public void testFindSelfWithLabelPredicate() {
        ensureStructure();
        assertFoundWithLabel(root1,root1);
    }

    @Ignore @Test public void testFindRootWithEqualsPredicate() {
        ensureStructure();
        // should fail because we use equals which is just ==?
        MNodeFinder finder=MNodeFinder.find(root1,root2,equalsPredicate);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(root1.label(),finder.found.label()); // might not always be labeled?
    }

    @Test public void testFindRootWithLabelPredicate() {
        ensureStructure();
        assertFoundWithLabel(root1,root2);
    }

    @Test public void testFindFirstChildWithLabelPredicate() {
        ensureStructure();
        MNode child=root1.children().iterator().next();
        assertFoundWithLabel(child,root2);
    }

    @Test public void testFindNodeWithLabelPredicate() {
        ensureStructure();
        for(MNode node1:list1) {
            MNode remote=new MNode(null,node1.sgfProperties());
            remote.setLabel(node1.label());
            assertFoundWithLabel(remote,root2);
        }
    }

    @Test public void testFinderWithSimple() {
        SgfNode games=SgfHarness.restoreFromKey(TestKeys.simpleWithVariations);
        SgfHarness.assertFinderMatches(games);
    }

    @Test public void testFinderWith3Moves() {
        Model model=new Model();
        model.move(Stone.black,new Point(0,0));
        model.move(Stone.white,new Point(0,1)); // fails if black - check later
        model.move(Stone.black,new Point(0,2));
        SgfHarness.assertFinderMatches(model.root().toBinaryTree());
    }

    @Test public void testTwoMovesPath() {
        SgfMovesPath acceptor=new SgfMovesPath();
        restoreAndTraverse(acceptor);
        assertEquals(2,acceptor.moves.size());
    }

    @Test public void testTwoMoves() {
        SgfNode games=restoreAndTraverse(new SgfNoOpAcceptor());
        SgfNode move1=games.left;
        SgfNode move2=games.left.left;
        SgfProperty property1=move1.sgfProperties.get(0);
        String m1=property1.list().get(0);
        SgfProperty property2=move2.sgfProperties.get(0);
        String m2=property2.list().get(0);
    }

    /*@Test*/ public void testSgfFileFromLittleGolem() {
        // Node root=quietLoad(new File("url.sgf"));
        Model model=new Model();
        SgfHarness.restore(model,new File("url.sgf"));
    }

    private Model newModelWithRoot(int size,boolean ensureBoard) {
        Model model=new Model();
        model.setRoot(size,size);
        if(ensureBoard) model.ensureBoard();
        return model;
    }

    private void assertGenerateAndMakeMovesPreserveTurn(Integer size,int moves) {
        if(size!=null) model.setRoot(size,size);
        Stone who=model.turn();
        Model.generateAndMakeMoves(model,moves);
        Stone expected=moves%2==0?who:who.otherColor();
        assertEquals(expected,model.turn());
    }

    private void assertMovesRoundTrip(Model source,Model actual,List<Move2> expectedMoves,boolean ensureBoards) {
        if(ensureBoards) actual.ensureBoard();
        actual.makeMoves(expectedMoves);
        if(ensureBoards) source.ensureBoard();
        source.board().isEqual(actual.board());
        List<Move2> actualMoves=actual.movesToCurrentState();
        assertEquals(expectedMoves,actualMoves); // round trip
    }

    private void assertGeneratedMovesRoundTrip(int size,boolean silly,boolean ensureBoards,boolean log) {
        model.setRoot(size,size);
        int n=model.movesToGenerate();
        if(log) Logging.mainLogger.info("start");
        if(silly) Model.generateSillyMoves(model,n);
        else Model.generateAndMakeMoves(model,n);
        List<Move2> expectedMoves=model.movesToCurrentState();
        if(log) Logging.mainLogger.info(String.valueOf(expectedMoves));
        Model actual=newModelWithRoot(size,ensureBoards);
        assertMovesRoundTrip(model,actual,expectedMoves,ensureBoards);
    }

    private void assertCopyConstructorRoundTrip(String sgf,boolean log) {
        Model model=new Model();
        String expected=SgfHarness.restoreAndSave(model,sgf,"first save fails");
        Model copy=new Model(model,model.name);
        String actual=SgfHarness.save(copy,"second save fails");
        if(log) {
            Logging.mainLogger.info("ex: "+expected);
            Logging.mainLogger.info("ac: "+actual);
        }
        assertEquals(expected,actual);
    }

    private void assertRoundTripHasRt(Model model,boolean expectedBefore,boolean expectedAfter) {
        boolean hasRT=Model.isSentinel(model.root());
        assertEquals("unexpected RT before round trip",expectedBefore,hasRT);
        String sgfString=SgfHarness.save(model);
        model=new Model();
        SgfHarness.restore(model,sgfString);
        hasRT=Model.isSentinel(model.root());
        assertEquals("unexpected RT after round trip",expectedAfter,hasRT);
    }

    private void assertMoveFlags(P id,boolean expectedMoveType,boolean expectedMove,boolean logChildren) {
        SgfNode node=SgfHarness.nodeWithProperty(id,"inside the brackets");
        assertEquals(expectedMoveType,node.hasAMoveType);
        assertEquals(expectedMove,node.hasAMove);
        node.setFlags();
        node.checkFlags();
        MNode mNode=MNode.toGeneralTree(node);
        for(MNode child:mNode.children()) {
            if(logChildren) Logging.mainLogger.info(String.valueOf(child));
            child.setFlags();
            child.checkFlags();
            assertEquals(expectedMoveType,child.hasAMoveType());
            assertEquals(expectedMove,child.hasAMove());
        }
    }

    private void assertMoveFlagsOnRoot(P id,boolean expectedMoveType,boolean expectedMove) {
        SgfNode node=SgfHarness.nodeWithProperty(id,"inside the brackets");
        assertEquals(expectedMoveType,node.hasAMoveType);
        assertEquals(expectedMove,node.hasAMove);
        MNode mNode=MNode.toGeneralTree(node);
        assertEquals(expectedMoveType,mNode.hasAMoveType());
        assertEquals(expectedMove,mNode.hasAMove());
    }

    private static MNodeFinder assertFound(MNode target,MNode root,BiPredicate<MNode,MNode> predicate) {
        MNodeFinder finder=MNodeFinder.find(target,root,predicate);
        assertTrue(finder.ancestors.size()>0);
        return finder;
    }

    private static void assertFoundWithLabel(MNode target,MNode root) {
        MNodeFinder finder=assertFound(target,root,labelPredicate);
        assertEquals(target.label(),finder.found.label());
    }

    private static void assertNotFound(MNode target,MNode root) {
        MNodeFinder finder=MNodeFinder.find(target,root,equalsPredicate);
        assertTrue(finder.ancestors.size()==0);
    }

    private void ensureStructure() {
        if(structureReady) return;
        Object key=structureKey;
        watcher.key=key;
        expectedStructureSgf=SgfHarness.loadExpectedSgf(key);
        if(expectedStructureSgf==null) { return; }
        root1=SgfHarness.restoreMNode(expectedStructureSgf);
        root2=SgfHarness.restoreMNode(expectedStructureSgf);
        MNode.label(root1,new Longs());
        MNode.label(root2,new Longs());
        list1=MakeList.toList(root1);
        list2=MakeList.toList(root2);
        structureReady=true;
    }

    private SgfNode restoreAndTraverse(SgfAcceptor acceptor) {
        ensureStructure();
        SgfNode games=SgfHarness.restore(expectedStructureSgf);
        if(games!=null) SgfHarness.traverse(acceptor,games);
        return games;
    }

    private void assertCombine(String filename) {
        File file=fileInSgfDir(filename);
        Logging.mainLogger.info(String.valueOf(file));
        if(file.exists()) {
            assertTrue(file.toString(),file.exists());
            //assertTrue(testCombine(""+file));
            assertTrue(file.toString(),testCombine(filename));
        } else Logging.mainLogger.info("file "+file+" does not exist!");
    }

    private static File fileInSgfDir(String name) {
        return new File(Combine.pathToHere,name);
    }

    private boolean testCombine(String name) {
        Logging.mainLogger.info("test combine: "+name);
        try {
            SgfNode combined=Combine.combine(name);
            if(combined==null) {
                Logging.mainLogger.warning("combine returns null!");
                return false;
            }
            Logging.mainLogger.warning("combined");
            Logging.mainLogger.warning(String.valueOf(SgfHarness.save(combined,standardIndent)));
            Logging.mainLogger.warning("");
        } catch(Exception e) {
            Logging.mainLogger.warning("in testCombine()");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static List<File> loadStrangeFiles() {
        if(!strangeDir.exists()) fail(strangeDir+" does not exits!");
        List<File> files=addFiles(null,strangeDir);
        if(files.size()==0) fail("no files!");
        return files;
    }

    private static void assertRestoresNull(SgfNode games) {
        assertNull(games);
    }

    private String dtrt(Model m) {
        String actual=SgfHarness.restoreAndSave(m,sgf,restored->{
            Logging.mainLogger.info("restored, root: "+restored.root().toString());
            boolean hasRT=Model.isSentinel(restored.root());
            assertTrue(hasRT);
        });
        Logging.mainLogger.info("saved: "+actual);
        return actual;
    }

    private void assertRoundTrip(String name,boolean oldWay) throws java.io.IOException {
        Logging.setLevels(Level.INFO);
        Model m=new Model(name,oldWay);
        final String actual=dtrt(m);
        assertEquals(sgf,actual);
    }

    private SgfNode comment(String string,SgfNode left,SgfNode right) {
        SgfNode node=SgfHarness.nodeWithProperty(P.C,string);
        if(left==null&&right==null) return node;
        if(left!=null) left.left=node;
        else if(right!=null) right.right=node;
        else throw new RuntimeException("both left and right are not null!");
        return node;
    }

    private void print(SgfNode node) {
        Logging.mainLogger.info("saved sgf node "+SgfHarness.save(node,noIndent));
        Logging.mainLogger.info("----------------");
    }

    private SgfNode sample() { // maybe use redbean example?
        SgfNode root=comment("root",null,null);
        Logging.mainLogger.info("node root: "+root+" ");
        print(root);
        SgfNode left1=comment("left1",root,null);
        Logging.mainLogger.info("node left1: "+left1+" ");
        print(root);
        SgfNode right1=comment("right1",null,root);
        Logging.mainLogger.info("node right; "+right1+" ");
        print(root);
        SgfNode left1Left2=comment("left1.left2",left1,null);
        Logging.mainLogger.info("node left1Left2: "+left1Left2+" ");
        print(root);
        SgfNode left1right1=comment("left1.right1",null,left1);
        Logging.mainLogger.info("node left1.right1: "+left1right1+" ");
        print(root);
        Logging.mainLogger.info("node at end");
        return root;
    }

    private void assertRoundTripForAllBytes() {
        for(int bite=0;bite<256;++bite) {
            byte[] expected=new byte[] {(byte)bite};
            String string=encode(expected);
            byte[] actual=decode(string);
            String string2=encode(actual);
            assertEquals(string,string,string2);
        }
    }

    private void assertEncodedByte(byte expected,String expectedHex) {
        String s=encode(new byte[] {expected});
        assertEquals(expectedHex,expectedHex,s);
        byte[] actual=decode(s);
        assertEquals(String.valueOf(expected),expected,actual[0]);
    }
    private String prepareExpectedSgf(Object key) {
        String rawSgf=SgfHarness.loadExpectedSgf(key);
        return SgfHarness.prepareExpectedSgf(key,rawSgf);
    }
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
    private void withIgnoreMoveAndSetupFlags(boolean enabled,ThrowingRunnable action) throws Exception {
        boolean oldIgnoreFlags=SgfNode.ignoreMoveAndSetupFlags;
        SgfNode.ignoreMoveAndSetupFlags=enabled;
        try {
            action.run();
        } finally {
            SgfNode.ignoreMoveAndSetupFlags=oldIgnoreFlags;
        }
    }

    private static SgfNodeMapping mapNode(MNode node,Model model) {
        return core.formats.sgf.SgfDomainActionMapper.mapNode(contextFor(model),node);
    }

    private static SgfNodeMapping mapNode(MNode node) {
        return mapNode(node,null);
    }

    private static core.formats.sgf.SgfMappingContext contextFor(Model model) {
        int depth=Board.standard;
        if(model!=null) {
            if(model.board()!=null) depth=model.board().depth();
            else if(model.depthFromSgf()>0) depth=model.depthFromSgf();
        }
        return new core.formats.sgf.SgfMappingContext(depth,Model.sgfBoardTopology,Model.sgfBoardShape);
    }

    private static MNode nodeWith(SgfProperty... properties) {
        return nodeWith(null,properties);
    }

    private static MNode nodeWith(MNode parent,SgfProperty... properties) {
        MNode node=new MNode(parent);
        if(properties!=null) {
            for(SgfProperty property:properties) {
                node.sgfProperties().add(property);
            }
        }
        return node;
    }

    private void runBasicMove() {
        basicModel.move(Move2.blackMoveAtA1);
        moves=basicModel.moves();
        basicModel.move(expectedMove);
        String expectedSgf=MNodeTestIo.save(basicModel.root());
        actualMove=basicModel.lastMove2();
        String actualSgf2=SgfHarness.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.standard);
        assertEquals(expectedSgf,actualSgf2);
    }

    private void assertMove(Move2 move) {
        expectedMove=move;
        runBasicMove();
        assertEquals(expectedMove,actualMove);
    }

    private void logMoves() {
        Logging.mainLogger.info("expected move: "+expectedMove);
        Logging.mainLogger.info("actual move: "+actualMove);
    }

    private final String testString="0123456789abcdefghijklmnopqrstuvwxyz";
    private final String sgf="(;FF[4]GM[1]AP[RTGO]C[comment];B[as])";
    private final Object structureKey=TestKeys.manyFacesTwoMovesAtA1AndR16OnA9by9Board;
    private String expectedStructureSgf;
    private boolean structureReady;
    private MNode root1;
    private MNode root2;
    private List<MNode> list1;
    private List<MNode> list2;
    private boolean failFast=true;
    private static final File strangeDir=new File("strangesgf/");
    private static final String kogoFilename="KogosJosekiDictionary.sgf";

    Model model=new Model();
    {
        model.ensureBoard();
    }

    Model basicModel=new Model();
    {
        basicModel.ensureBoard();
    }
    Move2 expectedMove,actualMove;
    int moves;

    final Model sgfModel=new Model();
    {
        sgfModel.setBoard(Board.factory.create(Board.standard));
    }
}

