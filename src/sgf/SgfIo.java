package sgf;

import java.io.Reader;
import java.io.StringWriter;
import com.tayek.util.io.Indent;

/**
 * Reusable SGF/MNode I/O helpers shared by source and tests.
 */
public final class SgfIo {
    private SgfIo() {}

    public static SgfNode restore(Reader reader) {
        return Parser.restoreSgf(reader);
    }

    public static MNode restoreMNode(Reader reader) {
        return MNode.restore(reader);
    }

    public static MNode quietLoadMNode(Reader reader) {
        return MNode.quietLoad(reader);
    }

    public static String mNodeRoundTrip(Reader reader,SgfRoundTrip.MNodeSaveMode saveMode) {
        StringWriter writer=new StringWriter();
        SgfRoundTrip.mNodeRoundTrip(reader,writer,saveMode);
        return writer.toString();
    }

    public static String save(SgfNode node,Indent indent) {
        return SgfRoundTrip.saveSgfToString(node,indent);
    }

    public static SgfNode saveAndRestore(SgfNode expected) {
        return SgfRoundTrip.saveAndRestore(expected);
    }

    public static String restoreAndSave(Reader reader) {
        return SgfRoundTrip.restoreAndSaveToString(reader);
    }

    public static boolean roundTripTwice(Reader reader) {
        return SgfRoundTrip.roundTripTwice(reader);
    }
}
