package model;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import com.tayek.util.io.FileIO;
import com.tayek.util.io.Indent;
import equipment.Board;
import equipment.Board.Shape;
import equipment.Board.Topology;
import io.Logging;
import sgf.MNode;
import sgf.SgfNode;
import model.ModelHelper.ModelSaveMode;

/**
 * Public facade for model tree/SGF persistence and root setup operations.
 */
public final class ModelTrees {
    public static boolean save(Model model,Writer writer) {
        return MNode.save(writer,rootForSave(model),new Indent(SgfNode.options.indent));
    }

    public static String save(Model model) {
        StringWriter stringWriter=new StringWriter();
		MNode.save(stringWriter,rootForSave(model),new Indent(SgfNode.options.indent));
		return stringWriter.toString();
    }

    public static void restore(Model model,Reader reader) {
		MNode games=MNode.restore(reader);
		Logging.mainLogger.info("restored root"+games);
		model.setRoot(games);
    }

    public static void restore(Model model,String sgf) {
        if(model==null||sgf==null) return;
        restore(model,FileIO.toReader(sgf));
    }

    public static void restore(Model model,File file) {
        if(model==null||file==null) return;
        restore(model,FileIO.toReader(file));
    }

    public static String modelRoundTripToString(Reader reader,ModelSaveMode saveMode) {
        StringWriter writer=new StringWriter();
        ModelHelper.modelRoundTrip(reader,writer,saveMode);
        return writer.toString();
    }

    public static Model restoreNew(Reader reader) {
        Model model=new Model();
        restore(model,reader);
        return model;
    }

    public static Model restoreNew(String sgf) {
        if(sgf==null) return new Model();
        return restoreNew(FileIO.toReader(sgf));
    }

    public static String restoreAndSave(Model model,Reader reader) {
        restore(model,reader);
        return save(model);
    }

    public static String restoreAndSave(Model model,String sgf) {
        restore(model,sgf);
        return model!=null?save(model):null;
    }

    public static boolean isSentinel(MNode root) {
        return ModelTreeOps.isSentinel(root);
    }

    public static void setRootFromParameters(Model model) {
        ModelTreeOps.setRootFromParameters(model);
    }

    public static void setRoot(Model model) {
        ModelTreeOps.setRoot(model,Board.standard,Board.standard);
    }

    public static void setRoot(Model model,int width,int depth) {
        ModelTreeOps.setRoot(model,width,depth);
    }

    public static void setRoot(Model model,int width,int depth,Topology topology,Shape shape) {
        ModelTreeOps.setRoot(model,width,depth,topology,shape);
    }

    private static MNode rootForSave(Model model) {
        MNode root=model.root();
        if(ModelTreeOps.isSentinel(root)) {
            Logging.mainLogger.info("root has RT");
            return root;
        }
        MNode wrapped=new MNode(null);
        wrapped.children().add(root);
        Logging.mainLogger.info("added null root");
        return wrapped;
    }

    private ModelTrees() {}
}
