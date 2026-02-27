package sgf;

import com.tayek.util.io.FileIO;
import io.Logging;
import io.TestIo;
import java.io.File;
import java.io.Reader;
import java.util.function.Consumer;
import model.Model;
import model.ModelHelper;
import model.ModelTrees;

/**
 * Test-side SGF/model IO helpers used by parser and round-trip tests.
 */
public final class SgfTestIo {
    private SgfTestIo() {}

    public record RoundTrip(String expected,String actual) {}

    public static String save(Model model,String message) {
        return TestIo.saveToString(message,writer->ModelTrees.save(model,writer));
    }

    public static String modelRoundTripToString(Reader reader,ModelHelper.ModelSaveMode saveMode) {
        return ModelTrees.modelRoundTripToString(reader,saveMode);
    }

    public static String modelRoundTripToString(Reader reader) {
        return modelRoundTripToString(reader,ModelHelper.ModelSaveMode.sgfNode);
    }

    public static String modelRoundTripToString(String sgf,ModelHelper.ModelSaveMode saveMode) {
        if(sgf==null) return null;
        return ModelTrees.modelRoundTripToString(FileIO.toReader(sgf),saveMode);
    }

    public static String modelRoundTripToString(String sgf) {
        return modelRoundTripToString(sgf,ModelHelper.ModelSaveMode.sgfNode);
    }

    public static String restoreAndSave(Model model,String sgf,String message) {
        ModelTrees.restore(model,sgf);
        return save(model,message);
    }

    public static String restoreAndSave(Model model,String sgf,Consumer<Model> afterRestore) {
        ModelTrees.restore(model,sgf);
        if(afterRestore!=null) afterRestore.accept(model);
        return ModelTrees.save(model);
    }

    static String restoreAndSave(Model model,String sgf,String message,Consumer<Model> afterRestore) {
        ModelTrees.restore(model,sgf);
        if(afterRestore!=null) afterRestore.accept(model);
        return save(model,message);
    }

    public static RoundTrip roundTrip(Model original,Model restored) {
        String expected=ModelTrees.save(original);
        ModelTrees.restore(restored,expected);
        String actual=ModelTrees.save(restored);
        return new RoundTrip(expected,actual);
    }

    static RoundTrip roundTrip(Model original,Model restored,String expectedMessage,String actualMessage) {
        String expected=save(original,expectedMessage);
        ModelTrees.restore(restored,expected);
        String actual=save(restored,actualMessage);
        return new RoundTrip(expected,actual);
    }

    public static MNode quietLoadMNode(String sgf) {
        return sgf!=null?SgfIo.quietLoadMNode(FileIO.toReader(sgf)):null;
    }

    public static String mNodeRoundTrip(String sgf,SgfIo.MNodeSaveMode saveMode) {
        return sgf!=null?SgfIo.mNodeRoundTrip(FileIO.toReader(sgf),saveMode):null;
    }

    public static boolean roundTripTwice(String sgf) {
        return sgf==null||SgfIo.roundTripTwice(FileIO.toReader(sgf));
    }

    public static boolean roundTripTwice(Reader reader) {
        return reader==null||SgfIo.roundTripTwice(reader);
    }

    public static boolean roundTripTwice(File file) {
        return file==null||SgfIo.roundTripTwice(FileIO.toReader(file));
    }

    static boolean roundTripTwiceWithLogging(File file) {
        boolean ok=roundTripTwice(file);
        if(!ok) Logging.mainLogger.info(file+" fails!");
        return ok;
    }

    static SgfNode restoreFromKey(Object key) {
        String sgf=SgfIo.loadExpectedSgf(key);
        return SgfIo.restore(sgf);
    }

    static File firstExistingFile(File... files) {
        return SgfIo.firstExistingFile(files);
    }

    static SgfProperty property(P id,String value) {
        return SgfIo.property(id,value);
    }

    static SgfNode nodeWithProperty(P id,String value) {
        return SgfIo.nodeWithProperty(id,value);
    }

    static File[] filesInDir(String dir,String... filenames) {
        return SgfIo.filesInDir(dir,filenames);
    }
}
