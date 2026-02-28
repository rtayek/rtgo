package sgf;

import io.Logging;
import io.IOs;
import static org.junit.Assert.*;
import static sgf.SgfNode.SgfOptions.containsQuotedControlCharacters;
import static com.tayek.util.core.Misc.implies;
import com.tayek.util.io.FileIO;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import model.Model;
import model.ModelHelper.ModelSaveMode;
import model.ModelTrees;
import model.Navigate;
import io.TestIo;
import com.tayek.util.core.ParameterArray;
import utilities.SgfTestParameters;

public final class SgfHarness {
    private SgfHarness() {}

    // Parser support
    static String loadExpectedSgf(Object key) {
        return SgfIo.loadExpectedSgf(key);
    }

    public static String prepareExpectedSgf(Object key,String sgf) {
        String normalized=SgfIo.prepareExpectedSgf(key,sgf);
        assertFalse(containsQuotedControlCharacters(key.toString(),normalized));
        return normalized;
    }

    static void assertSgfDelimiters(String sgf,Object key) {
        if(sgf!=null) if(sgf.startsWith("(")) {
            if(!sgf.endsWith(")")) Logging.mainLogger.info(key+" does not end with an close parenthesis");
        } else if(!sgf.equals("")) {
            throw new AssertionError(key+" does not start with an open parenthesis");
        }
    }

    static void logBadParentheses(String sgf,Object key,String label) {
        SgfIo.logBadParentheses(sgf,key,label);
    }

    static void assertKeyPresent(Object key,String expectedSgf) {
        if(!(key!=null||expectedSgf!=null)) {
            Logging.mainLogger.info("key!=null||expectedSgf!=null");
            IOs.stackTrace(10);
        }
        assertTrue(key!=null||expectedSgf!=null);
    }

    static SgfNode restoreExpectedSgf(String expectedSgf,Object key) {
        return restoreExpectedSgf(expectedSgf,key,true);
    }

    static SgfNode restoreExpectedSgf(String expectedSgf,Object key,boolean checkDelimiters) {
        if(checkDelimiters) assertSgfDelimiters(expectedSgf,key);
        return SgfIo.restore(FileIO.toReader(expectedSgf));
    }

    static SgfNode assertParse(Object key,String expectedSgf) {
        return restoreExpectedSgf(expectedSgf,key,true);
    }

    static SgfNode assertParse(Object key,String expectedSgf,boolean checkDelimiters) {
        return restoreExpectedSgf(expectedSgf,key,checkDelimiters);
    }

    static void assertHexAscii(Object key,String expectedSgf) {
        String encoded=expectedSgf!=null?HexAscii.encode(expectedSgf.getBytes()):null;
        String actualSgf=encoded!=null?HexAscii.decodeToString(encoded):null;
        String keyString=key!=null?key.toString():null;
        assertTrue(keyString,implies(expectedSgf==null,encoded==null));
        assertTrue(keyString,implies(encoded==null,actualSgf==null));
        assertEquals(keyString,expectedSgf,actualSgf);
    }

    static SgfNode assertFlags(Object key,String expectedSgf,boolean oldFlags) {
        return assertFlags(key,expectedSgf,oldFlags,true);
    }

    static SgfNode assertFlags(Object key,String expectedSgf,boolean oldFlags,boolean checkDelimiters) {
        SgfNode games=restoreExpectedSgf(expectedSgf,key,checkDelimiters);
        if(games==null) return games;
        if(oldFlags) games.oldPreorderCheckFlags();
        else games.preorderCheckFlags();
        return games;
    }

    // Round-trip support
    static void assertNoLineFeeds(String sgf) {
        if(sgf!=null) assertFalse(sgf.contains("\n"));
    }

    static String prepareSgf(String sgf) {
        return SgfIo.prepareSgf(sgf);
    }

    static String prepareActual(String actualSgf) {
        return prepareSgf(actualSgf);
    }

    static void assertPreparedEquals(Object key,String expectedSgf,String preparedSgf) {
        assertEquals(key.toString(),expectedSgf,preparedSgf);
    }

    static void assertPreparedRoundTrip(Object key,String expectedSgf,String actualSgf) {
        assertPreparedEquals(key,expectedSgf,prepareActual(actualSgf));
    }

    static void assertPreparedRoundTripWithParenthesesCheck(Object key,String expectedSgf,String actualSgf,String label) {
        String prepared=prepareActual(actualSgf);
        logBadParentheses(prepared,key,label);
        assertPreparedEquals(key,expectedSgf,prepared);
    }

    static void assertSgfSaveAndRestore(Object key,String expectedSgf) {
        assertNoLineFeeds(expectedSgf);
        SgfNode expected=SgfIo.restore(FileIO.toReader(expectedSgf));
        SgfNode actualSgf=saveAndRestore(expected);
        if(expected!=null) assertTrue(key.toString(),expected.deepEquals(actualSgf));
    }

    static void assertSgfRoundTrip(Object key,String expectedSgf) {
        if(expectedSgf==null) return;
        String actualSgf=restoreAndSaveToString(expectedSgf);
        actualSgf=prepareSgf(actualSgf);
        if(actualSgf.length()==expectedSgf.length()+1) if(actualSgf.endsWith(")")) {
            Logging.mainLogger.info(key+"removing extra ')' "+actualSgf.length());
            if(true) throw new RuntimeException(key+"removing extra ')' "+actualSgf.length());
            actualSgf=actualSgf.substring(0,actualSgf.length()-1);
        }
        assertPreparedEquals(key,expectedSgf,actualSgf);
    }

    static void assertRoundTripTwice(Object key,String expectedSgf) {
        assertNoLineFeeds(expectedSgf);
        boolean isOk=SgfIo.roundTripTwice(FileIO.toReader(expectedSgf));
        assertTrue(key.toString(),isOk);
    }

    static void assertSgfCannonical(Object key,String expectedSgf) {
        assertNoLineFeeds(expectedSgf);
        assertSgfRestoreSaveStable(expectedSgf,key);
    }

    static void assertMNodeRoundTrip(Object key,String expectedSgf,SgfIo.MNodeSaveMode saveMode,boolean logExpected) {
        if(logExpected) logBadParentheses(expectedSgf,key,"ex");
        StringWriter writer=new StringWriter();
        SgfIo.mNodeRoundTrip(FileIO.toReader(expectedSgf),writer,saveMode);
        String actualSgf=writer.toString();
        assertPreparedRoundTripWithParenthesesCheck(key,expectedSgf,actualSgf,"ac");
    }

    // Model round-trip support
    static void assertModelRestoreAndSave(Object key,String expectedSgf) {
        Model model=newModel("");
        assertModelRestoreAndSave(key,expectedSgf,model);
    }

    static void assertModelRestoreAndSave(Object key,String expectedSgf,Model model) {
        ModelTrees.restore(model,FileIO.toReader(expectedSgf));
        String actualSgf=saveModel(model);
        assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRoundTripToString(Object key,String expectedSgf,ModelSaveMode saveMode,boolean log) {
        String actualSgf=modelRoundTripToString(expectedSgf,saveMode);
        if(log) {
            Logging.mainLogger.info("ex: "+expectedSgf);
            Logging.mainLogger.info("ac: "+actualSgf);
        }
        assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRoundTripTwice(String sgf) {
        String expected=modelRoundTripToString(sgf,ModelSaveMode.sgfNode);
        String actual=modelRoundTripToString(expected,ModelSaveMode.sgfNode);
        assertEquals(expected,actual);
    }

    static void assertCheckBoardInRoot(Object key,String sgf) {
        if(key==null) IOs.stackTrace(10);
        assertNotNull(key);
        checkBoardInRoot(key,sgf);
        Logging.mainLogger.info("after key: "+key);
    }

    static void assertSgfRestoreAndSave(Object key,String expectedSgf) {
        String actualSgf=restoreAndSaveToString(expectedSgf);
        assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRestoreAndSaveWithExplicitModel(Object key,String expectedSgf) {
        Model model=newModel();
        Logging.mainLogger.info("ex: "+expectedSgf);
        ModelTrees.restore(model,FileIO.toReader(expectedSgf));
        String actualSgf=TestIo.saveToString(key.toString(),writer->ModelTrees.save(model,writer));
        actualSgf=SgfNode.options.removeUnwanted(actualSgf);
        assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelSaveFromMNode(Object key,String expectedSgf,MNode root) {
        Model model=newModelWithRoot(root);
        String actualSgf=TestIo.saveToString(key.toString(),writer->ModelTrees.save(model,writer));
        assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertCanonicalRoundTripTwice(Object key,String expectedSgf) {
        assertNoLineFeeds(expectedSgf);
        try {
            String expectedSgf2=restoreAndSavePrepared(expectedSgf);
            String actualSgf=restoreAndSavePrepared(expectedSgf2);
            assertEquals(key.toString(),expectedSgf2,actualSgf);
        } catch(Exception e) {
            fail("'"+key+"' caught: "+e);
        }
    }

    private static String restoreAndSavePrepared(String sgf) {
        Model model=restoreNewModel(sgf);
        String saved=saveModel(model);
        return prepareActual(saved);
    }

    private static boolean checkBoardInRoot(Object key,String sgf) {
        // move this?
        if(key==null) { Logging.mainLogger.info("key is null!"); return true; }
        Model original=restoreNewModel(sgf);
        boolean hasABoard=original.board()!=null;
        Model model=restoreNewModel(sgf);
        if(model.board()==null); // Logging.mainLogger.info("model has no board!");
        else Logging.mainLogger.info("model has a board!");
        Navigate.down.do_(model);
        Model.mainLineFromCurrentPosition(model);
        return hasABoard;
    }

    static void assertSgfRestoreSaveStable(String sgf,Object key) {
        String[] actual=restoreAndSaveTwice(sgf);
        assertEquals(key.toString(),actual[0],actual[1]);
    }

    static void assertSgfRestoreSaveStable(String sgf) {
        String[] actual=restoreAndSaveTwice(sgf);
        assertEquals(actual[0],actual[1]);
    }

    private static Model newModel() {
        return new Model();
    }

    private static Model newModel(String name) {
        return new Model(name);
    }

    private static Model newModelWithRoot(MNode root) {
        Model model=newModel();
        model.setRoot(root);
        return model;
    }

    private static String[] restoreAndSaveTwice(String sgf) {
        String actual=restoreAndSaveToString(sgf);
        String actual2=restoreAndSaveToString(actual);
        return new String[] {actual,actual2};
    }

    private static String restoreAndSaveToString(String sgf) {
        StringWriter writer=new StringWriter();
        SgfIo.restoreAndSave(FileIO.toReader(sgf),writer);
        return writer.toString();
    }

    private static SgfNode saveAndRestore(SgfNode expected) {
        if(expected==null) return null;
        String sgf=SgfIo.saveSgfToString(expected,IOs.noIndent);
        return SgfIo.restore(FileIO.toReader(sgf));
    }

    private static String saveModel(Model model) {
        StringWriter writer=new StringWriter();
        ModelTrees.save(model,writer);
        return writer.toString();
    }

    private static Model restoreNewModel(String sgf) {
        Model model=new Model();
        ModelTrees.restore(model,FileIO.toReader(sgf));
        return model;
    }

    private static String modelRoundTripToString(String sgf,ModelSaveMode saveMode) {
        StringWriter writer=new StringWriter();
        model.ModelHelper.modelRoundTrip(FileIO.toReader(sgf),writer,saveMode);
        return writer.toString();
    }

    static void traverse(SgfAcceptor acceptor,SgfNode games) {
        Traverser traverser=new Traverser(acceptor);
        traverser.visit(games);
    }

    static void assertFinderMatches(SgfNode games) {
        if(games==null) return;
        SgfAcceptor acceptor=new SgfAcceptorImpl() {
            @Override public void accept(SgfNode target) {
                SgfNodeFinder finder=SgfNodeFinder.finder(target,games);
                finder.checkMove();
                assertTrue(finder.found!=null);
                assertEquals(target,finder.found);
            }
        };
        traverse(acceptor,games);
    }

    static java.util.Collection<Object[]> allSgfParameters() {
        return SgfTestParameters.allSgfKeysAndFiles();
    }

    static java.util.Collection<Object[]> multipleGameParameters() {
        return SgfTestParameters.multipleGameKeysAndFiles();
    }

    static java.util.Collection<Object[]> edgeParserParameters() {
        List<Object> objects=edgeParserObjects();
        Logging.mainLogger.info(String.valueOf(objects.iterator().next().getClass().getName()));
        java.util.Collection<Object[]> parameters=ParameterArray.parameterize(objects);
        for(Object[] parameterized:parameters) Logging.mainLogger.info(parameterized[0]+" "+parameterized[0].getClass());
        return parameters;
    }

    static java.util.Collection<Object[]> parserParameters() {
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(Parser.sgfDataKeySet());
        objects.addAll(Parser.sgfFiles());
        objects.addAll(edgeParserObjects());
        for(String sgf:Parser.illegalSgfKeys) objects.add(new RawSgf(sgf));
        return ParameterArray.parameterize(objects);
    }

    private static List<Object> edgeParserObjects() {
        String[] filenames=new String[] { //
                //"empty.sgf", //
                // "reallyempty.sgf", //
                //"saved.sgf", //
                //"mf0false.sgf", //
                //"mf1false.sgf", //
                "mf0.sgf", //
                "mf1.sgf", //
                "smart0.sgf", //
                "smart1.sgf", //
                "rtgo0.sgf", //
                "rtgo1.sgf", //
        };
        // use variable names above
        File[] files=SgfIo.filesInDir(Parser.sgfPath,filenames);
        List<Object> objects=new ArrayList<>();
        for(File file:files) objects.add(file);
        return objects;
    }

    static final class RawSgf {
        private final String sgf;

        RawSgf(String sgf) { this.sgf=sgf; }

        String sgf() { return sgf; }

        @Override public String toString() {
            if(sgf==null) return "raw:null";
            if(sgf.isEmpty()) return "raw:<empty>";
            return "raw:"+sgf;
        }
    }
}




