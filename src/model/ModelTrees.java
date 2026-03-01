package model;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import com.tayek.util.io.Indent;
import equipment.Board;
import equipment.Board.Shape;
import equipment.Board.Topology;
import io.Logging;
import sgf.MNode;
import sgf.SgfNode;
/** Public facade for model tree/SGF persistence and root setup operations. */
public final class ModelTrees {
	
	public static String saveModel(Model model) {
		StringWriter writer=new StringWriter();
		ModelTrees.saveModel(model,writer);
		return writer.toString();
	}
	
	// Independent operations
	public static boolean saveModel(Model model,Writer writer) {
		return MNode.saveMNodes(writer,rootForSave(model),new Indent(SgfNode.options.indent));
	}
	public static void restoreModel(Model model,Reader reader) {
		MNode games=MNode.restoreMdodes(reader);
		Logging.mainLogger.info("restored root"+games);
		model.setRoot(games);
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
