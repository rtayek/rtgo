package model;
import java.io.File;
import io.IOs;
public final class ModelIo {
    private ModelIo() {}
    public static void restore(Model model,File file) {
        if(model==null||file==null) return;
        model.restore(IOs.toReader(file));
    }
    public static void restore(Model model,String sgf) {
        if(model==null) return;
        model.restore(IOs.toReader(sgf));
    }
    public static boolean save(Model model,File file) {
        if(model==null||file==null) return false;
        return model.save(IOs.toWriter(file));
    }
}
