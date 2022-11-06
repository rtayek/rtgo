package equipment;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import io.Logging;
import utilities.Utilities;
public class Coordinates {
    public static String toGtpCoordinateSystem(Point point,int width,int depth) { // no i!
        String moveString="";
        if(!new Rectangle(0,0,width,depth).contains(point)) {
            Logging.mainLogger.severe("point is off the board: "+point+" "+width+" "+depth+" "+Utilities.method(3));
            //throw new RuntimeException("point is off the board!");
        }
        char letter=(char)('a'+point.x);
        if(letter>'h') letter++; // no i allowed in this coordinate system
        letter=Character.toUpperCase(letter);
        moveString+=letter;
        moveString+=point.y+1;
        return moveString;
    }
    public static Point fromGtpCoordinateSystem(String move,int size) { // no i!
        // they are being used now by GTP
        // and GTP says that they should be in upper case!
        // and only support board sizes up to 25x25
        Character letter=move.charAt(0);
        if(Character.isLowerCase(letter)) Logging.mainLogger.warning(""+" "+"move: "+move+" has lower case character!");
        if(Character.isUpperCase(letter)) letter=Character.toLowerCase(letter);
        if(letter=='i') throw new RuntimeException("oops");
        int n=0;
        if(letter>'i') { // no i allowed in this coordinate system
            n++;
            letter--;
        }
        // Model.mumble(move + " " + letter + " " + (char) ('a' + size -
        // n) + " " + (letter - 'a') + ", n=" + n);
        if(false&&!('a'<=letter&&letter<= /* why is this = here? */('a'+size-n)))
            throw new RuntimeException("letter out of range");
        int number=0;
        try {
            number=Integer.parseInt(move.substring(1));
        } catch(NumberFormatException e) {
            Logging.mainLogger.warning(""+" "+move+" threw: "+e);
        }
        if(false&&!(1<=number&&number<=size)) // what is this for?
            throw new RuntimeException("number out of range: "+number);
        return new Point(letter-'a',number-1);
    }
    public static String toSgfCoordinates(Point board,int depth) {
        // allow "A-Z" and add test
        // this does not check to see if the coordinates are legit like to GTP
        return ""+(char)('a'+board.x)+""+(char)('a'+depth-1-board.y);
    }
    public static Point fromSgfCoordinates(String string,int depth) {
        // allow "A-Z" and add test
        if(string.length()!=2) throw new RuntimeException();
        char x=string.charAt(0),y=string.charAt(1);
        Point point=new Point(x-'a','a'+depth-1-y);
        return point;
    }
    public static Point2D.Float toBoardCoordinates(Point screen,Point p0,Point dp,int depth) {
        return new Point2D.Float((screen.x-p0.x)/(float)dp.x,((depth-1)*dp.y-(screen.y-p0.y))/(float)dp.y);
    }
    public static Point toScreenCoordinates(Point board,Point p0,Point dp,int depth) {
        return new Point(p0.x+(board.x)*dp.x,p0.y+(depth-1-board.y)*dp.y);
    }
    public static void main(String[] args) { // explain the coordinate systems
        int n=Board.standard;
        Point point=new Point(0,0);
        System.out.println("board coordinate system is right handed, origin is lower left: "+point);
        String noI=toGtpCoordinateSystem(point,n,n);
        System.out.println("noI/gtp coordinate system is right handed, origin is lower left: "+noI);
        String sgf=toSgfCoordinates(point,n);
        String sgfOrigin=toSgfCoordinates(new Point(0,n-1),n);
        System.out.println("sgf coordinate system is left handed, origin is upper left: "+sgfOrigin);
        Point screenOrigin=new Point(0,0);
        System.out.println("screen coordinate system is left handed, origin is upper left: "+screenOrigin);
        Point2D.Float screen=toBoardCoordinates(screenOrigin,new Point(0,0),new Point(10,10)/*no aspect ratio!*/,n);
        System.out.println(
                "so lower left corner is: "+point+" (board),  "+noI+" (gtp), "+sgf+" (sgf), "+screen+" (screen)");
    }
}
