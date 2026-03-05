package model;

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import equipment.Board;
import equipment.Coordinates;
import equipment.Point;
import equipment.Board.Shape;
import equipment.Board.Topology;
import io.Logging;
import sgf.MNode;
import sgf.P;
import sgf.SgfNode;
import sgf.SgfProperty;
import com.tayek.util.io.Indent;

public final class ModelTreeOps {
    public static boolean isSentinel(MNode root) {
        // RT is a sentinel extra-root marker; preserve for lossless round-trip.
        return MNode.hasProperty(root,P.RT);
    }

    static void addProperty(MNode node,P p,String string) {
        addProperty(node,p,new String[] {string});
    }

    static void addProperty(MNode node,P p,String[] strings) {
        List<String> list=new ArrayList<>(Arrays.asList(strings));
        addProperty(node,p,list);
    }

    static void addProperty(MNode node,P p,List<String> strings) {
        SgfProperty property=new SgfProperty(p,strings);
        node.sgfProperties().add(property);
    }

    public static void setRootFromParameters(Model model) {
        Logging.mainLogger.info("set root from parameters");
        int width=(int)Parameters.width.currentValue();
        int depth=(int)Parameters.depth.currentValue();
        Topology topology=(Topology)Parameters.topology.currentValue();
        Shape shape=(Shape)Parameters.shape.currentValue();
        setRoot(model,width,depth,topology,shape);
    }

    static void setRoot(Model model,int width,int depth) {
        setRoot(model,width,depth,Topology.normal,Shape.normal);
    }

    public static void setRoot(Model model,int width,int depth,Topology topology,Shape shape) {
        Logging.mainLogger.config("setRoot: "+model.name+" board type is: "+topology+", shape is: "+shape);
        MNode main=new MNode(null); // no extra root
        addProperty(main,P.FF,"4");
        addProperty(main,P.GM,"1");
        addProperty(main,P.AP,Model.sgfApplicationName);
        addProperty(main,P.C,"comment");
        if(!topology.equals(Topology.normal)) addProperty(main,P.C,Model.sgfBoardTopology+topology);
        if(!shape.equals(Shape.normal)) addProperty(main,P.C,Model.sgfBoardShape+shape);
        String sizeString=Integer.valueOf(width).toString()+":"+Integer.valueOf(depth).toString();
        if(width==depth) sizeString=Integer.valueOf(width).toString();
        boolean alwaysSetBoardSize=true; // required so new-game width/depth is preserved through root rebuild.
        if(alwaysSetBoardSize) addProperty(main,P.SZ,sizeString);
        if(topology.equals(Topology.torus)) addProperty(main,P.KM,"4.5");
        model.setStoredBoardSizeFromSgf(width,depth);
        addRegion(width,depth,topology,shape,main);
        Logging.mainLogger.fine(model.name+" new root is: "+main);
        model.setRoot(main);
        Logging.mainLogger.config("exit setRoot: "+model.name+" board type is: "+topology+", shape is: "+shape);
    }

    private static void addSgfRegion(int depth,MNode newRoot,List<Point> points) {
        if(points.size()>0) {
            List<String> strings=new ArrayList<>();
            for(Point point:points) {
                String string=Coordinates.toSgfCoordinates(point,depth);
                strings.add(string);
            }
            Logging.mainLogger.warning("region string: "+strings);
            addProperty(newRoot,P.RG,strings);
        }
    }

    private static void addRegion(int width,int depth,Topology topology,Shape shape,MNode newRoot) {
        List<Point> points;
        if(topology==Topology.diamond) points=Board.getPointsForDiamondRegion(width,depth);
        else points=Shape.getPointsForRegion(width,depth,shape);
        addSgfRegion(depth,newRoot,points);
    }

    private ModelTreeOps() {}
}
