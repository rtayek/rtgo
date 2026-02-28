package sgf;

import static io.IOs.noIndent;
import static io.Logging.parserLogger;
import static sgf.Parser.parentheses;
import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import com.tayek.util.io.FileIO;
import com.tayek.util.io.Indent;
import io.Logging;

/**
 * Reusable SGF/MNode I/O helpers shared by source and tests.
 */
public final class SgfIo {
    public enum MNodeSaveMode {
        standard,direct
    }
    private SgfIo() {}

    // Independent operations
    public static SgfNode restore(Reader reader) {
        if(reader==null) return null;
        return Parser.restoreSgf(reader);
    }

    public static MNode restoreMNode(Reader reader) {
        return MNode.restore(reader);
    }

    public static MNode quietLoadMNode(Reader reader) {
        return MNode.quietLoad(reader);
    }

    public static String saveSgfToString(SgfNode node,Indent indent) {
        if(node==null) return null;
        StringWriter writer=new StringWriter();
        node.saveSgf(writer,indent);
        return writer.toString();
    }

    public static SgfNode restoreAndSave(Reader reader,Writer writer) {
        if(reader==null) return null;
        SgfNode games=restore(reader);
        if(games!=null) games.saveSgf(writer,noIndent);
        String actual=writer.toString();
        int p=parentheses(actual);
        if(p!=0) Logging.mainLogger.info("actual parentheses count: "+p);
        return games;
    }

    public static boolean roundTripTwice(Reader reader) {
        Writer writer=new StringWriter();
        restoreAndSave(reader,writer);
        String expected=writer.toString();
        writer=new StringWriter();
        SgfNode games=restore(FileIO.toReader(expected));
        if(games!=null) games.saveSgf(writer,noIndent);
        String actual=writer.toString();
        if(!actual.equals(expected)) {
            parserLogger.severe(actual+"!="+reader);
            return false;
        }
        return true;
    }

    public static MNode mNodeRoundTrip(Reader reader,Writer writer,MNodeSaveMode saveMode) {
        MNode root=MNode.restore(reader);
        if(saveMode==MNodeSaveMode.direct) {
            String actual=MNode.saveDirectly(root);
            try {
                writer.write(actual);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            boolean ok=MNode.save(writer,root,noIndent);
            if(!ok) Logging.mainLogger.info("not ok!");
        }
        return root;
    }

    public static String loadExpectedSgf(Object key) {
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

    public static String prepareExpectedSgf(Object key,String sgf) {
        String normalized=sgf;
        if(normalized!=null) {
            normalized=SgfNode.options.prepareSgf(normalized);
            if(SgfNode.options.removeLineFeed) if(normalized.contains("\n)")) {
                Logging.mainLogger.info("lf badness");
                System.exit(0);
            }
            if(SgfNode.SgfOptions.containsQuotedControlCharacters(key,normalized)) {
                Logging.mainLogger.info(key+" contains quoted control characters.");
                normalized=SgfNode.SgfOptions.removeQuotedControlCharacters(normalized);
            }
        }
        return normalized;
    }

    public static String prepareSgf(String sgf) {
        return sgf!=null?SgfNode.options.prepareSgf(sgf):null;
    }

    public static void logBadParentheses(String sgf,Object key,String label) {
        if(sgf==null) return;
        int p=Parser.parentheses(sgf);
        if(p!=0) Logging.mainLogger.info(key+" "+label+" bad parentheses: "+p);
    }

    public static File firstExistingFile(File... files) {
        if(files==null) return null;
        for(File file:files) if(file!=null&&file.exists()) return file;
        return null;
    }

    public static SgfProperty property(P id,String value) {
        return new SgfProperty(id,Arrays.asList(new String[] {value}));
    }

    public static SgfNode nodeWithProperty(P id,String value) {
        SgfNode node=new SgfNode();
        node.add(property(id,value));
        return node;
    }

    public static File[] filesInDir(String dir,String... filenames) {
        File[] files=new File[filenames.length];
        for(int i=0;i<filenames.length;i++) files[i]=new File(dir,filenames[i]);
        return files;
    }
}
