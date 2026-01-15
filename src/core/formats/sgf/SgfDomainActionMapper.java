package core.formats.sgf;

import java.util.ArrayList;
import java.util.List;
import core.engine.DomainAction;
import equipment.Board;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
import sgf.MNode;
import sgf.P2;
import sgf.SgfProperty;

/**
 * Maps SGF properties/nodes to DomainActions and returns extras for round-trip.
 * Mapping is pure; callers explicitly decide how to apply extras.
 */
public final class SgfDomainActionMapper {
    private SgfDomainActionMapper() {}

    public static List<DomainAction> mapPropertyToDomainActions(SgfMappingContext context,SgfProperty property) {
        P2 p2=P2.which(property.p().id);
        if(p2==null) return List.of();
        return switch(p2) {
            case AB->mapSetupAdds(context,Stone.black,property.list());
            case AW->mapSetupAdds(context,Stone.white,property.list());
            case B->List.of(mapMoveOrPass(context,Stone.black,property.list().get(0)));
            case W->List.of(mapMoveOrPass(context,Stone.white,property.list().get(0)));
            case RG->mapEdges(context,property.list());
            case SZ->List.of(mapSize(property.list().get(0)));
            case C->mapComment(context,property.list().get(0));
            case ZB->List.of(new DomainAction.Resign(Stone.black));
            case ZW->List.of(new DomainAction.Resign(Stone.white));
            case RE->List.of(); // preserve as raw extras instead
            // All other properties become raw annotations (no actions)
            default->List.of();
        };
    }

    public static SgfNodeMapping mapNode(SgfMappingContext context,MNode node) {
        if(node==null) return SgfNodeMapping.empty();
        return mapProperties(context,node.sgfProperties());
    }

    public static SgfNodeMapping mapProperties(SgfMappingContext context,List<SgfProperty> properties) {
        List<DomainAction> actions=new ArrayList<>();
        List<SgfProperty> extras=new ArrayList<>();
        for(SgfProperty property:properties) {
            try {
                List<DomainAction> mapped=mapPropertyToDomainActions(context,property);
                if(mapped.isEmpty()) extras.add(property);
                else actions.addAll(mapped);
            } catch(RuntimeException e) { // malformed or unknown property -> preserve as extra
                extras.add(property);
            }
        }
        return new SgfNodeMapping(actions,extras);
    }

    private static List<DomainAction> mapSetupAdds(SgfMappingContext context,Stone color,List<String> coords) {
        List<DomainAction> out=new ArrayList<>(coords.size());
        for(String s:coords) out.add(new DomainAction.SetupAddStone(color,parseSgfPoint(context,s)));
        return out;
    }

    private static DomainAction mapMoveOrPass(SgfMappingContext context,Stone color,String coord) {
        if(coord==null||coord.isEmpty()) return new DomainAction.Pass(color);
        Point point=parseSgfPoint(context,coord);
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

    private static List<DomainAction> mapEdges(SgfMappingContext context,List<String> coords) {
        List<DomainAction> actions=new ArrayList<>(coords.size());
        for(String s:coords) actions.add(new DomainAction.SetupSetEdge(parseSgfPoint(context,s)));
        return actions;
    }

    private static List<DomainAction> mapComment(SgfMappingContext context,String comment) {
        if(comment==null) return List.of();
        List<DomainAction> actions=new ArrayList<>();
        if(context.boardTopologyPrefix()!=null && comment.startsWith(context.boardTopologyPrefix())) {
            String topo=comment.substring(context.boardTopologyPrefix().length());
            actions.add(new DomainAction.SetTopology(Board.Topology.valueOf(topo)));
        } else if(context.boardShapePrefix()!=null && comment.startsWith(context.boardShapePrefix())) {
            String shapeString=comment.substring(context.boardShapePrefix().length());
            actions.add(new DomainAction.SetShape(Board.Shape.valueOf(shapeString)));
        } else {
            return List.of(); // treat normal comments as raw extras
        }
        return actions;
    }

    private static Point parseSgfPoint(SgfMappingContext context,String coord) {
        int depth=context.depth()>0?context.depth():Board.standard;
        return Coordinates.fromSgfCoordinates(coord,depth);
    }
}
