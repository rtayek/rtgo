package model;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import equipment.*;
import equipment.Coordinates;
import equipment.Board.Shape;
import sgf.P2;
import sgf.MNode;
import sgf.SgfProperty;
import model.Model;
public sealed interface DomainAction permits DomainAction.SetBoardSpec, DomainAction.SetTopology, DomainAction.SetShape,
        DomainAction.SetupAddStone, DomainAction.SetupSetEdge, DomainAction.Move, DomainAction.Pass,
        DomainAction.Resign, DomainAction.RecordResult, DomainAction.Metadata {
    record SetBoardSpec(int width,int depth) implements DomainAction {} // creates board using current state.topology/state.shape
    record SetTopology(Board.Topology topology) implements DomainAction {}
    record SetShape(Shape shape) implements DomainAction {}
    record SetupAddStone(Stone color,Point point) implements DomainAction {}
    record SetupSetEdge(Point point) implements DomainAction {}
    record Move(Stone color,Point point) implements DomainAction {}
    record Pass(Stone color) implements DomainAction {}
    record Resign(Stone color) implements DomainAction {}
    // optional: keep RE but do not apply to core game end
    record RecordResult(String result) implements DomainAction {}
    // optional: FF/GM/HA/KM/AP etc as “extras” (no-op for core)
    record Metadata(P2 p2,List<String> values) implements DomainAction {}
    default void apply(Model model) { applyTo(this,model); }
    static void applyTo(DomainAction action,Model model) {
        switch(action) {
            case DomainAction.SetBoardSpec a-> {
                model.setRoot(a.width(),a.depth(),model.boardTopology(),model.boardShape());
                model.setBoard(Board.factory.create(a.width(),a.depth(),model.boardTopology(),model.boardShape()));
            }
            case DomainAction.SetTopology a-> {
                model.setBoardTopology(a.topology());
                if(model.board()!=null) model.setBoard(Board.factory.create(model.board().width(),model.board().depth(),
                        model.boardTopology(),model.boardShape()));
            }
            case DomainAction.SetShape a-> {
                model.setBoardShape(a.shape());
                if(model.board()!=null) model.setBoard(Board.factory.create(model.board().width(),model.board().depth(),
                        model.boardTopology(),model.boardShape()));
            }
            case DomainAction.SetupAddStone a-> {
                model.ensureBoard();
                model.board().setAt(a.point(),a.color());
            }
            case DomainAction.SetupSetEdge a-> {
                model.ensureBoard();
                model.board().setAt(a.point(),Stone.edge);
            }
            case DomainAction.Move a-> {
                model.ensureBoard();
                if(a.point()==null) model.sgfPassAction();
                else model.sgfMakeMove(a.color(),a.point());
            }
            case DomainAction.Pass a->model.sgfPassAction();
            case DomainAction.Resign a->model.sgfResignAction();
            case DomainAction.RecordResult a-> {
                /* metadata only for now */ }
            case DomainAction.Metadata a-> {
                /* no-op */ }
            default->throw new IllegalArgumentException("Unexpected value: "+action);
        }
    }
    static List<DomainAction> mapPropertyToDomainActions(Model model,SgfProperty property) {
        P2 p2=P2.which(property.p().id);
        if(p2==null) return List.of();
        return switch(p2) {
            case AB->mapSetupAdds(model,Stone.black,property.list());
            case AW->mapSetupAdds(model,Stone.white,property.list());
            case B->List.of(mapMoveOrPass(model,Stone.black,property.list().get(0)));
            case W->List.of(mapMoveOrPass(model,Stone.white,property.list().get(0)));
            case RG->mapEdges(model,property.list());
            case SZ->List.of(mapSize(property.list().get(0)));
            case C->mapComment(property.list().get(0));
            case ZB->List.of(new DomainAction.Resign(Stone.black));
            case ZW->List.of(new DomainAction.Resign(Stone.white));
            case RE->List.of(new DomainAction.RecordResult(property.list().get(0)));
            // Keep metadata as metadata for now; apply() can mutate state like today.
            case AP,FF,GM,HA,KM,BL,WL,RT->List.of(new DomainAction.Metadata(p2,property.list()));
            default->List.of(new DomainAction.Metadata(p2,property.list()));
        };
    }
    static List<DomainAction> mapNodeToDomainActions(Model model,MNode node) {
        List<DomainAction> actions=new ArrayList<>();
        if(node==null) return actions;
        for(Iterator<SgfProperty> it=node.sgfProperties().iterator();it.hasNext();) {
            SgfProperty property=it.next();
            try {
                actions.addAll(mapPropertyToDomainActions(model,property));
            } catch(IllegalArgumentException e) { // unknown property id -> preserve as extra
                it.remove();
                node.addExtraProperty(property);
            }
        }
        return actions;
    }
    private static List<DomainAction> mapSetupAdds(Model model,Stone color,List<String> coords) {
        List<DomainAction> out=new ArrayList<>(coords.size());
        for(String s:coords) out.add(new DomainAction.SetupAddStone(color,parseSgfPoint(model,s)));
        return out;
    }
    private static DomainAction mapMoveOrPass(Model model,Stone color,String coord) {
        if(coord==null||coord.isEmpty()) return new DomainAction.Pass(color);
        Point point=parseSgfPoint(model,coord);
        return new DomainAction.Move(color,point);
    }
    private static DomainAction mapSize(String sz) {
        if(sz==null||sz.isEmpty()) return new DomainAction.SetBoardSpec(Board.standard,Board.standard);
        if(sz.contains(":")) {
            String[] parts=sz.split(":");
            int w=Integer.parseInt(parts[0]);
            int d=Integer.parseInt(parts[1]);
            return new DomainAction.SetBoardSpec(w,d);
        }
        int n=Integer.parseInt(sz);
        return new DomainAction.SetBoardSpec(n,n);
    }
    private static List<DomainAction> mapEdges(Model model,List<String> coords) {
        List<DomainAction> actions=new ArrayList<>(coords.size());
        for(String s:coords) actions.add(new DomainAction.SetupSetEdge(parseSgfPoint(model,s)));
        return actions;
    }
    private static List<DomainAction> mapComment(String comment) {
        if(comment==null) return List.of();
        List<DomainAction> actions=new ArrayList<>();
        if(comment.startsWith(Model.sgfBoardTopology)) {
            String topo=comment.substring(Model.sgfBoardTopology.length());
            actions.add(new DomainAction.SetTopology(Board.Topology.valueOf(topo)));
        } else if(comment.startsWith(Model.sgfBoardShape)) {
            String shapeString=comment.substring(Model.sgfBoardShape.length());
            actions.add(new DomainAction.SetShape(Shape.valueOf(shapeString)));
        }
        actions.add(new DomainAction.Metadata(P2.C,List.of(comment)));
        return actions;
    }
    private static Point parseSgfPoint(Model model,String coord) {
        int depth=model.board()!=null?model.board().depth():model.depthFromSgf()>0?model.depthFromSgf():Board.standard;
        return Coordinates.fromSgfCoordinates(coord,depth);
    }
}
