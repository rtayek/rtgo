package model;
import java.util.*;
import controller.Command;
import equipment.*;
import model.Move.MoveImpl;
public class MoveHelper {
    public static Move fromGTP(Stone color,String string,int width,int depth) {
        if(string==null) return Move.nullMove;
        else if(string.equals("")) {
            System.out.println("string is: "+"\"\"");
            return Move.nullMove; // pass?
        } else if(string.contains(gtpPassString)) {
            if(color.equals(Stone.black)) return Move.blackPass;
            else if(color.equals(Stone.white)) return Move.whitePass;
            else throw new RuntimeException("pass bad color!");
        } else if(string.contains(gtpResignString)) {
            if(color.equals(Stone.black)) return Move.blackResign;
            else if(color.equals(Stone.white)) return Move.whiteResign;
            else throw new RuntimeException("resign bad color!");
        }
        Point point=Coordinates.fromGtpCoordinateSystem(string,width);
        // check to see if point is on board? or at lease in range?
        return new MoveImpl(color,point);
    }
    public static List<String> toGTPMoves(List<Move> moves,int width,int depth) {
        List<String> commands=new ArrayList<>();
        for(Move move:moves) { // how about pass and resign?
            String string=Command.play.name()+" "+move.color()+" "+move.toGTPCoordinates(width,depth);
            commands.add(string);
        }
        return commands;
    }
    public static String gtpPassString="pass",gtpResignString="resign";
}
