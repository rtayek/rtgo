package model;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import equipment.Board;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
import equipment.Board.Shape;
import io.Logging;
import sgf.*;
import utilities.Utilities;
import static io.IOs.noIndent;
import static sgf.Parser.restoreSgf;

/**
 * Utilities for SGF round-tripping in tests.
 */
public final class ModelHelper {
    static record InterpretedNode(List<DomainAction> actions,List<SgfProperty> extras) {
        //
    }

    private ModelHelper() {}

    static void processProperty(Model model,Model.State state,SgfProperty property) {
        // need a way to convert this/these to gtp?
        Logging.mainLogger.config("property: "+property);
        String string=null;
        P p=property.p();
        P2 p2=P2.which(p.id);
        if(p2!=null) {
            switch(p2) {
                // AB[dl][ld] list of stones
                // vs list of points
                // ;FF[4]GM[1]AP[RTGO] SZ[19:17]KM[4.5]RG[ij][jj][kj][ii][ji][ki][ih][jh][kh]
                case AB:
                case AW:
                    for(String s:property.list()) {
                        Point point=Coordinates.fromSgfCoordinates(s,model.board().depth());
                        model.board().setAt(point,p2.equals(P2.AB)?Stone.black:Stone.white);
                    }
                    break;
                case AP:
                    state.application=property.list().get(0);
                    if(state.application.startsWith(Model.sgfApplicationName)); // mumble("it's
                    // one of ours");
                    break;
                case FF:
                    state.sgfVersion=property.list().get(0);
                    break;
                case GM:
                    string=property.list().get(0);
                    state.gameType=Integer.valueOf(string);
                    if(state.gameType!=Model.sgfGoGame) Logging.mainLogger.config(model.name+" "+"not a go game!");
                    break;
                case HA:
                    string=property.list().get(0);
                    state.handicap=Double.valueOf(string);
                    break;
                case KM:
                    string=property.list().get(0);
                    state.komi=Double.valueOf(string);
                    break;
                case BL:
                case WL:
                    // time left
                    break;
                case B:
                case W:
                    Stone color=null;
                    if(p2.equals(P2.B)) color=Stone.black;
                    else if(p2.equals(P2.W)) color=Stone.white;
                    else throw new RuntimeException("oops");
                    string=property.list().get(0);
                    boolean isPass=string.equals("");
                    model.ensureBoard();
                    if(model.board().width()<=Board.standard&&model.board().depth()<=Board.standard&&string.equals("tt"))
                        isPass=true; // hack for some sgf wierdness/
                    if(isPass) {
                        Logging.mainLogger.config(model.name+" "+"passing");
                        state.sgfPass();
                    } else {
                        Point point=Coordinates.fromSgfCoordinates(string,model.board().depth());
                        state.sgfMakeMove(color,point);
                        // if(!checkingForLegalMove) Audio.playStoneSound();
                        // if(areStonesInAtari()) Sound.play("goatari.wav");
                    }
                    break;
                case C:
                    // mumble("comment: "+property.list());
                    String comment=property.list().get(0);
                    if(comment.startsWith(Model.sgfApplicationName)) { // get type and shape
                        if(comment.startsWith(Model.sgfBoardTopology)) {
                            String typeString=comment.substring(Model.sgfBoardTopology.length());
                            Board.Topology type=Board.Topology.valueOf(typeString);
                            Logging.mainLogger.fine(model.name+" "+"setting board type to "+type);
                            model.setBoardTopology(type);
                        } else if(comment.startsWith(Model.sgfBoardShape)) {
                            String shapeString=comment.substring(Model.sgfBoardShape.length());
                            Shape shape=Shape.valueOf(shapeString);
                            model.setBoardShape(shape);
                        } else Logging.mainLogger.warning(model.name+" "+"what is "+comment);
                    }
                    break;
                case RG:
                    System.out.println("RG:  "+state.shape+", region: "+property.list());
                    // above we have diamond, hole1, region [jj]
                    // using state.board above does not seem quite right.
                    // yes, diamond needs another region or a bigger region.
                    // but diamond is a topology, maybe it should be a shape?
                    // if it's a shape, we must have a normal board.
                    // what about other shapes, do these require a normal board?
                    //
                    //if(!shape.equals(Shape.normal))
                    // let's let any board have regions
                    // maybe restrict diamond to normal for now and add some holes later?
                    model.ensureBoard();
                    if(model.board()!=null) for(String s:property.list()) {
                        Logging.mainLogger.config(model.name+" "+"hole at: "+s);
                        Point point=Coordinates.fromSgfCoordinates(s,model.board().depth());
                        model.board().setAt(point,Stone.edge);
                    }
                    else System.out.println("RG: board() is  null.");
                    break;
                case SZ: // create the board
                    // maybe delay the board creation?
                    // we need to convert this to gtp. how?
                    // what others do we need to convert?
                    string=property.list().get(0);
                    int width=(int)Parameters.width.currentValue();
                    int depth=(int)Parameters.depth.currentValue();
                    if(string.contains(":")) {
                        String[] tokens=string.split(":");
                        width=Integer.valueOf(tokens[0]);
                        depth=Integer.valueOf(tokens[1]);
                        if(width!=depth) Logging.mainLogger.config(model.name+" "+width+"!="+depth+"!");
                        Logging.mainLogger.fine(model.name+" "+"size: "+string);
                    } else width=depth=Integer.valueOf(string);
                    state.widthFromSgf=width;
                    state.depthFromSgf=depth;
                    int w=width;
                    int d=depth;
                    // check shape and adjust w and d!
                    // 7/15/21 we may not know the shape. we may have regions.
                    Board board=Board.factory.create(w,d,state.topology,state.shape);
                    // get state from controls?
                    Logging.mainLogger.fine(model.name+" "+"creating board");
                    model.setBoard(board);
                    // notify(Event.start,"SZ "+property);
                    // maybe we should do the notify above?
                    break;
                case RE:
                    String results=property.list().get(0);
                    Logging.mainLogger.severe(model.name+" "+"result: "+results);
                    //state.sgfResign(); // very bad
                    // why, seems like the right thing to do?
                    // maybe, but ogs has this up front before any moves
                    // lets keep this off and use ZBC and ZW for resign
                    break;
                case RT: // private property - my root
                    break;
                case ZB: // private property - black resign
                case ZW: // private property - white resign
                    color=null;
                    if(p2.equals(P2.ZB)) color=Stone.black;
                    else if(p2.equals(P2.ZW)) color=Stone.white;
                    else throw new RuntimeException("oops");
                    Logging.mainLogger.info(model.name+" "+"resigns");
                    state.sgfResign();
                    break;
                default:
                    Logging.mainLogger.config(model.name+" "+p2+" is not implemented!");
                    break;
            }
        } else Logging.mainLogger.warning(model.name+" "+"p2 is null!");
    }

    static List<DomainAction> mapPropertyToDomainActions(Model model, SgfProperty property) {
        P2 p2 = P2.which(property.p().id);
        if (p2 == null) return List.of();
        return switch (p2) {
            case AB -> mapSetupAdds(model, Stone.black, property.list());
            case AW -> mapSetupAdds(model, Stone.white, property.list());
            case B -> List.of(mapMoveOrPass(model, Stone.black, property.list().get(0)));
            case W -> List.of(mapMoveOrPass(model, Stone.white, property.list().get(0)));
            case RG -> mapEdges(model, property.list());
            case SZ -> List.of(mapSize(property.list().get(0)));
            case C -> mapComment(property.list().get(0));
            case ZB -> List.of(new DomainAction.Resign(Stone.black));
            case ZW -> List.of(new DomainAction.Resign(Stone.white));
            case RE -> List.of(new DomainAction.RecordResult(property.list().get(0)));
            // Keep metadata as metadata for now; apply() can mutate state like today.
            case AP, FF, GM, HA, KM, BL, WL, RT -> List.of(new DomainAction.Metadata(p2, property.list()));
            default -> List.of(new DomainAction.Metadata(p2, property.list()));
        };
    }

    private static List<DomainAction> mapSetupAdds(Model model, Stone color, List<String> coords) {
        List<DomainAction> out = new ArrayList<>(coords.size());
        for (String s : coords) out.add(new DomainAction.SetupAddStone(color, parseSgfPoint(model, s)));
        return out;
    }

    private static DomainAction mapMoveOrPass(Model model, Stone color, String coord) {
        if (coord == null || coord.isEmpty()) return new DomainAction.Pass(color);
        Point point = parseSgfPoint(model, coord);
        return new DomainAction.Move(color, point);
    }

    private static DomainAction mapSize(String sz) {
        if (sz == null || sz.isEmpty()) return new DomainAction.SetBoardSpec(Board.standard, Board.standard);
        if (sz.contains(":")) {
            String[] parts = sz.split(":");
            int w = Integer.parseInt(parts[0]);
            int d = Integer.parseInt(parts[1]);
            return new DomainAction.SetBoardSpec(w, d);
        }
        int n = Integer.parseInt(sz);
        return new DomainAction.SetBoardSpec(n, n);
    }

    private static List<DomainAction> mapEdges(Model model, List<String> coords) {
        List<DomainAction> actions = new ArrayList<>(coords.size());
        for (String s : coords) actions.add(new DomainAction.SetupSetEdge(parseSgfPoint(model, s)));
        return actions;
    }

    private static List<DomainAction> mapComment(String comment) {
        if (comment == null) return List.of();
        List<DomainAction> actions = new ArrayList<>();
        if (comment.startsWith(Model.sgfBoardTopology)) {
            String topo = comment.substring(Model.sgfBoardTopology.length());
            actions.add(new DomainAction.SetTopology(Board.Topology.valueOf(topo)));
        } else if (comment.startsWith(Model.sgfBoardShape)) {
            String shapeString = comment.substring(Model.sgfBoardShape.length());
            actions.add(new DomainAction.SetShape(Shape.valueOf(shapeString)));
        }
        actions.add(new DomainAction.Metadata(P2.C, List.of(comment)));
        return actions;
    }

    private static Point parseSgfPoint(Model model, String coord) {
        int depth = model.board() != null
            ? model.board().depth()
            : model.depthFromSgf() > 0 ? model.depthFromSgf() : Board.standard;
        return Coordinates.fromSgfCoordinates(coord, depth);
    }

    public static MNode modelRoundTrip(Reader reader, Writer writer) {
        StringBuffer sb = new StringBuffer();
        Utilities.fromReader(sb, reader);
        String expectedSgf = sb.toString(); // so we can compare
        SgfNode games = restoreSgf(new StringReader(expectedSgf));
        if (games == null) return null; // return empty node!
        if (games.right != null) System.out.println(" 2 more than one game!");

        MNode mNodes0 = MNode.toGeneralTree(games);
        Model model = new Model();
        model.setRoot(mNodes0);
        MNode mNodes = model.root();
        if (mNodes != null) {
            if (mNodes.children.size() > 1) {
                //System.out.println("more than one child: "+mNodes.children);
            }
            SgfNode sgfRoot = mNodes.toBinaryTree();
            SgfNode actual = sgfRoot.left;
            StringWriter hack = new StringWriter();
            actual.saveSgf(hack, noIndent);
            try {
                writer.write(hack.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mNodes;
    }

    public static MNode modelRoundTrip2(String expectedSgf, Writer writer) {
        SgfNode games = restoreSgf(new StringReader(expectedSgf));
        if (games == null) return null;
        if (games.right != null) System.out.println(" 2 more than one game!");
        games.saveSgf(new StringWriter(), noIndent);
        MNode mNodes0 = MNode.toGeneralTree(games);
        Model model = new Model();
        model.setRoot(mNodes0);
        MNode mNodes = model.root();
        String actualSgf = null;
        if (games != null) {
            SgfNode sgfRoot = mNodes.toBinaryTree();
            SgfNode actual = sgfRoot.left;
            StringWriter stringWriter = new StringWriter();
            actual.saveSgf(stringWriter, noIndent);
            actualSgf = stringWriter.toString();
        }
        if (actualSgf != null) try {
            writer.write(actualSgf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mNodes;
    }
}
