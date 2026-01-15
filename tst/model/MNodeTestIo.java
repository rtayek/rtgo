package model;
import io.TestIo;
import sgf.MNode;
public final class MNodeTestIo {
    private MNodeTestIo() {}
    public static String save(MNode node) {
        return save(node,"save fails");
    }
    static String save(MNode node,String message) {
        return TestIo.saveToString(message,writer->MNode.save(writer,node,null));
    }
}
