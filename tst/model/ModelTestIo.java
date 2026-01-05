package model;
import static org.junit.Assert.assertTrue;
import java.io.StringReader;
import java.io.StringWriter;
public final class ModelTestIo {
    private ModelTestIo() {}
    public static void restore(Model model,String sgf) {
        model.restore(new StringReader(sgf));
    }
    static String save(Model model) {
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue("save fails",ok);
        return stringWriter.toString();
    }
    static String save(Model model,String message) {
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(message,ok);
        return stringWriter.toString();
    }
}
