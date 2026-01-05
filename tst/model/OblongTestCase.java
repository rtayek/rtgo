package model;
import static sgf.Parser.getSgfData;
import java.io.File;
import org.junit.*;
import equipment.Board;
import sgf.*;
public class OblongTestCase {
    @Before public void setUp() throws Exception {
        File file=new File("ogs/lecoblong.sgf");
        expectedSgf=getSgfData(file);
    }
    @After public void tearDown() throws Exception {}
    @Test public void test() {
        MNode games=SgfTestIo.restoreMNode(expectedSgf);
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