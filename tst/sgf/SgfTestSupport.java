package sgf;

import java.io.IOException;
import java.io.Reader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.Logging;
import io.IOs;
import utilities.SgfTestParameters;
import utilities.ParameterArray;

public final class SgfTestSupport {
    private SgfTestSupport() {}

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

    static void assertModelRoundTripTwice(String sgf) {
        String expected=ModelTestIo.modelRoundTripToString(sgf);
        String actual=ModelTestIo.modelRoundTripToString(expected);
        org.junit.Assert.assertEquals(expected,actual);
    }

    static void assertModelRoundTripTwice(Reader reader) {
        String expected=ModelTestIo.modelRoundTripToString(reader);
        assertModelRoundTripTwice(expected);
        try {
            reader.close();
        } catch(IOException e) {
            Logging.mainLogger.severe("caught: "+e);
        }
    }

    static void assertSgfRestoreSaveStable(String sgf,Object key) {
        String[] actual=restoreAndSaveTwice(sgf);
        org.junit.Assert.assertEquals(key.toString(),actual[0],actual[1]);
    }

    static void assertSgfRestoreSaveStable(String sgf) {
        String[] actual=restoreAndSaveTwice(sgf);
        org.junit.Assert.assertEquals(actual[0],actual[1]);
    }

    public static boolean roundTripTwice(File file) {
        return SgfTestIo.roundTripTwice(IOs.toReader(file));
    }

    static boolean roundTripTwiceWithLogging(File file) {
        boolean ok=roundTripTwice(file);
        if(!ok) Logging.mainLogger.info(file+" fails!");
        return ok;
    }

    static String restoreAndSave(String sgf) {
        return SgfTestIo.restoreAndSave(sgf);
    }

    static SgfNode restoreFromKey(Object key) {
        String sgf=loadExpectedSgf(key);
        return SgfTestIo.restore(sgf);
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
        String actual=SgfTestIo.restoreAndSave(sgf);
        String actual2=SgfTestIo.restoreAndSave(actual);
        return new String[] {actual,actual2};
    }

    static void traverse(SgfAcceptor acceptor,SgfNode games) {
        Traverser traverser=new Traverser(acceptor);
        traverser.visit(games);
    }

    static java.util.Collection<Object[]> allSgfParameters() {
        return SgfTestParameters.allSgfKeysAndFiles();
    }

    static java.util.Collection<Object[]> multipleGameParameters() {
        return SgfTestParameters.multipleGameKeysAndFiles();
    }

    static java.util.Collection<Object[]> illegalSgfParameters() {
        return ParameterArray.parameterize(Parser.illegalSgfKeys);
    }

    static java.util.Collection<Object[]> edgeParserParameters() {
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
        Logging.mainLogger.info(String.valueOf(objects.iterator().next().getClass().getName()));
        objects.add("reallyEmpty");
        java.util.Collection<Object[]> parameters=ParameterArray.parameterize(objects);
        for(Object[] parameterized:parameters) Logging.mainLogger.info(parameterized[0]+" "+parameterized[0].getClass());
        return parameters;
    }

    static File[] filesInDir(String dir,String... filenames) {
        File[] files=new File[filenames.length];
        for(int i=0;i<filenames.length;i++) files[i]=new File(dir,filenames[i]);
        return files;
    }
}
