package model;
import equipment.Board;
import equipment.Board.Shape;
import equipment.Board.Topology;
import sgf.MNode;

/** Public facade for model tree/SGF persistence and root setup operations. */
public final class ModelTrees {
    private ModelTrees() {}

    // Root/sentinel operations remain here as model-tree facade
    public static boolean isSentinel(MNode root) {
        return ModelTreeOps.isSentinel(root);
    }

    public static void setRootFromParameters(Model model) {
        ModelTreeOps.setRootFromParameters(model);
    }

    public static void setRoot(Model model) {
        setRoot(model,Board.standard,Board.standard);
    }

    public static void setRoot(Model model,int width,int depth) {
        setRoot(model,width,depth,Topology.normal,Shape.normal);
    }

    public static void setRoot(Model model,int width,int depth,Topology topology,Shape shape) {
        ModelTreeOps.setRoot(model,width,depth,topology,shape);
    }
}
