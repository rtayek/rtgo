package equipment;
import java.util.*;
import equipment.Board.Topology;
import io.Logging;
import com.tayek.util.core.Pair;
public record Block(Stone who,List<Point> points,int liberties) {
    public Block(Board board,Point point,boolean[][] processed) { this(build(board,point,processed)); }
    public Block(Board board,int x,int y,boolean[][] processed) { this(board,new Point(x,y),processed); }
    private Block(BlockData data) { this(data.who(),List.copyOf(data.points()),data.liberties()); }
    public Stone color() { return who; }
    @Override public String toString() {
        return "block: "+who+" "+points.size()+" stone(s), "+liberties+" liberties"+points;
    }
    public static Point modulo(Point point,Board board) { // only works for dx of 1!
        // or maybe i should just use modulo in each case and allow for dx >1?
        int x=point.x;
        int y=point.y;
        switch(board.topology()) { // how to do mobius and klein bottle?
            // mobius - overflow/underflow gets mirrored
            // maybe both gets us a klein bottle?
            case normal:
                break;
            case horizontalCylinder:
                if(x==-1) x=board.width()-1;
                else if(x==board.width()) x=0;
                break;
            case verticalCylinder:
                if(y==-1) y=board.depth()-1;
                else if(y==board.depth()) y=0;
                break;
            case torus:
                if(y==-1) y=board.depth()-1;
                else if(y==board.depth()) y=0;
                if(x==-1) x=board.width()-1;
                else if(x==board.width()) x=0;
                break;
            case diamond:
                break;
        }
        return new Point(x,y);
    }
    public static boolean isOffTheBoard(Point point,Board board) {
        Point wrapped=modulo(point,board);
        return !board.isOnBoard(wrapped);
    }
    private static BlockData build(Board board,Point point,boolean[][] processed) {
        List<Point> collected=new ArrayList<Point>();
        Stone color=board.at(point);
        grow(point,board,processed,color,collected);
        int liberties=countLiberties(board,collected);
        return new BlockData(color,collected,liberties);
    }
    private static void grow(Point point,Board board,boolean[][] processed,Stone color,List<Point> collected) {
        if(!board.topology().equals(Topology.normal)) point=modulo(point,board);
        if(!board.isOnBoard(point)) return;
        if(!processed[point.x][point.y]) {
            Stone whoOnBoard=board.at(point.x,point.y);
            // maybe i can/should generate a set of liberties as we go?
            if(whoOnBoard.equals(color)||whoOnBoard.equals(Stone.vacant)||whoOnBoard.equals(Stone.edge)) processed[point.x][point.y]=true;
            // try always setting processed=true. -- was a really bad idea!
            if(whoOnBoard==color) {
                collected.add(point);
                List<Point> neighbors=Neighborhood.neighbor4s(point);
                for(Point p:neighbors) grow(p,board,processed,color,collected);
            }
        }
    }
    private static int count(Point point,Board board,boolean[][] processed) {
        if(!board.topology().equals(Topology.normal)) point=modulo(point,board);
        if(board.isOnBoard(point)&&!processed[point.x][point.y]) { // checking not process will unsure tha we do not count liberties more than once.
            processed[point.x][point.y]=true;
            return board.at(point.x,point.y)==Stone.vacant ? 1 : 0;
        }
        return 0;
    }
    private static int countLiberties(Board board,List<Point> points) {
        int liberties=0;
        boolean[][] processed=new boolean[board.width()][board.depth()];
        for(int k=0;k<points.size();k++) {
            Point p=points.get(k);
            List<Point> neighbors=Neighborhood.neighbor4s(p);
            for(Point point:neighbors) liberties+=count(point,board,processed);
        }
        return liberties;
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
        ArrayList<Point> adjusted=new ArrayList<>(neighbor4s.size());
        for(Point point:neighbor4s) {
            Point adjustedPoint=board.topology().equals(Topology.normal)?point:modulo(point,board);
            if(board.isOnBoard(adjustedPoint)) adjusted.add(adjustedPoint);
        }
        ArrayList<Block> capturedBlocks=new ArrayList<>(4);
        for(Point point:adjusted) if(board.at(point).equals(stone.otherColor())) {
            Block block=find(board,point);
            if(block!=null&&block.liberties()==0) capturedBlocks.add(block);
        }
        return capturedBlocks;
    }
    public static ArrayList<Block> findAdjacentOpponentsBlocksInAtari(Board board,Point at,Stone stone) {
        List<Point> neighbor4s=Neighborhood.neighbor4s(at);
        ArrayList<Point> adjusted=new ArrayList<>(neighbor4s.size());
        for(Point point:neighbor4s) {
            Point adjustedPoint=board.topology().equals(Topology.normal)?point:modulo(point,board);
            if(board.isOnBoard(adjustedPoint)) adjusted.add(adjustedPoint);
        }
        ArrayList<Block> blocksInAtari=new ArrayList<>(4);
        for(Point point:adjusted) if(board.at(point).equals(stone.otherColor())) {
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
    private static record BlockData(Stone who,List<Point> points,int liberties) {}
}
