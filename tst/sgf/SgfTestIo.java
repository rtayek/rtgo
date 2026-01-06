package sgf;
import java.io.Reader;
import io.Indent;
import io.IOs;
import io.TestIo;
public final class SgfTestIo {
    private SgfTestIo() {}
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
    public static String restoreAndSave(String sgf) {
        if(sgf==null) return null;
        return SgfRoundTrip.restoreAndSave(sgf);
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
}
