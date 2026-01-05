package sgf;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import io.Indent;
import io.IOs;
public final class SgfTestIo {
    private SgfTestIo() {}
    public static SgfNode restore(Reader reader) {
        return Parser.restoreSgf(reader);
    }
    public static SgfNode restore(String sgf) {
        return restore(sgf!=null?new StringReader(sgf):null);
    }
    public static MNode restoreMNode(String sgf) {
        return sgf!=null?MNode.restore(new StringReader(sgf)):null;
    }
    public static String mNodeRoundTrip(String sgf,SgfRoundTrip.MNodeSaveMode saveMode) {
        if(sgf==null) return null;
        StringWriter writer=new StringWriter();
        SgfRoundTrip.mNodeRoundTrip(new StringReader(sgf),writer,saveMode);
        return writer.toString();
    }
    public static String save(SgfNode node,Indent indent) {
        if(node==null) return null;
        StringWriter writer=new StringWriter();
        node.saveSgf(writer,indent);
        return writer.toString();
    }
    public static String save(SgfNode node) {
        return save(node,IOs.noIndent);
    }
    public static SgfNode saveAndRestore(SgfNode expected) {
        if(expected==null) return null;
        StringWriter writer=new StringWriter();
        return SgfRoundTrip.saveAndRestore(expected,writer);
    }
    public static boolean roundTripTwice(String sgf) {
        if(sgf==null) return true;
        return SgfRoundTrip.roundTripTwice(new StringReader(sgf));
    }
}
