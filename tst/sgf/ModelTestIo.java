package sgf;
import java.io.Reader;
import io.IOs;
import io.TestIo;
import model.Model;
import model.ModelHelper;
import model.ModelIo;
public final class ModelTestIo {
    public record RoundTrip(String expected,String actual) {}
    @FunctionalInterface public interface ModelConsumer {
        void accept(Model model);
    }
    private ModelTestIo() {}
    public static void restore(Model model,String sgf) {
        ModelIo.restore(model,sgf);
    }
    public static Model restoreNew(String sgf) {
        Model model=new Model();
        restore(model,sgf);
        return model;
    }
    public static String save(Model model) {
        return save(model,"save fails");
    }
    public static String save(Model model,String message) {
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
    public static String modelRoundTripToString(String sgf,ModelHelper.ModelSaveMode saveMode) {
        if(sgf==null) return null;
        return modelRoundTripToString(IOs.toReader(sgf),saveMode);
    }
    public static String restoreAndSave(Model model,String sgf) {
        restore(model,sgf);
        return save(model);
    }
    public static String restoreAndSave(Model model,String sgf,String message) {
        restore(model,sgf);
        return save(model,message);
    }
    public static String restoreAndSave(Model model,String sgf,ModelConsumer afterRestore) {
        restore(model,sgf);
        if(afterRestore!=null) afterRestore.accept(model);
        return save(model);
    }
    static String restoreAndSave(Model model,String sgf,String message,ModelConsumer afterRestore) {
        restore(model,sgf);
        if(afterRestore!=null) afterRestore.accept(model);
        return save(model,message);
    }
    public static RoundTrip roundTrip(Model original,Model restored) {
        String expected=save(original);
        restore(restored,expected);
        String actual=save(restored);
        return new RoundTrip(expected,actual);
    }
    static RoundTrip roundTrip(Model original,Model restored,String expectedMessage,String actualMessage) {
        String expected=save(original,expectedMessage);
        restore(restored,expected);
        String actual=save(restored,actualMessage);
        return new RoundTrip(expected,actual);
    }
}
