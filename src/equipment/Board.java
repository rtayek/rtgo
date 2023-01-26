package equipment;
import static org.junit.Assert.*;
import java.io.FileNotFoundException;
import java.util.*;
import equipment.Board.*;
import io.*;
import model.Model;
import utilities.Pair;
public interface Board { // http://stackoverflow.com/questions/28681737/java-8-default-methods-as-traits-safe
    // make a subinterface called MutableBoard?
    public enum Topology { normal, horizontalCylinder, verticalCylinder, torus, diamond }
    public enum Shape { // is this a duplicate? don't think so.
        // maybe move this to board?
        normal,plus1,plus2,plus3,plus4,plus5,plus6,hole1,hole3,hole5,programmer;
        public static List<Point> getPointsForRegion(int width,int depth,Shape shape) {
            List<Point> points=new ArrayList<>();
            switch(shape) {
                case normal:
                    break;
                case plus1:
                    points.addAll(squares(1,width,depth));
                    break;
                case plus2:
                    points.addAll(squares(2,width,depth));
                    break;
                case plus3:
                    points.addAll(squares(3,width,depth));
                    break;
                case plus4:
                    points.addAll(squares(4,width,depth));
                    break;
                case plus5:
                    points.addAll(squares(5,width,depth));
                    break;
                case plus6:
                    points.addAll(squares(6,width,depth));
                    break;
                case hole1:
                    points.addAll(holeInCenter(0,width,depth));
                    break;
                case hole3:
                    points.addAll(holeInCenter(1,width,depth));
                    break;
                case hole5:
                    points.addAll(holeInCenter(2,width,depth));
                    Logging.mainLogger.warning("hole 5 points: "+points);
                    break;
                case programmer:
                    if(width>23||depth<19) {
                        Logging.mainLogger.warning("not big enough for programmers board!");
                    } else {
                        int[] x1=new int[] {5,11,17,18,19,20,21,22};
                        int[] x2=new int[] {5,11,17,18,19,21,22};
                        int[] x3=new int[] {0,1,3,4,5,6,7,9,10,11,12,13,15,16,17,18,19,21,22};
                        int[] x4=new int[] {21,22};
                        int[] x5=new int[] {17};
                        int[] y1=new int[] {0,1,17,18};
                        int[] y2=new int[] {3,4,14,15};
                        int[] y3=new int[] {5,13};
                        int[] y4=new int[] {2,16};
                        int[] y5=new int[] {6,7,8,10,11,12};
                        for(int i=0;i<x1.length;i++) for(int j=0;j<y1.length;j++) points.add(new Point(x1[i],y1[j]));
                        for(int i=0;i<x2.length;i++) for(int j=0;j<y2.length;j++) points.add(new Point(x2[i],y2[j]));
                        for(int i=0;i<x3.length;i++) for(int j=0;j<y3.length;j++) points.add(new Point(x3[i],y3[j]));
                        for(int i=0;i<x4.length;i++) for(int j=0;j<y4.length;j++) points.add(new Point(x4[i],y4[j]));
                        for(int i=0;i<x5.length;i++) for(int j=0;j<y5.length;j++) points.add(new Point(x5[i],y5[j]));
                    }
                    break;
                default:
                    Logging.mainLogger.config("unhandled case: "+shape);
            }
            return points;
        }
    }
    int width();
    int depth();
    Topology topology();
    Shape shape();
    default int moduloWidth(int x) { // add these to board?
        return modulo(x,width());
    }
    default int moduloDepth(int y) { return modulo(y,depth()); }
    int id();
    Stone[] stones();
    Board copy();
    default boolean isInRange(Point point) { return isInRange(point.x,point.y); }
    default boolean isXInRange(int x) { return 0<=x&&x<width(); }
    default boolean isYInRange(int y) { return 0<=y&&y<depth(); }
    default boolean isInRange(int x,int y) { return isXInRange(x)&&isYInRange(y); }
    default public boolean isOnBoard(Point point) {
        if(point==null) { Logging.mainLogger.severe("point is null!"); return false; }
        return isOnBoard(point.x,point.y);
    }
    default boolean isOnBoard(int x,int y) { return isInRange(x,y)&&!at(x,y).equals(Stone.edge); }
    static int index(int x,int y,int length) { return length*y+x; }
    default int index(int x,int y) {
        return index(x,y,width()); // maybe wrong, but it's only in one place
    }
    default int index(Point point) { return index(point.x,point.y); }
    default Stone at(int index) { return stones()[index]; }
    default Stone at(int x,int y) { return stones()[index(x,y)]; }
    default Stone at(Point point) { return stones()[index(point)]; }
    //default Stone at(String string) { return at(,); }
    default void setAt(Point point,Stone color) { setAt(point.x,point.y,color); }
    default void setAt(int x,int y,Stone color) { setAt(index(x,y),color); }
    void setAt(int k,Stone color);
    default void setAll(Stone color) {
        for(int x=0;x<width();x++) for(int y=0;y<depth();y++) if(at(x,y)!=Stone.edge) setAt(x,y,color);
    }
    boolean hasStarPoints();
    static List<Point> getPointsForDiamondRegion(int width,int depth) {
        List<Point> points=new ArrayList<>();
        // manhatten distance - may work!
        int xCenter=width/2,yCenter=depth/2;
        for(int x=0;x<width;x++) for(int y=0;y<depth;y++) {
            int dx=Math.abs(x-xCenter),dy=Math.abs(y-yCenter);
            if(dx+dy>width/2) // better be a square board
                points.add(new Point(x,y));
        }
        return points;
    }
    default boolean isEqual(Board board) { // why is this not equals()?
        if(board==null) return false;
        if(width()!=board.width()||depth()!=board.depth()) return false;
        for(int i=0;i<stones().length;i++) if(stones()[i]!=board.at(i)) { return false; }
        // does b=not consider komi, prisoners, or ko state!
        return true; // does this really work for sure?
    }
    default String toShortString() { return width()+" by "+depth()+" "+topology(); }
    static int modulo(int z,int n) { z%=n; if(z<0) z+=n; return z; }
    Point uL(); // upper left
    Point uR(); // upper right
    Point lL(); // lower left
    Point lR(); // upper right
    Point center(); // cener of board
    List<Point> starPoints();
    static List<Point> squares(int n,int width,int depth) {
        ArrayList<Point> points=new ArrayList<>(4*n*n);
        for(int x=0;x<n;x++) for(int y=0;y<n;y++) {
            points.add(new Point(x,y));
            points.add(new Point(x,depth-1-y));
            points.add(new Point(width-1-x,y));
            points.add(new Point(width-1-x,depth-1-y));
        }
        return points;
    }
    static List<Point> holeInCenter(int n,int width,int depth) {
        ArrayList<Point> points=new ArrayList<>((2*n+1)*(2*n+1));
        for(int x=-n;x<=n;x++) for(int y=-n;y<=n;y++) points.add(new Point(y+width/2,x+depth/2));
        return points;
    }
    Integer smallest=1,standard=19,largest=26;
    interface Factory {
        Board create();
        Board create(int n);
        Board create(int n,Board.Topology type);
        Board create(int width,int depth,Board.Topology type);
        Board create(int width,int depth,Board.Topology type,Shape shape);
    }
    public static Board part(Board board,int width,int depth,Point origin) {
        Board smaller=factory.create(width,depth,Topology.normal);
        for(int y=0;y<depth;++y) for(int x=0;x<width;++x) {
            Stone color=board.at(origin.x+x,origin.y+y);
            smaller.setAt(x,y,color);
        }
        return smaller;
    }
    public static void x() {
        Board board=factory.create(7,7,Topology.normal);
        for(int x=0;x<board.width();++x) board.setAt(x,board.depth()-1-x,Stone.black);
        for(int x=0;x<board.width();++x) board.setAt(x,x,Stone.white);
        int width=5,depth=5;
        // make these available from board
        // these are strange, the other 2 look similar.
        // yes, because they are getting part of a board.
        Point lL=new Point(0,0);
        Point lR=new Point(board.width()-width,0);
        Point uL=new Point(0,board.depth()-depth);
        Point uR=new Point(board.width()-width,board.depth()-depth);
        Set<Point> points=Set.of(lL,lR,uL,uR);
        for(Point point:points) {
            Board lowerLeft=part(board,width,depth,point);
            System.out.println(board);
            System.out.println(lowerLeft);
            System.out.println(point+" -----------------------");
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        //x(); 
        int width=23,depth=19;
        Topology topology=Topology.normal;
        for(Shape shape:Shape.values()) {
            Model model=new Model();
            model.setBoardTopology(Topology.normal);
            model.setBoardShape(shape);
            model.setRoot(width,depth,topology,shape);
            // triangle?
            Board board=model.board();
            List<Point> points=topology==Topology.diamond?points=Board.getPointsForDiamondRegion(width,depth)
                    :Shape.getPointsForRegion(width,depth,shape);
            System.out.println(points.size()+" points.");
            int xMax=-1,yMax=-1;
            for(Point point:points) {
                if(point.x>xMax) xMax=point.x;
                if(point.y>yMax) yMax=point.y;
            }
            System.out.println(xMax+"x"+yMax);
            int i=0;
            for(Point point:points) {
                System.out.println(i+" point: "+point);
                if(!Stone.edge.equals(board.at(point))) System.out.println("bad");
                ++i;
            }
            System.out.println(model);
        }
    }
    Factory factory=BoardFactory.instance;
}
class Neighborhood {
    // we may need to check if onboard?
    private static void four(int x,int y,ArrayList<Point> neighbors) {
        neighbors.add(new Point(x-1,y));
        neighbors.add(new Point(x+1,y));
        neighbors.add(new Point(x,y-1));
        neighbors.add(new Point(x,y+1));
    }
    private static void diag(int x,int y,ArrayList<Point> neighbors) {
        neighbors.add(new Point(x-1,y-1));
        neighbors.add(new Point(x+1,y-1));
        neighbors.add(new Point(x-1,y+1));
        neighbors.add(new Point(x+1,y+1));
    }
    static List<Point> neighbor4s(Point point) {
        ArrayList<Point> points=new ArrayList<>(4);
        four(point.x,point.y,points);
        return points;
    }
    static ArrayList<Point> neighbor8s(int x,int y) {
        ArrayList<Point> points=new ArrayList<>(8);
        // check for edge?
        four(x,y,points);
        points.add(new Point(x+0,y+0));
        diag(x,y,points);
        return points;
    }
}
class BoardFactory implements Board.Factory {
    //board interface is way over-engineered. fix!
    @Override public Board create() { return create(Board.standard); }
    @Override public Board create(int n) { return create(n,Topology.normal); }
    @Override public Board create(int n,Topology type) { return create(n,n,type,Shape.normal); }
    @Override public Board create(int width,int depth,Topology type) { return create(width,depth,type,Shape.normal); }
    @Override public Board create(int width,int depth,Topology topeology,Shape shape) {
        System.out.println("factory is crearing board.");
        BoardImpl boardImpl=null;
        if(topeology.equals(Topology.diamond)) if(shape.equals(Shape.programmer)) {
            Logging.mainLogger.severe(topeology+" "+shape+"is illegal combination");
            //throw new RuntimeException(type+" "+shape+"is illegal combination");
            return boardImpl;
        }
        if(shape.equals(Shape.programmer)) { width=23; depth=19; }
        try {
            boardImpl=new BoardImpl(width,depth,topeology,shape,ids++);
            // what about shape?
            // yes. what about it!
            // also, how is topology handled?
            // figure out what the legal combinations are.
        } catch(Exception e) {
            Logging.mainLogger.info("board factory fails!");
        }
        return boardImpl;
    }
    private BoardFactory() {}
    static int ids;
    static final BoardFactory instance=new BoardFactory();
}
abstract class BoardABC implements Board {
    BoardABC(int width,int depth,Topology topology,Shape shape,int id) {
        this.id=id;
        this.width=width;
        this.depth=depth;
        this.topology=topology!=null?topology:Topology.normal;
        this.shape=shape==null?Shape.normal:shape;
        center=new Point(width/2,depth/2);
        lL=new Point(0,0);
        lR=new Point(width()-1,0);
        uL=new Point(0,depth()-1);
        uR=new Point(width()-1,depth()-1);
        if(hasStarPoints()) initializeStarPoints(); // maybe belongs in a
        // subclass?
    }
    @Override public Board copy() {
        BoardImpl board=(BoardImpl)factory.create(width(),depth(),topology());
        for(int i=0;i<stones().length;i++) board.setAt(i,stones()[i]);
        return board;
    }
    @Override public int id() { return id; }
    @Override public Topology topology() { return topology; }
    @Override public Shape shape() { return shape; }
    @Override public int width() { return width; }
    @Override public int depth() { return depth; }
    @Override public Point uL() { return uL; }
    @Override public Point uR() { return uR; }
    @Override public Point lL() { return lL; }
    @Override public Point lR() { return lR; }
    @Override public Point center() { return center; }
    // refactor into 4 neighbors + diagonals
    Pair<List<Point>,List<Point>> neighbor8sOfCorners() { // unused!
        // what about edges?
        ArrayList<Point> points=new ArrayList<Point>(36);
        points.addAll(Neighborhood.neighbor8s(0,0)); // why do we need Board here??????
        points.addAll(Neighborhood.neighbor8s(0,width()-1));
        points.addAll(Neighborhood.neighbor8s(depth()-1,0));
        points.addAll(Neighborhood.neighbor8s(width()-1,depth()-1));
        List<Point> onBoard=new ArrayList<Point>(16);
        List<Point> offBoard=new ArrayList<Point>(20);
        for(Point point:points) if(isOnBoard(point)) onBoard.add(point);
        else offBoard.add(point);
        return new Pair<>(onBoard,offBoard);
    }
    @Override public boolean hasStarPoints() { // add test for diamond. and others?
        return(width()==depth()&&width()<starPointTable.length);
    }
    @Override public String toString() {
        StringBuffer sb=new StringBuffer((width+3)*(depth+2)+100);
        sb.append('+');
        for(int column=0;column<width;column++) sb.append('-');
        sb.append('+');
        sb.append('\n');
        for(int row=0;row<depth;row++) {
            sb.append('|');
            for(int column=0;column<width;column++) sb.append(at(column,depth-1-row).toCharacter());
            sb.append('|');
            sb.append('\n');
        }
        sb.append('+');
        for(int column=0;column<width;column++) sb.append('-');
        sb.append('+');
        sb.append('\n');
        return sb.toString();
    }
    private void initializeStarPoints() {
        if(width==depth) if(width<starPointTable.length) {
            String s=starPointTable[width];
            starPoints=new Point[s.length()/2];
            for(int i=0;i<s.length()/2;i++) starPoints[i]=new Point(s.charAt(2*i)-'a'+1,s.charAt(2*i+1)-'a'+1);
        }
    }
    final Topology topology;
    final Shape shape;
    // no shape!
    // probably need a shape here
    final int width,depth;
    final int id;
    Point[] starPoints;
    final Point center;
    final Point lL,lR,uL,uR; // use these now that we have them.
    // star points stuff probably belongs in a subclass?
    private static String starPointTable[]= {"", // 0
            "aa", // 1
            "", // 2
            "", // 3
            "", // 4
            "cc", // 5
            "", // 6
            "dd", // 7
            "cccffcff", // 8
            "ggcccggc", // 9
            "hhccchhc", // 10
            "iiccciicff", // 11
            "iidddiid", // 12
            "jjdddjjdgg", // 13
            "kkdddkkd", // 14
            "lldddllddhlhhdhlhh", // 15
            "mmdddmmd", // 16
            "nndddnnddiniidinii", // 17
            "oodddood", // 18
            "ppdddppddjpjjdjpjj" // 19
            // looks like the are 1 based as opposed to 0 based.
    };
}
class BoardImpl extends BoardABC {
    BoardImpl(int n,int id) { this(n,n,Topology.normal,Shape.normal,id); }
    BoardImpl(int width,int depth,Topology topology,Shape shape,int id) {
        super(width,depth,topology,shape,id);
        stones=new Stone[width*depth];
        setAll(Stone.vacant);
        if(topology.equals(Topology.diamond)) {
            // need to set the unused regions to edge!
            // not sure this is the right place to do this.
            // other boards with holes look like the do something else wth nodes and regions.
            // yes they do/did something else, but i think we broke that by tossing parent mnode.
            // seems like they shapes should all be setup here?
            List<Point> points=Board.getPointsForDiamondRegion(width(),depth());
            for(Point point:points) setAt(point,Stone.edge);
        }
    }
    @Override public Stone[] stones() { return stones; }
    public static boolean isLeft(Point a,Point b,Point c) { return ((b.x-a.x)*(c.y-a.y)-(b.y-a.y)*(c.x-a.x))>0; }
    @Override public void setAt(int k,Stone color) {
        stones[k]=color; // no one else should use direct access!
    }
    private static void addNeighbors(List<Point> points,int x,int y) {
        // fix this code, it's duplicated above in neighbors.
        // and this is only called from unused code!
        points.add(new Point(x-1,y-1));
        points.add(new Point(x,y-1));
        points.add(new Point(x+1,y-1));
        points.add(new Point(x-1,y));
        points.add(new Point(x,y)); // maybe omit this?
        points.add(new Point(x+1,y));
        points.add(new Point(x-1,y+1));
        points.add(new Point(x,y+1));
        points.add(new Point(x+1,y+1));
    }
    private Pair<List<Point>,List<Point>> NeighborsOfCorners(int n) { // unused!
        List<Point> points=new ArrayList<Point>(36);
        addNeighbors(points,0,0);
        addNeighbors(points,0,n-1);
        addNeighbors(points,n-1,0);
        addNeighbors(points,n-1,n-1);
        List<Point> onBoard=new ArrayList<Point>(16);
        List<Point> offBoard=new ArrayList<Point>(20);
        for(Point point:points) if(isOnBoard(point)) onBoard.add(point);
        else offBoard.add(point);
        return new Pair<>(onBoard,offBoard);
    }
    @Override public List<Point> starPoints() {
        if(width()>standard||depth()>standard||width()!=depth()) return null;
        // check topology
        return Arrays.asList(starPoints);
    }
    static BoardImpl random(int n) {
        Random random=new Random();
        BoardImpl b=new BoardImpl(n,0);
        for(int i=0;i<b.stones.length;i++) b.stones[i]=Stone.values()[random.nextInt(Stone.values().length)];
        return b;
    }
    private final Stone[] stones;
    // looks like we could resize the board if we wanted to as well as change
    // the topology
    // we just need to change width, depth, and stones.
}
