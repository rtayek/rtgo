package model;
import static sgf.Parser.getSgfData;
import java.io.*;
import java.util.logging.Level;
import org.junit.*;
import equipment.Board;
import io.Logging;
import sgf.*;
public class OblongTestCase {
    @Before public void setUp() throws Exception {
        File file=new File("sgf/lecoblong.sgf");
        expectedSgf=getSgfData(file);
    }
    @After public void tearDown() throws Exception {}
    @Test public void test() {
        Logging.setLevels(Level.WARNING);
        //System.out.println(expectedSgf);
        StringReader stringReader=new StringReader(expectedSgf);
        MNode games=MNode.restore(stringReader);
        //System.out.println("root: "+games);
        //System.out.println("children: "+games.children);
        model=new Model("oblong");
        model.setRoot(games); // does this really trash everything correctly?
        Board board=model.board();
        //if(board!=null) System.out.println("board is "+board.width()+"x"+board.depth());
        //else System.out.println("board is null");
        //System.out.println("before down");
        model.down(0);
        //System.out.println("after down");
        board=model.board();
        //if(board!=null) System.out.println("board is "+board.width()+"x"+board.depth());
        //else System.out.println("board is null");
    }
    String expectedSgf;
    SgfNode games;
    Model model;
}