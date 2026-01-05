package sgf;
import io.Logging;
import static io.IOs.noIndent;
import static io.IOs.toReader;
import static sgf.Parser.parentheses;
import static sgf.Parser.restoreSgf;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
import io.Indent;
import static io.Logging.parserLogger;
/**
 * Shared round-trip helpers for SGF and MNode flows.
 */
public final class SgfRoundTrip {
    public enum MNodeSaveMode {
        standard,direct
    }
    private SgfRoundTrip() {}
    public static String saveSgfToString(SgfNode node,Indent indent) {
        if(node==null) return null;
        StringWriter writer=new StringWriter();
        node.saveSgf(writer,indent);
        return writer.toString();
    }
    public static String restoreAndSaveToString(Reader reader) {
        if(reader==null) return null;
        StringWriter stringWriter=new StringWriter();
        restoreAndSave(reader,stringWriter);
        return stringWriter.toString();
    }
    public static SgfNode restoreAndSave(Reader reader,Writer writer) {
        if(reader==null) return null;
        SgfNode games=restoreSgf(reader);
        if(games!=null) games.saveSgf(writer,noIndent);
        String actual=writer.toString();
        int p=parentheses(actual);
        if(p!=0) Logging.mainLogger.info("actual parentheses count: "+p);
        //if(p!=0) throw new RuntimeException("actual parentheses count: "+p);
        return games;
    }
    public static String restoreAndSave(String expectedSgf) {
        if(expectedSgf==null) return null;
        return restoreAndSaveToString(toReader(expectedSgf));
    }
    public static SgfNode saveAndRestore(SgfNode expected,StringWriter stringWriter) {
        SgfNode actualSgf=null;
        if(expected!=null) {
            String sgf=saveSgfToString(expected,noIndent);
            stringWriter.append(sgf);
            actualSgf=restoreSgf(toReader(sgf));
        }
        return actualSgf;
    }
    public static SgfNode saveAndRestore(SgfNode expected) {
        return saveAndRestore(expected,new StringWriter());
    }
    public static boolean roundTripTwice(Reader original) {
        Writer writer=new StringWriter();
        restoreAndSave(original,writer);
        String expected=writer.toString(); // cannonical form?
        writer=new StringWriter();
        SgfNode games=restoreSgf(toReader(expected));
        if(games!=null) games.saveSgf(writer,noIndent);
        // allow null for now (11/8/22).
        String actual=writer.toString();
        if(!actual.equals(expected)) {
            parserLogger.severe(actual+"!="+original);
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
}
