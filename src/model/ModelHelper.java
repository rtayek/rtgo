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

    static GameNode toGameNode(Model model,sgf.MNode node) { return toGameNode(model,node,null); }
    private static GameNode toGameNode(Model model,sgf.MNode node,GameNode parent) {
        if(node==null) return null;
        List<DomainAction> actions=SgfDomainActionMapper.mapNodeToDomainActions(model,node);
        List<SgfProperty> extras=new ArrayList<>(node.extraProperties());
        List<RawProperty> rawExtras=new ArrayList<>(extras.size());
        for(SgfProperty p:extras) rawExtras.add(new RawProperty(p.p().id,p.list()));
        NodeAnnotations annotations=rawExtras.isEmpty()?NodeAnnotations.empty():
                new NodeAnnotations(rawExtras,List.of());
        List<GameNode> children=new ArrayList<>(node.children().size());
        GameNode current=new GameNode(actions,annotations,children);
        current.parent=parent;
        for(sgf.MNode child:node.children()) {
            GameNode c=toGameNode(model,child,current);
            if(c!=null) children.add(c);
        }
        return current;
    }

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
            if (mNodes.children().size() > 1) {
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
