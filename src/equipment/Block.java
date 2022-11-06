package equipment;
import java.util.*;
import equipment.Board.Topology;
import io.Logging;
import utilities.Pair;
public class Block {
    Block(Board board,Point point,boolean[][] processed) {
        this.who=board.at(point);
        points=new ArrayList<Point>();
        grow(point,board,processed);
        liberties=countLiberties(board);
    }
    Block(Board board,int x,int y,boolean[][] processed) { this(board,new Point(x,y),processed); }
    public int liberties() { return liberties_; }
    public Stone color() { return who; }
    public List<Point> points() { return Collections.unmodifiableList(points); }
    @Override public String toString() {
        return "block: "+who+" "+points.size()+" stone(s), "+liberties_+" liberties"+points;
    }
    public static Point modulo(Point point,Board board) { // only works for dx of 1!
        // or maybe i should just use modulo in each case and allow for dx >1?
        Point maybeWrapped=new Point(point);
        switch(board.topology()) { // how to do mobius and klein bottle?
            // mobius - overflow/underflow gets mirrored
            // maybe both gets us a klein bottle?
            case normal:
                break;
            case horizontalCylinder:
                if(maybeWrapped.x==-1) maybeWrapped.x=board.width()-1;
                else if(maybeWrapped.x==board.width()) maybeWrapped.x=0;
                break;
            case verticalCylinder:
                if(maybeWrapped.y==-1) maybeWrapped.y=board.depth()-1;
                else if(maybeWrapped.y==board.depth()) maybeWrapped.y=0;
                break;
            case torus:
                if(maybeWrapped.y==-1) maybeWrapped.y=board.depth()-1;
                else if(maybeWrapped.y==board.depth()) maybeWrapped.y=0;
                if(maybeWrapped.x==-1) maybeWrapped.x=board.width()-1;
                else if(maybeWrapped.x==board.width()) maybeWrapped.x=0;
                break;
            case diamond:
                break;
        }
        return maybeWrapped;
    }
    public static boolean isOffTheBoard(Point point,Board board) {
        Point wrapped=modulo(point,board);
        return !board.isOnBoard(wrapped);
    }
    private void grow(Point point,Board board,boolean[][] processed) {
        if(!board.topology().equals(Topology.normal)) point=modulo(point,board);
        if(!board.isOnBoard(point)) return;
        if(!processed[point.x][point.y]) {
            Stone who=board.at(point.x,point.y);
            // maybe i can/should generate a set of liberties as we go?
            if(who.equals(this.who)||who.equals(Stone.vacant)||who.equals(Stone.edge)) processed[point.x][point.y]=true;
            // try always setting processed=true. -- was a really bad idea!
            if(who==this.who) {
                points.add(point);
                List<Point> neighbors=Neighborhood.neighbor4s(point);
                for(Point p:neighbors) grow(p,board,processed);
            }
        }
    }
    private void count(Point point,Board board,boolean[][] processed) {
        if(!board.topology().equals(Topology.normal)) point=modulo(point,board);
        if(board.isOnBoard(point)&&!processed[point.x][point.y]) { // checking not process will unsure tha we do not count liberties more than once.
            if(board.at(point.x,point.y)==Stone.vacant) liberties_++;
            processed[point.x][point.y]=true;
        }
    }
    int countLiberties(Board board) {
        liberties_=0;
        boolean[][] processed=new boolean[board.width()][board.depth()];
        for(int k=0;k<points.size();k++) {
            Point p=points.get(k);
            List<Point> neighbors=Neighborhood.neighbor4s(p);
            for(Point point:neighbors) count(point,board,processed);
        }
        return liberties_;
    }
    static Block find(Board b,boolean[][] processed,int x,int y) {
        Block block=new Block(b,x,y,processed);
        return block;
    }
    public static Block find(Board b,Point p) {
        boolean[][] processed=new boolean[b.width()][b.depth()];
        return find(b,processed,p.x,p.y);
    }
    public static Pair<List<Block>,List<Block>> findBlocks(Board b) { // only used by tests
        // needs to be public now, since using from some server test cases.
        boolean[][] processed=new boolean[b.width()][b.depth()];
        List<Block> blackBlocks=new ArrayList<Block>();
        List<Block> whiteBlocks=new ArrayList<Block>();
        for(int x=0;x<b.width();x++) for(int y=0;y<b.depth();y++) {
            Stone who=b.at(x,y);
            if(!who.equals(Stone.edge)&&!who.equals(Stone.vacant)&&!processed[x][y]) {
                Block g=find(b,processed,x,y);
                switch(who) {
                    case black:
                        blackBlocks.add(g);
                        break;
                    case white:
                        whiteBlocks.add(g);
                        break;
                    default:
                        Logging.mainLogger.warning(""+" "+"default in findGroups "+b+g+who);
                        throw new RuntimeException("oops");
                }
            }
        }
        return new Pair<List<Block>,List<Block>>(blackBlocks,whiteBlocks);
    }
    public static ArrayList<Block> findAdjacentCapturedOpponentsBlocks(Board board,Point at,Stone stone) {
        List<Point> neighbor4s=Neighborhood.neighbor4s(at);
        for(Iterator<Point> iterator=neighbor4s.iterator();iterator.hasNext();) {
            Point point=iterator.next();
            if(!board.topology().equals(Topology.normal)) { // change the (x,y) in
                // the list!
                Point wrapped=modulo(point,board);
                point.x=wrapped.x;
                point.y=wrapped.y;
            }
            if(!board.isOnBoard(point)) iterator.remove();
        }
        ArrayList<Block> capturedBlocks=new ArrayList<>(4);
        for(Point point:neighbor4s) if(board.at(point).equals(stone.otherColor())) {
            Block block=find(board,point);
            if(block!=null&&block.liberties()==0) capturedBlocks.add(block);
        }
        return capturedBlocks;
    }
    public static ArrayList<Block> findAdjacentOpponentsBlocksInAtari(Board board,Point at,Stone stone) {
        List<Point> neighbor4s=Neighborhood.neighbor4s(at);
        for(Iterator<Point> iterator=neighbor4s.iterator();iterator.hasNext();) {
            Point point=iterator.next();
            if(!board.topology().equals(Topology.normal)) { // change the (x,y) in
                // the list!
                Point wrapped=modulo(point,board);
                point.x=wrapped.x; // change the values in the list
                point.y=wrapped.y; // change the values in the list
            }
            if(!board.isOnBoard(point)) iterator.remove();
        }
        ArrayList<Block> blocksInAtari=new ArrayList<>(4);
        for(Point point:neighbor4s) if(board.at(point).equals(stone.otherColor())) {
            Block block=find(board,point);
            if(block!=null&&block.liberties()==1) blocksInAtari.add(block);
        }
        return blocksInAtari;
    }
    private static Pair<List<Block>,List<Block>> findCapturedStonesOrSonesInAtari(List<Block> blocks) {
        List<Block> dead=new ArrayList<Block>();
        List<Block> inAtari=new ArrayList<Block>();
        Pair<List<Block>,List<Block>> pair=new Pair<>(dead,inAtari);
        if(blocks!=null) for(Block block:blocks) {
            if(block.liberties()==0) dead.add(block);
            else if(block.liberties()==1) inAtari.add(block);
        }
        return pair;
    }
    private final Stone who;
    private int liberties;
    private int liberties_;
    private List<Point> points;
}
