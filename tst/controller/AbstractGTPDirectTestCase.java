package controller;
import static controller.GTPBackEnd.*;
import static io.Init.first;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import equipment.*;
import io.*;
import model.*;
import utilities.MyTestWatcher;
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractGTPDirectTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // these create a back end with a buffered string reader passed to it.
    // the string reader has the command(s).
    // the commands get run by  the runCommands method.
    // either by just calling run() or starting a thread.
    // in either case. it then waits for done.
    public static class RunTestCase extends AbstractGTPDirectTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Override @Before public void setUp() throws Exception { super.setUp(); directJustRun=true; }
        @Override @After public void tearDown() throws Exception { super.tearDown(); }
    }
    public static class ThreadTestCase extends AbstractGTPDirectTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Override @Before public void setUp() throws Exception { super.setUp(); directJustRun=false; }
        @Override @After public void tearDown() throws Exception { super.tearDown(); }
    }
    @RunWith(Suite.class) @SuiteClasses({RunTestCase.class,ThreadTestCase.class}) public class GTPDirectTestSuite {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        // eclipse can't find this!
        // this should have been found by grep!
        // eclipse still can't find this!
    }
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    // almost all of these make a new gtp backen for each command.
    // except maybe for the one's that take a list of commands.
    @Test public void testGtpOneEmptyCommand() throws Exception {
        String actual=new GTPBackEnd("",directModel).runCommands(directJustRun);
        assertTrue(actual.equals(""));
        Response response=Response.response(actual);
        //assertTrue(response.isOk()); // this is failing
    }
    @Test public void testGtpSomeEmptyCommands() throws Exception {
        // bad, engine should not return anything :(
        String actual=new GTPBackEnd("",directModel).runCommands(directJustRun);
        assertTrue(actual.equals(""));
        actual=new GTPBackEnd("\n",directModel).runCommands(directJustRun);
        assertTrue(actual.equals(""));
        actual=new GTPBackEnd(twoLineFeeds,directModel).runCommands(directJustRun);
        assertTrue(actual.equals(""));
    }
    @Test public void testGtpSplit() throws Exception {
        String s="a"+twoLineFeeds+"b"+twoLineFeeds;
        String[] strings=s.split(twoLineFeeds);
        assertEquals(2,strings.length);
        Logging.mainLogger.info(""+Arrays.asList(s));
    }
    @Test public void testGtpEmptyCommand2() throws Exception {
        String actual=new GTPBackEnd("  \t \r \f \r\n",directModel).runCommands(directJustRun);
        assertEquals("",actual);
    }
    @Test public void testGtpLineFeeds() throws Exception {
        String actual=new GTPBackEnd(Command.name.name(),directModel).runCommands(directJustRun);
        assertTrue(actual.startsWith(""+okCharacter));
        actual=new GTPBackEnd(Command.name.name()+'\n',directModel).runCommands(directJustRun);
        assertTrue(actual.startsWith(""+okCharacter));
        actual=new GTPBackEnd(Command.name.name()+twoLineFeeds,directModel).runCommands(directJustRun);
        assertTrue(actual.startsWith(""+okCharacter));
    }
    @Test public void testGtpCreate() throws Exception {}
    @Test public void testGtpProtocolVersion() throws Exception {
        String actual=new GTPBackEnd(Command.protocol_version.name(),directModel).runCommands(directJustRun);
        assertEquals(okString+protocolVersionString+twoLineFeeds,actual);
    }
    @Test public void testGtpName() throws Exception {
        String response=new GTPBackEnd(Command.name.name(),directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testHtpNameCommandWithIdentityNumber() throws Exception {
        Integer id=123;
        Command command=Command.name;
        String response=new GTPBackEnd(""+id+" "+command.name(),directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(id,actual.id);
    }
    @Test public void testGtpQuit() throws Exception {
        String response=new GTPBackEnd(Command.quit.name(),directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testGtpListCommands() throws Exception {
        String response=new GTPBackEnd("list_commands",directModel).runCommands(directJustRun);
        // why isn't this a command?
        Response actual=Response.response(response); // why not in command?
        Command[] values=Command.values();
        if(Response.checkForTwoLineFeeds(actual.response)) {
            Logging.mainLogger.warning("removing line feeds from response!");
            actual.response=actual.response.substring(0,actual.response.length()-2);
        }
        assertTrue(actual.response.endsWith(values[values.length-1].name()));
        assertTrue(actual.response.startsWith('\n'+values[0].name()+"\n"));
        for(Command command:values) if(!command.equals(Command.values()[Command.values().length-1]))
            assertTrue(actual.response.contains(command.name()+'\n'));
        else assertTrue(actual.response.endsWith(command.name()));
    }
    @Test public void testGypUnknownCommand() throws Exception {
        String response=new GTPBackEnd("some-unknown-command",directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isBad());
        assertTrue(actual.response.startsWith(GTPBackEnd.unknownCommandMessage));
    }
    @Test public void testGtpUnknownCommandWithIdNumber() throws Exception {
        String response=new GTPBackEnd(256+" "+"some-unknown-command",directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isBad());
        assertTrue(actual.response.startsWith(GTPBackEnd.unknownCommandMessage));
    }
    @Test public void testGtpKnownCommandQuit() throws Exception {
        Command command=Command.known_command;
        String response=new GTPBackEnd(command.name()+" "+Command.quit,directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        if(Response.checkForTwoLineFeeds(actual.response)) {
            Logging.mainLogger.warning("removing line feeds from response!");
            actual.response=actual.response.substring(0,actual.response.length()-2);
        }
        assertEquals("true",actual.response);
    }
    @Test public void testGtpKnownCommandWithMissingArgument() throws Exception {
        Command command=Command.known_command;
        String response=new GTPBackEnd(command.name(),directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isBad());
        if(Response.checkForTwoLineFeeds(actual.response)) {
            Logging.mainLogger.warning("removing line feeds from response!");
            actual.response=actual.response.substring(0,actual.response.length()-2);
        }
        assertEquals(Failure.syntax_error.toString2(),actual.response);
    }
    @Test public void testGtpBoardsize0() throws Exception {
        Command command=Command.boardsize;
        String response=new GTPBackEnd(command.name()+" "+"0",directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isBad());
    }
    void checkModelBoardSize(int n) {
        Command command=Command.boardsize;
        String response=new GTPBackEnd(command.name()+" "+n,directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        if(!actual.isOk()) System.out.println(response);
        assertTrue(actual.isOk());
        assertEquals(directModel.board().width(),n);
        assertEquals(directModel.board().depth(),n);
    }
    @Test public void testGtpBoardsize1() throws Exception { checkModelBoardSize(1); }
    @Test public void testGtpBoardsize2() throws Exception { checkModelBoardSize(2); }
    @Test public void testGtpBoardsize3() throws Exception { checkModelBoardSize(3); }
    @Test public void testGtpBoardsize9() throws Exception { checkModelBoardSize(9); }
    @Test public void testGtpBoardsize13() throws Exception { checkModelBoardSize(13); }
    @Test public void testGtpBoardsize19() throws Exception { checkModelBoardSize(19); }
    @Test public void testGtpBoardsize21() throws Exception { checkModelBoardSize(21); }
    @Test public void testGtpBoardsize23() throws Exception { checkModelBoardSize(23); }
    @Test public void testGtpBoardsize25() throws Exception { checkModelBoardSize(25); }
    //@Test public void testBoardsize26() throws Exception { checkModelBoardSize(26); }
    //@Test public void testBoardsize27() throws Exception { checkModelBoardSize(27); }
    //@Test public void testBoardsize37() throws Exception { checkModelBoardSize(37); }
    @Test public void testSGtptandardBoardsize() throws Exception {
        Command command=Command.boardsize;
        String response=new GTPBackEnd(command.name()+" "+Board.standard,directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testGtpClearBoard() throws Exception {
        String response=new GTPBackEnd(Command.clear_board.name(),directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        Logging.mainLogger.info("'"+actual.response+"'");
    }
    @Test public void testGtpKomi() throws Exception {
        Command command=Command.komi;
        String response=new GTPBackEnd(command.name()+" 1234.5678",directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testGtpKomiWithSyntaxError() throws Exception {
        String response=new GTPBackEnd(Command.komi.name()+" foo",directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isBad());
        if(Response.checkForTwoLineFeeds(actual.response)) {
            Logging.mainLogger.warning("removing line feeds from response!");
            actual.response=actual.response.substring(0,actual.response.length()-2);
        }
        assertEquals(Failure.syntax_error.toString2(),actual.response);
    }
    @Test public void testGtpPlayOneMove() throws Exception {
        // assumes that we can make a move with no setup (i.e. no board yet)
        Point point=new Point(0,0);
        String noI=Coordinates.toGtpCoordinateSystem(point,directModel.board().width(),directModel.board().depth());
        String response=new GTPBackEnd(Command.play.name()+" Black "+noI,directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(Stone.black,directModel.board().at(point));
        // traverse model and shove from front end?
        // how to do this?
        // look at sgf stuff. combine maybe
    }
    @Test public void testGtpPlayBlackA1() throws Exception {
        String response=new GTPBackEnd("play black A1",directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(Stone.black,directModel.board().at(0,0));
        // traverse model and shove from front end?
        // how to do this?
        // look at sgf stuff. combine maybe
    }
    @Test public void testGtpPassWithClearBoard() throws Exception {
        String commands=""+Command.clear_board+'\n'+Command.play.name()+" "+Move.blackPass.nameWithColor()+"\n";
        String response=new GTPBackEnd(commands,directModel).runCommands(directJustRun);
        // this has 2 commands
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(Move.blackPass,directModel.lastMove());
    }
    @Test public void testGtpPass() throws Exception {
        String command=Command.play.name()+" "+Move.blackPass.nameWithColor();
        String response=new GTPBackEnd(command,directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(Move.blackPass,directModel.lastMove());
    }
    @Test public void testGtpResign2() throws Exception {
        String response=new GTPBackEnd(Command.play.name()+" "+Move.blackResign.nameWithColor(),directModel)
                .runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(Move.blackResign,directModel.lastMove());
    }
    @Test public void testPlayOffTheBoard() throws Exception {
        String actual=new GTPBackEnd(Command.play.name()+" Black Z101",directModel).runCommands(directJustRun);
        assertEquals(badString+Failure.illegal_move.toString2()+twoLineFeeds,actual);
    }
    @Test public void testPlayIllegalMove() throws Exception {
        System.out.println("directJustRun: "+directJustRun);
        Point point=new Point(0,0);
        String noI=Coordinates.toGtpCoordinateSystem(point,directModel.board().width(),directModel.board().depth());
        String commands=Command.play.name()+" Black "+noI+'\n'+Command.play.name()+" White A1"+'\n';
        // this has 2 commands
        String actual=new GTPBackEnd(commands,directModel).runCommands(directJustRun);
        Response[] responses=Response.responses(actual);
        Logging.mainLogger.info(responses.length+" responses.");
        Logging.mainLogger.info(""+responses);
        assertTrue(responses[0].isOk());
        assertTrue(responses[1].isBad());
        assertTrue(responses[1].response.contains(Failure.illegal_move.toString2()));
    }
    public static Response[] playTwoMovesOnTheSamePoint(boolean justRun) throws Exception {
        Model model=new Model("model"); // make sure it's the same one chekString uses.
        String commands=Command.play.name()+" Black A1"+"\n"+Command.play.name()+" White A1"+"\n";
        String actual=new GTPBackEnd(commands,model).runCommands(justRun);
        Response[] responses=Response.responses(actual);
        Logging.mainLogger.info(responses.length+" responses.");
        Logging.mainLogger.info(""+responses);
        return responses;
    }
    // these tests are confusing. fix!
    @Test public void testPlayTwoMovesDirect() throws Exception {
        Response[] responses=playTwoMovesOnTheSamePoint(directJustRun);
        assertTrue(responses[0].isOk());
        assertTrue(responses[1].isBad());
        assertTrue(responses[1].response.contains(Failure.illegal_move.toString2()));
    }
    @Test public void testBlackPlayTwoMovesInARowDirectStrict() throws Exception {
        // this should be allowed?
        directModel.strict=true;
        String commands=Command.play.name()+" Black "+"A1"+'\n'+Command.play.name()+" Black A2"+'\n';
        String actual=new GTPBackEnd(commands,directModel).runCommands(directJustRun);
        Response[] responses=Response.responses(actual);
        Logging.mainLogger.info(responses.length+" responses.");
        Logging.mainLogger.info(""+responses);
        assertTrue(responses[0].isOk());
        assertTrue(responses[1].isBad());
    }
    @Test public void testBlackPlayTwoMovesInARowDiect() throws Exception {
        // this should be allowed?
        directModel.strict=false;
        String commands=Command.play.name()+" Black "+"A1"+'\n'+Command.play.name()+" Black A2"+'\n';
        String actual=new GTPBackEnd(commands,directModel).runCommands(directJustRun);
        Response[] responses=Response.responses(actual);
        Logging.mainLogger.info(responses.length+" responses.");
        Logging.mainLogger.info(""+responses);
        assertTrue(responses[0].isOk());
        assertTrue(responses[1].isOk());
    }
    @Test public void testUndo() throws Exception {
        Point point=new Point(0,0);
        String noI=Coordinates.toGtpCoordinateSystem(point,directModel.board().width(),directModel.board().depth());
        String commands=Command.play.name()+" Black "+noI+'\n'+Command.undo.name()+"\n";
        // i may have to roll up a new model?
        String actual=new GTPBackEnd(commands,directModel).runCommands(directJustRun);
        assertTrue(actual.startsWith(""+okCharacter));
        assertEquals(Stone.vacant,directModel.board().at(point));
    }
    @Test public void testTgo_stop() throws Exception {
        String response=new GTPBackEnd(Command.tgo_stop.name(),directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testTgo_stopWithCheckString() throws Exception {
        Model model=new Model("model"); // why a new model?
        String actual=new GTPBackEnd(Command.tgo_stop.name(),model).runCommands(directJustRun);
        //String actual=runCommands(Command.tgo_stop.name(),model,directJustRun);
        assertTrue(actual.startsWith("= true"));
        // maybe this should be contains? if we have id's?
    }
    @Test public void testTgo_Black() throws Exception {
        String response=new GTPBackEnd(Command.tgo_black.name(),directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testTgo_White() throws Exception {
        String response=new GTPBackEnd(Command.tgo_white.name(),directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testToSGFCommand() throws Exception {
        String response=new GTPBackEnd(Command.tgo_send_sgf.name(),directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    // some of these from commands do not check the data!
    @Test public void testFromSGFCommand() throws Exception {
        String response=new GTPBackEnd(Command.tgo_receive_sgf.name()+" (;)",directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testFromSGFCommand2() throws Exception {
        String response=new GTPBackEnd(Command.tgo_receive_sgf.name()+" (;)(;)",directModel).runCommands(directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testFromSGFCommandFoo() throws Exception {
        String response=new GTPBackEnd(Command.tgo_receive_sgf.name()+" not sgf",directModel)
                .runCommands(directJustRun);
        Response actual=Response.response(response);
        assertFalse(actual.isOk());
    }
    public static void main(String[] args) {
        System.out.println(Init.first);
        first.suiteControls=true;
        JUnitCore jUnitCore=new JUnitCore();
        jUnitCore.run(GTPDirectTestSuite.class);
    }
    boolean directJustRun;
    Set<Thread> initialThreads;
    int ids;
    final Model directModel=new Model("model");
    static EnumSet<Command> skipped=EnumSet.of(Command.genmove);
    // genmove hangs. figure out a workaround!
}
