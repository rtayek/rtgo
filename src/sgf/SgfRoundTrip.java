package sgf;
import static io.IOs.noIndent;
import static sgf.Parser.parentheses;
import static sgf.Parser.restoreSgf;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import static io.Logging.parserLogger;
/**
 * Shared round-trip helpers for SGF and MNode flows.
 */
public final class SgfRoundTrip {
    public enum MNodeSaveMode {
        standard,direct
    }
    private SgfRoundTrip() {}
    public static SgfNode restoreAndSave(Reader reader,Writer writer) {
        if(reader==null) return null;
        SgfNode games=restoreSgf(reader);
        if(games!=null) games.saveSgf(writer,noIndent);
        String actual=writer.toString();
        int p=parentheses(actual);
        if(p!=0) System.out.println("actual parentheses count: "+p);
        //if(p!=0) throw new RuntimeException("actual parentheses count: "+p);
        return games;
    }
    public static String restoreAndSave(String expectedSgf) {
        if(expectedSgf==null) return null;
        StringWriter stringWriter=new StringWriter();
        StringReader stringReader=new StringReader(expectedSgf);
        restoreAndSave(stringReader,stringWriter);
        return stringWriter.toString();
    }
    public static SgfNode saveAndRestore(SgfNode expected,StringWriter stringWriter) {
        SgfNode actualSgf=null;
        if(expected!=null) {
            expected.saveSgf(stringWriter,noIndent);
            String sgf=stringWriter.toString();
            actualSgf=restoreSgf(new StringReader(sgf));
        }
        return actualSgf;
    }
    public static boolean roundTripTwice(Reader original) {
        Writer writer=new StringWriter();
        restoreAndSave(original,writer);
        String expected=writer.toString(); // cannonical form?
        writer=new StringWriter();
        SgfNode games=restoreSgf(new StringReader(expected));
        if(games!=null) games.saveSgf(writer,noIndent);
        // allow null for now (11/8/22).
        String actual=writer.toString();
        if(!actual.equals(expected)) {
            parserLogger.severe(actual+"!="+original);
            return false;
        }
        return true;
    }
    public static MNode mNodeRoundTrip(StringReader stringReader,StringWriter stringWriter,MNodeSaveMode saveMode) {
        MNode root=MNode.restore(stringReader);
        if(saveMode==MNodeSaveMode.direct) {
            String actual=MNode.saveDirectly(root);
            stringWriter.write(actual);
        } else {
            boolean ok=MNode.save(stringWriter,root,noIndent);
            if(!ok) System.out.println("not ok!");
        }
        return root;
    }
}
