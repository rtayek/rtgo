package sgf;

import io.Indent;
import io.Logging;
import io.IOs;
import io.TestIo;
import static org.junit.Assert.*;
import static sgf.SgfNode.SgfOptions.containsQuotedControlCharacters;
import static utilities.Utilities.implies;
import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import model.Model;
import model.ModelHelper;
import model.ModelHelper.ModelSaveMode;
import model.ModelIo;
import model.Navigate;
import sgf.SgfNode.SgfOptions;
import utilities.ParameterArray;
import utilities.SgfTestParameters;

public final class SgfHarness {
    private SgfHarness() {}

    public record RoundTrip(String expected,String actual) {}

    @FunctionalInterface public interface ModelConsumer {
        void accept(Model model);
    }

    // Model IO helpers
    public static void restore(Model model,String sgf) {
        ModelIo.restore(model,sgf);
    }

    public static void restore(Model model,java.io.File file) {
        ModelIo.restore(model,file);
    }

    public static Model restoreNew(String sgf) {
        Model model=new Model();
        restore(model,sgf);
        return model;
    }

    public static String save(Model model) {
        return save(model,"save fails");
    }

    public static String save(Model model,String message) {
        return TestIo.saveToString(message,writer->model.save(writer));
    }

    public static String modelRoundTripToString(Reader reader) {
        return modelRoundTripToString(reader,ModelHelper.ModelSaveMode.sgfNode);
    }

    static String modelRoundTripToString(Reader reader,ModelHelper.ModelSaveMode saveMode) {
        if(reader==null) return null;
        return TestIo.writeToString(writer->ModelHelper.modelRoundTrip(reader,writer,saveMode));
    }

    public static String modelRoundTripToString(String sgf) {
        return modelRoundTripToString(sgf,ModelHelper.ModelSaveMode.sgfNode);
    }

    public static String modelRoundTripToString(String sgf,ModelHelper.ModelSaveMode saveMode) {
        if(sgf==null) return null;
        return modelRoundTripToString(IOs.toReader(sgf),saveMode);
    }

    public static String restoreAndSave(Model model,String sgf) {
        restore(model,sgf);
        return save(model);
    }

    public static String restoreAndSave(Model model,String sgf,String message) {
        restore(model,sgf);
        return save(model,message);
    }

    public static String restoreAndSave(Model model,String sgf,ModelConsumer afterRestore) {
        restore(model,sgf);
        if(afterRestore!=null) afterRestore.accept(model);
        return save(model);
    }

    static String restoreAndSave(Model model,String sgf,String message,ModelConsumer afterRestore) {
        restore(model,sgf);
        if(afterRestore!=null) afterRestore.accept(model);
        return save(model,message);
    }

    public static RoundTrip roundTrip(Model original,Model restored) {
        String expected=save(original);
        restore(restored,expected);
        String actual=save(restored);
        return new RoundTrip(expected,actual);
    }

    static RoundTrip roundTrip(Model original,Model restored,String expectedMessage,String actualMessage) {
        String expected=save(original,expectedMessage);
        restore(restored,expected);
        String actual=save(restored,actualMessage);
        return new RoundTrip(expected,actual);
    }

    // SGF IO helpers
    public static SgfNode restore(Reader reader) {
        return Parser.restoreSgf(reader);
    }

    public static SgfNode restore(String sgf) {
        return restore(sgf!=null?IOs.toReader(sgf):null);
    }

    public static MNode restoreMNode(String sgf) {
        return sgf!=null?MNode.restore(IOs.toReader(sgf)):null;
    }

    public static MNode quietLoadMNode(String sgf) {
        return sgf!=null?MNode.quietLoad(IOs.toReader(sgf)):null;
    }

    public static String mNodeRoundTrip(String sgf,SgfRoundTrip.MNodeSaveMode saveMode) {
        if(sgf==null) return null;
        return TestIo.writeToString(writer->SgfRoundTrip.mNodeRoundTrip(IOs.toReader(sgf),writer,saveMode));
    }

    public static String save(SgfNode node,Indent indent) {
        if(node==null) return null;
        return SgfRoundTrip.saveSgfToString(node,indent);
    }

    public static String save(SgfNode node) {
        return save(node,IOs.noIndent);
    }

    public static SgfNode saveAndRestore(SgfNode expected) {
        if(expected==null) return null;
        return SgfRoundTrip.saveAndRestore(expected);
    }

    public static String restoreAndSave(Reader reader) {
        if(reader==null) return null;
        return SgfRoundTrip.restoreAndSaveToString(reader);
    }

    public static boolean roundTripTwice(String sgf) {
        if(sgf==null) return true;
        return SgfRoundTrip.roundTripTwice(IOs.toReader(sgf));
    }

    public static boolean roundTripTwice(Reader reader) {
        if(reader==null) return true;
        return SgfRoundTrip.roundTripTwice(reader);
    }

    // Parser support
    static String loadExpectedSgf(Object key) {
        if(key==null) throw new RuntimeException("key: "+key+" is nul!");
        String sgf=Parser.getSgfData(key);
        if(sgf==null) return null;
        int p=Parser.parentheses(sgf);
        if(p!=0) {
            Logging.mainLogger.info(" bad parentheses: "+p);
            throw new RuntimeException(key+" bad parentheses: "+p);
        }
        return sgf;
    }

    static String prepareExpectedSgf(Object key,String sgf) {
        String normalized=sgf;
        if(normalized!=null) {
            normalized=SgfNode.options.prepareSgf(normalized);
            if(SgfNode.options.removeLineFeed) if(normalized.contains("\n)")) {
                Logging.mainLogger.info("lf badness");
                System.exit(0);
            }
            if(containsQuotedControlCharacters(key,normalized)) {
                Logging.mainLogger.info(key+" contains quoted control characters.");
                normalized=SgfOptions.removeQuotedControlCharacters(normalized);
            }
        }
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
        if(sgf==null) return;
        int p=Parser.parentheses(sgf);
        if(p!=0) Logging.mainLogger.info(key+" "+label+" bad parentheses: "+p);
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
        return SgfHarness.restore(expectedSgf);
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
        return sgf!=null?SgfNode.options.prepareSgf(sgf):null;
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
        SgfNode expected=SgfHarness.restore(expectedSgf);
        SgfNode actualSgf=SgfHarness.saveAndRestore(expected);
        if(expected!=null) assertTrue(key.toString(),expected.deepEquals(actualSgf));
    }

    static void assertSgfRoundTrip(Object key,String expectedSgf) {
        if(expectedSgf==null) return;
        String actualSgf=restoreAndSave(expectedSgf);
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
        boolean isOk=SgfHarness.roundTripTwice(expectedSgf);
        assertTrue(key.toString(),isOk);
    }

    static void assertSgfCannonical(Object key,String expectedSgf) {
        assertNoLineFeeds(expectedSgf);
        assertSgfRestoreSaveStable(expectedSgf,key);
    }

    static void assertMNodeRoundTrip(Object key,String expectedSgf,SgfRoundTrip.MNodeSaveMode saveMode,boolean logExpected) {
        if(logExpected) logBadParentheses(expectedSgf,key,"ex");
        String actualSgf=SgfHarness.mNodeRoundTrip(expectedSgf,saveMode);
        assertPreparedRoundTripWithParenthesesCheck(key,expectedSgf,actualSgf,"ac");
    }

    // Model round-trip support
    static void assertModelRestoreAndSave(Object key,String expectedSgf,boolean oldWay) {
        Model model=new Model("",oldWay);
        assertModelRestoreAndSave(key,expectedSgf,model);
    }

    static void assertModelRestoreAndSave(Object key,String expectedSgf,Model model) {
        String actualSgf=SgfHarness.restoreAndSave(model,expectedSgf);
        assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRoundTripToString(Object key,String expectedSgf,ModelSaveMode saveMode,boolean log) {
        String actualSgf=SgfHarness.modelRoundTripToString(expectedSgf,saveMode);
        if(log) {
            Logging.mainLogger.info("ex: "+expectedSgf);
            Logging.mainLogger.info("ac: "+actualSgf);
        }
        assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRoundTripTwice(String sgf) {
        String expected=SgfHarness.modelRoundTripToString(sgf);
        String actual=SgfHarness.modelRoundTripToString(expected);
        assertEquals(expected,actual);
    }

    static void assertCheckBoardInRoot(Object key,String sgf) {
        if(key==null) IOs.stackTrace(10);
        assertNotNull(key);
        checkBoardInRoot(key,sgf);
        Logging.mainLogger.info("after key: "+key);
    }

    static void assertSgfRestoreAndSave(Object key,String expectedSgf) {
        String actualSgf=restoreAndSave(expectedSgf);
        assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelRestoreAndSaveWithExplicitModel(Object key,String expectedSgf) {
        Model model=new Model();
        Logging.mainLogger.info("ex: "+expectedSgf);
        String actualSgf=SgfHarness.restoreAndSave(model,expectedSgf,key.toString());
        actualSgf=SgfNode.options.removeUnwanted(actualSgf);
        assertPreparedRoundTrip(key,expectedSgf,actualSgf);
    }

    static void assertModelSaveFromMNode(Object key,String expectedSgf,MNode root) {
        Model model=new Model();
        model.setRoot(root);
        String actualSgf=SgfHarness.save(model,key.toString());
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
        Model model=SgfHarness.restoreNew(sgf);
        String saved=model.save();
        return prepareActual(saved);
    }

    private static boolean checkBoardInRoot(Object key,String sgf) {
        // move this?
        if(key==null) { Logging.mainLogger.info("key is null!"); return true; }
        Model original=SgfHarness.restoreNew(sgf);
        boolean hasABoard=original.board()!=null;
        Model model=SgfHarness.restoreNew(sgf);
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

    public static boolean roundTripTwice(File file) {
        return SgfHarness.roundTripTwice(IOs.toReader(file));
    }

    static boolean roundTripTwiceWithLogging(File file) {
        boolean ok=roundTripTwice(file);
        if(!ok) Logging.mainLogger.info(file+" fails!");
        return ok;
    }

    public static String restoreAndSave(String sgf) {
        if(sgf==null) return null;
        return SgfRoundTrip.restoreAndSave(sgf);
    }

    static SgfNode restoreFromKey(Object key) {
        String sgf=loadExpectedSgf(key);
        return SgfHarness.restore(sgf);
    }

    static File firstExistingFile(File... files) {
        if(files==null) return null;
        for(File file:files) if(file!=null&&file.exists()) return file;
        return null;
    }

    static SgfProperty property(P id,String value) {
        return new SgfProperty(id,Arrays.asList(new String[] {value}));
    }

    static SgfNode nodeWithProperty(P id,String value) {
        SgfNode node=new SgfNode();
        node.add(property(id,value));
        return node;
    }

    private static String[] restoreAndSaveTwice(String sgf) {
        String actual=SgfHarness.restoreAndSave(sgf);
        String actual2=SgfHarness.restoreAndSave(actual);
        return new String[] {actual,actual2};
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
        File[] files=filesInDir(Parser.sgfPath,filenames);
        List<Object> objects=new ArrayList<>();
        for(File file:files) objects.add(file);
        return objects;
    }

    static File[] filesInDir(String dir,String... filenames) {
        File[] files=new File[filenames.length];
        for(int i=0;i<filenames.length;i++) files[i]=new File(dir,filenames[i]);
        return files;
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

