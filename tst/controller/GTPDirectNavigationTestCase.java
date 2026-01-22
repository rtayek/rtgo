package controller;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;
import equipment.*;
import utilities.MyTestWatcher;
// where does test for delete belong?
public class GTPDirectNavigationTestCase extends ControllerGtpTestSupport {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // maybe add tests that fail if trying to navigate somewhere that does not exist.
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    // add tests for tp and bottom.
    @Test public void testUp() throws Exception {
        ArrayList<String> strings=new ArrayList<>(startGameAndPlayOneMoveAtA1);
        strings.add(Command.tgo_up.name().toString());
        Response[] responses=runGtpCommands(strings);
        for(int i=0;i<responses.length;i++) assertTrue(responses[i].isOk());
        assertEquals("true",responses[responses.length-1].response);
        assertEquals(Stone.vacant,model.board().at(new Point(0,0)));
    }
    @Test public void testDown() throws Exception {
        ArrayList<String> strings=new ArrayList<>(startGameAndPlayOneMoveAtA1);
        strings.add(Command.tgo_up.name().toString());
        strings.add(Command.tgo_down.name().toString());
        Response[] responses=runGtpCommands(strings);
        for(int i=0;i<responses.length;i++) assertTrue(responses[i].isOk());
        assertEquals(Stone.black,model.board().at(new Point(0,0)));
    }
    @Test public void testLeft() throws Exception {
        ArrayList<String> strings=new ArrayList<>(startGameAndPlayOneMoveAtA1);
        strings.add(Command.tgo_up.name().toString());
        strings.add(Command.play.name().toString()+" black A2");
        strings.add(Command.tgo_left.name().toString()); // back to main line
        Response[] responses=runGtpCommands(strings);
        for(int i=0;i<responses.length;i++) assertTrue(responses[i].isOk());
        assertEquals("true",responses[responses.length-1].response);
        assertEquals(Stone.black,model.board().at(new Point(0,0)));
    }
    @Test public void testRight() throws Exception {
        ArrayList<String> strings=new ArrayList<>(startGameAndPlayOneMoveAtA1);
        strings.add(Command.tgo_up.name().toString());
        strings.add(Command.play.name().toString()+" black A2");
        strings.add(Command.tgo_left.name().toString()); // back to main line
        strings.add(Command.tgo_right.name().toString()); // variation
        Response[] responses=runGtpCommands(strings);
        for(int i=0;i<responses.length;i++) assertTrue(responses[i].isOk());
        assertEquals("true",responses[responses.length-1].response);
        assertEquals(Stone.black,model.board().at(new Point(0,1)));
    }
    public static final List<String> startGameAndPlayOneMoveAtA1=new ArrayList<>();
    static {
        startGameAndPlayOneMoveAtA1.add(Command.boardsize.name()+" "+Board.standard);
        startGameAndPlayOneMoveAtA1.add(Command.clear_board.name());
        startGameAndPlayOneMoveAtA1.add(Command.play.name()+" black A1");
    }
}
