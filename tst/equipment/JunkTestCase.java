package equipment;
import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.*;
import equipment.Board.Topology;
import io.Logging;
import utilities.*;
public class JunkTestCase {
    // maybe not junk = may be useful
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testTopology() {
        for(Topology topology:Topology.values()) if(!topology.equals(Topology.diamond)) {
            Logging.mainLogger.info("=== testing topology: "+topology+" ===");
            board=Board.factory.create(n,topology);
            String[] names=new String[] {"uL","uR","lL","lR"};
            Point[] points=new Point[] {board.uL(),board.uR(),board.lL(),board.lR()};
            Block[] blocks=new Block[names.length];
            Map<String,Point> map=new LinkedHashMap<>();
            for(int i=0;i<names.length;++i) map.put(names[i],points[i]);
            for(Point point:points) board.setAt(point,Stone.black);
            Map<String,Block> map2=new LinkedHashMap<>();
            for(String name:map.keySet()) {
                Logging.mainLogger.info(name+" at "+map.get(name));
                map2.put(name,
                        new Block(board,map.get(name).x,map.get(name).y,new boolean[board.width()][board.depth()]));
            }
            for(String name:map.keySet()) Logging.mainLogger.info(name+" "+map.get(name)+" "+map2.get(name));
            for(String name:map.keySet()) {
                Block actual=map2.get(name);
                Pair<Integer,Integer> expected=expectedMap.get(topology);
                assertEquals(Integer.valueOf(actual.points().size()),expected.first);
                assertEquals(Integer.valueOf(actual.liberties()),expected.second);
            }
        } else Logging.mainLogger.warning("how to test: "+topology+"?");
    }
    int n=5;
    Board board;
    static Map<Topology,Pair<Integer,Integer>> expectedMap=new TreeMap<>();
    static {
        expectedMap.put(Topology.normal,new Pair<Integer,Integer>(1,2));
        expectedMap.put(Topology.horizontalCylinder,new Pair<Integer,Integer>(2,4));
        expectedMap.put(Topology.verticalCylinder,new Pair<Integer,Integer>(2,4));
        expectedMap.put(Topology.torus,new Pair<Integer,Integer>(4,8));
    }
}
