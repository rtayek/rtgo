package model;
import io.TestIo;
import sgf.MNode;
final class MNodeTestIo {
    private MNodeTestIo() {}
    static String save(MNode node) {
        return save(node,"save fails");
    }
    static String save(MNode node,String message) {
        return TestIo.saveToString(message,writer->MNode.save(writer,node,null));
    }
}
