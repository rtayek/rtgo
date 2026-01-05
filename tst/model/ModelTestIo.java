package model;
import java.io.Reader;
import io.IOs;
import io.TestIo;
public final class ModelTestIo {
    private ModelTestIo() {}
    public static void restore(Model model,String sgf) {
        model.restore(IOs.toReader(sgf));
    }
    static String save(Model model) {
        return save(model,"save fails");
    }
    static String save(Model model,String message) {
        return TestIo.saveToString(message,writer->model.save(writer));
    }
    public static String modelRoundTripToString(Reader reader) {
        return modelRoundTripToString(reader,ModelHelper.ModelSaveMode.sgfNode);
    }
    static String modelRoundTripToString(Reader reader,ModelHelper.ModelSaveMode saveMode) {
        if(reader==null) return null;
        return TestIo.writeToString(writer->ModelHelper.modelRoundTrip(reader,writer,saveMode));
    }
    public static String modelRoundTripToString(String sgf) {
        return modelRoundTripToString(sgf,ModelHelper.ModelSaveMode.sgfNode);
    }
    static String modelRoundTripToString(String sgf,ModelHelper.ModelSaveMode saveMode) {
        if(sgf==null) return null;
        return modelRoundTripToString(IOs.toReader(sgf),saveMode);
    }
}
