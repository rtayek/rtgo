package model;
import static org.junit.Assert.assertTrue;
import java.io.StringWriter;
import sgf.MNode;
final class MNodeTestIo {
    private MNodeTestIo() {}
    static String save(MNode node) {
        return save(node,"save fails");
    }
    static String save(MNode node,String message) {
        StringWriter stringWriter=new StringWriter();
        boolean ok=MNode.save(stringWriter,node,null);
        assertTrue(message,ok);
        return stringWriter.toString();
    }
}
