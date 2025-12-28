package model;
import java.util.*;
import controller.*;
import controller.Command;
import equipment.*;
import io.IOs.End.Holder;
import io.Logging;
import model.LegacyMove.*;
import model.Move2.MoveType;
public class MoveHelper {
    static Move2 toGameMove(LegacyMove legacy,int width,int depth) {
        //@Override public String toSGFCoordinates(int width,int depth) { return ""; }
        //@Override public String toGTPCoordinates(int width,int depth) { return Move.nullMove.name(); }
        if(legacy instanceof Pass) {
            if(legacy.color().equals(Stone.black)) return Move2.blackPass;
            else if(legacy.color().equals(Stone.white)) return Move2.whitePass;
            else throw new RuntimeException("pass bad color!");
        } else if(legacy instanceof Resign) {
            if(legacy.color().equals(Stone.black)) return Move2.blackResign;
            else if(legacy.color().equals(Stone.white)) return Move2.whiteResign;
            else throw new RuntimeException("resign bad color!");
        } else if(legacy instanceof Null) {
            return Move2.nullMove;
        } else if(legacy instanceof MoveImpl) {
            Point point=legacy.point();
            return Move2.move(legacy.color(),point,legacy.name());
        } else throw new RuntimeException("unknown legacy move type: "+legacy);
    }
    static LegacyMove toLegacyMove(Move2 m) {
        if(m.isMove()) {
            return new MoveImpl(m.color,m.point);
        } else if(m.isPass()) {
            if(m.color.equals(Stone.black)) return LegacyMove.blackPass;
            else if(m.color.equals(Stone.white)) return LegacyMove.whitePass;
            else throw new RuntimeException("pass bad color!");
        } else if(m.isResign()) {
            if(m.color.equals(Stone.black)) return LegacyMove.blackResign;
            else if(m.color.equals(Stone.white)) return LegacyMove.whiteResign;
            else throw new RuntimeException("resign bad color!");
        } else if(m.isNull()) {
            return LegacyMove.nullMove;
        } else throw new RuntimeException("unknown Move2 type: "+m);
    }
    public static String toSGFCoordinates(LegacyMove move,int width,int depth) {
        if(move instanceof Pass) {
            return gtpPassString; // was returning "" - check for this!
            // looks like a bug!
        } else if(move instanceof Resign) {
            return gtpResignString;
        } else if(move instanceof Null) {
            throw new RuntimeException("null move has no sgf coordinates!");
        } else if(move instanceof MoveImpl) {
            // looks like sgf uses depth
            // i use width everywhere else
            return Coordinates.toSgfCoordinates(move.point(),/*width,*/depth);
        } else throw new RuntimeException("unknown move type for sgf coordinates: "+move);
    }
    public static String toGTPCoordinates(LegacyMove move,int width,int depth) {
        if(move instanceof Pass) {
            return move.color().equals(Stone.black)?LegacyMove.blackPass.name():LegacyMove.whitePass.name();
        } else if(move instanceof Resign) {
            return move.color().equals(Stone.black)?LegacyMove.blackResign.name():LegacyMove.whiteResign.name();
        } else if(move instanceof Null) {
            return ""; // treat null as no-op for encoding
        } else if(move instanceof MoveImpl) {
            return Coordinates.toGtpCoordinateSystem(move.point(),width,depth);
        } else throw new RuntimeException("unknown move type for gtp coordinates: "+move);
    }
    public static String toGTPCoordinates(Move2 move,int width,int depth) {
        return toGTPCoordinates(toLegacyMove(move),width,depth);
    }
    public static Move2 fromGTP(Stone color,String raw,int width,int depth) {
        if(raw==null) return Move2.nullMove;
        String string=raw.trim();
        if(string.equals("")) {
            System.out.println("string is: "+"\"\"");
            return Move2.nullMove; // pass?
        } else if(string.equalsIgnoreCase(gtpPassString)) {
            if(color.equals(Stone.black)) return Move2.blackPass;
            else if(color.equals(Stone.white)) return Move2.whitePass;
            else throw new RuntimeException("pass bad color!");
        } else if(string.equalsIgnoreCase(gtpResignString)) {
            if(color.equals(Stone.black)) return Move2.blackResign;
            else if(color.equals(Stone.white)) return Move2.whiteResign;
            else throw new RuntimeException("resign bad color!");
        }
        Point point=Coordinates.fromGtpCoordinateSystem(string,width);
        // check to see if point is on board? or at lease in range?
        return new Move2(MoveType.move,color,point);
    }
    public static List<String> toGTPMoves(List<Move2> moves,int width,int depth) {
        List<String> commands=new ArrayList<>();
        for(Move2 move:moves) { // how about pass and resign?
            String string=Command.play.name()+" "+move.color+" "+toGTPCoordinates(move,width,depth);
            commands.add(string);
        }
        return commands;
    }
    public static String gtpPassString="pass",gtpResignString="resign";
    public static Model pushGTPMovesToCurrentStateDirect(Model original,boolean oneAtATime) {
        Model model=new Model();
        if(original.board()!=null) { // normally no access to both of these at the same time
            model.setRoot(original.board().width(),original.board().depth());
            Board board=Board.factory.create(original.board().width(),original.board().depth());
            model.setBoard(board);
            // probably need to set other stuff like shape etc.
        }
        // should set the board shape and topology also?
        List<String> gtpMoves=original.gtpMovesToCurrentState();
        boolean ok=GTPBackEnd.checkMoveCommandsDirect(model,gtpMoves,oneAtATime);
        if(!ok) Logging.mainLogger.severe("push fails on: "+gtpMoves);
        return model;
    }
    // should we do this before sending commands?
    //int width=model.board().width();
    //int depth=model.board().depth();
    // use width and depth command?
    //String command=Command.boardsize.name()+" "+width+":"+depth;
    //gtpMoves.add(0,command);
    public static void getMovesAndPush(GTPFrontEnd frontEnd,Model model,boolean oneAtATime) {
        List<String> gtpMoves=model.gtpMovesToCurrentState();
        System.out.println("gtp moves: "+gtpMoves);
        if(oneAtATime) {
            for(String gtpMove:gtpMoves) {
                Response response=frontEnd.sendAndReceive(gtpMove);
                if(!response.isOk()) Logging.mainLogger.severe(response+" is not ok!");
            }
        } else frontEnd.sendAndReceive(gtpMoves);
    }
    public static Model pushGTPMovesToCurrentStateBoth(Model original,boolean oneAtATime) {
        Model model=new Model("model");
        if(original.board()!=null) { // normally no access to both of these at the same time
            model.setRoot(original.board().width(),original.board().depth());
            Board board=Board.factory.create(original.board().width(),original.board().depth());
            model.setBoard(board);
        }
        BothEnds both=new BothEnds();
        Holder holder=Holder.duplex();
        both.setupBoth(holder,"test",model);
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        getMovesAndPush(both.frontEnd,original,oneAtATime);
        return model;
    }
    public static void main(String[] args) {
        Model original=new Model();
        original.setRoot(5,5);
        original.move(Stone.black,new Point());
        original.move(Stone.white,new Point(1,1));
        Model model=pushGTPMovesToCurrentStateDirect(original,false);
        if(!model.board().isEqual(original.board())) System.out.println("fail!");
        Model model2=pushGTPMovesToCurrentStateDirect(original,true);
        if(!model2.board().isEqual(original.board())) System.out.println("fail!");
        Model model3=pushGTPMovesToCurrentStateBoth(original,true);
        if(!model3.board().isEqual(original.board())) System.out.println("fail!");
        Model model4=pushGTPMovesToCurrentStateBoth(original,false);
        if(!model4.board().isEqual(original.board())) System.out.println("fail!");
    }
}
