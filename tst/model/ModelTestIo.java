package model;
import static org.junit.Assert.assertTrue;
import java.io.Reader;
import java.io.StringWriter;
import io.IOs;
public final class ModelTestIo {
    private ModelTestIo() {}
    public static void restore(Model model,String sgf) {
        model.restore(IOs.toReader(sgf));
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
    public static String modelRoundTripToString(Reader reader) {
        return modelRoundTripToString(reader,ModelHelper.ModelSaveMode.sgfNode);
    }
    static String modelRoundTripToString(Reader reader,ModelHelper.ModelSaveMode saveMode) {
        if(reader==null) return null;
        StringWriter stringWriter=new StringWriter();
        ModelHelper.modelRoundTrip(reader,stringWriter,saveMode);
        return stringWriter.toString();
    }
    public static String modelRoundTripToString(String sgf) {
        return modelRoundTripToString(sgf,ModelHelper.ModelSaveMode.sgfNode);
    }
    static String modelRoundTripToString(String sgf,ModelHelper.ModelSaveMode saveMode) {
        if(sgf==null) return null;
        return modelRoundTripToString(IOs.toReader(sgf),saveMode);
    }
}
