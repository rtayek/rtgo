package model;
import io.Logging;
import java.util.Objects;
import equipment.*;
// PASS & resign https://www.gnu.org/software/gnugo/gnugo_19.html
public final class Move2 { // make this a record?
	@Override public int hashCode() {
		return Objects.hash(color,moveType,name,point);
	}
	@Override public boolean equals(Object obj) {
		if(this==obj) return true;
		if(obj==null) return false;
		if(getClass()!=obj.getClass()) return false;
		Move2 other=(Move2)obj;
		return color==other.color&&moveType==other.moveType&&Objects.equals(name,other.name)&&Objects.equals(point,other.point);
	}
	public Move2(MoveType moveType,Stone color,Point point) {
		this(moveType,color,point,moveType.toString());
	}
	public Move2(MoveType moveType,Stone color,Point point,String name) {
		this.name=name;
		this.moveType=moveType;
		this.color=color;
		this.point=point;
	}
	public enum MoveType {
		move,nullMove,pass,resign,
	} // setup or undo?
	public String name() {
		return name;
	}
	public final String name;
	public final MoveType moveType;
	public final Stone color; // or null if you really must
	public final Point point; // only for play
	public static Move2 move(Stone color,Point point,String name) {
		Objects.requireNonNull(color);
		Objects.requireNonNull(point);
		return new Move2(MoveType.move,color,point,name);
	}
	public static Move2 pass(Stone color) {
		return new Move2(MoveType.pass,color,null);
	}
	public static Move2 resign(Stone color) {
		return new Move2(MoveType.resign,color,null);
	}
	public String nameWithColor() {
		return color+" "+moveType;
	}
	public boolean isPass() {
		return moveType.equals(MoveType.pass);
	}
	public boolean isResign() {
		return moveType.equals(MoveType.resign);
	}
	public boolean isNull() {
		return moveType.equals(MoveType.nullMove);
	}
	boolean isMove() {
		return moveType==MoveType.move;
	}
	@Override public String toString() {
		/* keep whatever legacy format */ return moveType.toString();
	}
	public static Move2 fromGTP(Stone color,String raw,int width,int depth) {
		if(raw==null) return Move2.nullMove;
		String string=raw.trim();
		if(string.equals("")) {
			Logging.mainLogger.info("string is: "+"\"\"");
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
	public static String toGTPCoordinates(Move2 move,int width,int depth) {
		if(move.isPass()) {
			return move.color.equals(Stone.black)?Move2.blackPass.name():Move2.whitePass.name();
		} else if(move.isResign()) {
			return move.color.equals(Stone.black)?Move2.blackResign.name():Move2.whiteResign.name();
		} else if(move.isNull()) {
			return "";
		} else if(move.isMove()) {
			return Coordinates.toGtpCoordinateSystem(move.point,width,depth);
		} else throw new RuntimeException("unknown move type for gtp coordinates: "+move);
	}
	public static final Move2 nullMove=new Move2(MoveType.nullMove,Stone.vacant,null);
	public static final Move2 blackPass=new Move2(MoveType.pass,Stone.black,null);
	public static final Move2 whitePass=new Move2(MoveType.pass,Stone.white,null);
	public static final Move2 blackResign=new Move2(MoveType.resign,Stone.black,null);
	public static final Move2 whiteResign=new Move2(MoveType.resign,Stone.white,null);
	public static final Move2 blackMoveAtA1=new Move2(MoveType.move,Stone.black,new Point());
	public static final Move2 whiteMoveAtA2=new Move2(MoveType.move,Stone.white,new Point(0,1));
	public static String gtpPassString="pass",gtpResignString="resign";
}
