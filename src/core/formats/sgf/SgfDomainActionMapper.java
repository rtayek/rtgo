package core.formats.sgf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import core.engine.DomainAction;
import equipment.Board;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
import model.Model;
import sgf.MNode;
import sgf.P2;
import sgf.SgfProperty;

/**
 * Maps SGF properties/nodes to DomainActions and captures extras for round-trip.
 */
public final class SgfDomainActionMapper {
    private SgfDomainActionMapper() {}

    public static List<DomainAction> mapPropertyToDomainActions(Model model,SgfProperty property) {
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
            case RE->List.of(); // preserve as raw extras instead
            // All other properties become raw annotations (no actions)
            default->List.of();
        };
    }

    public static List<DomainAction> mapNodeToDomainActions(Model model,MNode node) {
        List<DomainAction> actions=new ArrayList<>();
        if(node==null) return actions;
        for(Iterator<SgfProperty> it=node.sgfProperties().iterator();it.hasNext();) {
            SgfProperty property=it.next();
            try {
                List<DomainAction> mapped=mapPropertyToDomainActions(model,property);
                if(mapped.isEmpty()) {
                    it.remove();
                    node.addExtraProperty(property);
                } else {
                    actions.addAll(mapped);
                }
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
            actions.add(new DomainAction.SetShape(Board.Shape.valueOf(shapeString)));
        } else {
            return List.of(); // treat normal comments as raw extras
        }
        return actions;
    }

    private static Point parseSgfPoint(Model model,String coord) {
        int depth=model.board()!=null?model.board().depth():model.depthFromSgf()>0?model.depthFromSgf():Board.standard;
        return Coordinates.fromSgfCoordinates(coord,depth);
    }
}
