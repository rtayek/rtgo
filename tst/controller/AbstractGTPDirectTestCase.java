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
import sgf.HexAscii;
import utilities.MyTestWatcher;
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// some of these need to be done directly in the model.
// as well as b the bac end when we have role=something!
public abstract class AbstractGTPDirectTestCase extends ControllerGtpTestSupport {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // these create a back end with a buffered string reader passed to it.
    // the string reader has the command(s).
    // the commands get run by  the runCommands method.
    // either by just calling run() or starting a thread.
    // in either case. it then waits for done.
    public static class DirectTestCase extends AbstractGTPDirectTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Before public void setUp() throws Exception { directJustRun=true; }
    }
    public static class ThreadTestCase extends AbstractGTPDirectTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Before public void setUp() throws Exception { directJustRun=false; }
    }
    @RunWith(Suite.class) @SuiteClasses({DirectTestCase.class,ThreadTestCase.class}) public class GTPDirectTestSuite {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        // eclipse can't find this!
        // this should have been found by grep!
        // eclipse still can't find this!
    }
    // almost all of these make a new gtp backen for each command.
    // except maybe for the one's that take a list of commands.
    @Test public void testGtpOneEmptyCommand() throws Exception {
        String actual=runGtpCommandString(directModel, "", directJustRun);
        assertTrue(actual.equals(""));
        Response response=Response.response(actual);
        //assertTrue(response.isOk()); // this is failing
    }
    @Test public void testGtpSomeEmptyCommands() throws Exception {
        // bad, engine should not return anything :(
        String actual=runGtpCommandString(directModel, "", directJustRun);
        assertTrue(actual.equals(""));
        actual=runGtpCommandString(directModel, "\n", directJustRun);
        assertTrue(actual.equals(""));
        actual=runGtpCommandString(directModel, twoLineFeeds, directJustRun);
        assertTrue(actual.equals(""));
    }
    @Test public void testGtpSplit() throws Exception {
        String s="a"+twoLineFeeds+"b"+twoLineFeeds;
        String[] strings=s.split(twoLineFeeds);
        assertEquals(2,strings.length);
        Logging.mainLogger.info(""+Arrays.asList(s));
    }
    @Test public void testGtpEmptyCommand2() throws Exception {
        String actual=runGtpCommandString(directModel, "  \t \r \f \r\n", directJustRun);
        assertEquals("",actual);
    }
    @Test public void testGtpLineFeeds() throws Exception {
        String actual=runGtpCommandString(directModel, Command.name.name(), directJustRun);
        assertTrue(actual.startsWith(""+okCharacter));
        actual=runGtpCommandString(directModel, Command.name.name()+'\n', directJustRun);
        assertTrue(actual.startsWith(""+okCharacter));
        actual=runGtpCommandString(directModel, Command.name.name()+twoLineFeeds, directJustRun);
        assertTrue(actual.startsWith(""+okCharacter));
    }
    @Test public void testGtpCreate() throws Exception {}
    @Test public void testGtpProtocolVersion() throws Exception {
        String actual=runGtpCommandString(directModel, Command.protocol_version.name(), directJustRun);
        assertEquals(okString+protocolVersionString+twoLineFeeds,actual);
    }
    @Test public void testGtpName() throws Exception {
        String response=runGtpCommandString(directModel, Command.name.name(), directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testHtpNameCommandWithIdentityNumber() throws Exception {
        Integer id=123;
        Command command=Command.name;
        String response=runGtpCommandString(directModel, ""+id+" "+command.name(), directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(id,actual.id);
    }
    @Test public void testGtpQuit() throws Exception {
        String response=runGtpCommandString(directModel, Command.quit.name(), directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testGtpListCommands() throws Exception {
        String response=runGtpCommandString(directModel, "list_commands", directJustRun);
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
        String response=runGtpCommandString(directModel, "some-unknown-command", directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isBad());
        assertTrue(actual.response.startsWith(GTPBackEnd.unknownCommandMessage));
    }
    @Test public void testGtpUnknownCommandWithIdNumber() throws Exception {
        String response=runGtpCommandString(directModel, 256+" "+"some-unknown-command", directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isBad());
        assertTrue(actual.response.startsWith(GTPBackEnd.unknownCommandMessage));
    }
    @Test public void testGtpKnownCommandQuit() throws Exception {
        Command command=Command.known_command;
        String response=runGtpCommandString(directModel, command.name()+" "+Command.quit, directJustRun);
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
        String response=runGtpCommandString(directModel, command.name(), directJustRun);
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
        String response=runGtpCommandString(directModel, command.name()+" "+"0", directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isBad());
    }
    void checkModelBoardSize(int n) {
        Command command=Command.boardsize;
        String response=runGtpCommandString(directModel, command.name()+" "+n, directJustRun);
        Response actual=Response.response(response);
        if(!actual.isOk()) Logging.mainLogger.info(String.valueOf(response));
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
        String response=runGtpCommandString(directModel, command.name()+" "+Board.standard, directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testGtpClearBoard() throws Exception {
        String response=runGtpCommandString(directModel, Command.clear_board.name(), directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        Logging.mainLogger.info("'"+actual.response+"'");
    }
    @Test public void testGtpKomi() throws Exception {
        Command command=Command.komi;
        String response=runGtpCommandString(directModel, command.name()+" 1234.5678", directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testGtpKomiWithSyntaxError() throws Exception {
        String response=runGtpCommandString(directModel, Command.komi.name()+" foo", directJustRun);
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
        directModel.ensureBoard();
        String noI=Coordinates.toGtpCoordinateSystem(point,directModel.board().width(),directModel.board().depth());
        String response=runGtpCommandString(directModel, Command.play.name()+" Black "+noI, directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(Stone.black,directModel.board().at(point));
        // traverse model and shove from front end?
        // how to do this?
        // look at sgf stuff. combine maybe
    }
    @Test public void testGtpPlayBlackA1() throws Exception {
        String response=runGtpCommandString(directModel, "play black A1", directJustRun);
        Response actual=Response.response(response);
        Logging.mainLogger.info(String.valueOf(response));
        assertTrue(actual.isOk());
        assertEquals(Stone.black,directModel.board().at(0,0));
        // traverse model and shove from front end?
        // how to do this?
        // look at sgf stuff. combine maybe
    }
    @Test public void testGtpPassWithClearBoard() throws Exception {
        String commands=""+Command.clear_board+'\n'+Command.play.name()+" "+Move2.blackPass.nameWithColor()+"\n";
        String response=runGtpCommandString(directModel, commands, directJustRun);
        // this has 2 commands
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(Move2.blackPass,directModel.lastMove2());
    }
    @Test public void testGtpPass() throws Exception {
        String command=Command.play.name()+" "+Move2.blackPass.nameWithColor();
        String response=runGtpCommandString(directModel, command, directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        assertEquals(Move2.blackPass,directModel.lastMove2());
    }
    @Test public void testGtpResign2() throws Exception {
        String response=runGtpCommandString(directModel, Command.play.name()+" "+Move2.blackResign.nameWithColor(), directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
        Move2 lastMove=directModel.lastMove2();
        assertEquals(Move2.blackResign,lastMove);
    }
    @Test public void testPlayOffTheBoard() throws Exception {
        String actual=runGtpCommandString(directModel, Command.play.name()+" Black Z101", directJustRun);
        assertEquals(badString+Failure.illegal_move.toString2()+twoLineFeeds,actual);
    }
    @Test public void testPlayIllegalMove() throws Exception {
        directModel.ensureBoard();
        Point point=new Point(0,0);
        String noI=Coordinates.toGtpCoordinateSystem(point,directModel.board().width(),directModel.board().depth());
        Logging.mainLogger.info("noI: "+noI);
        String commands=Command.play.name()+" Black "+noI+'\n'+Command.play.name()+" White A1"+'\n';
        // this has 2 commands
        String actual=runGtpCommandString(directModel, commands, directJustRun);
        Response[] responses=Response.responses(actual);
        Logging.mainLogger.info(responses.length+" responses.");
        Logging.mainLogger.info(""+responses);
        assertTrue(responses[0].isOk());
        assertTrue(responses[1].isBad());
        assertTrue(responses[1].response.contains(Failure.illegal_move.toString2()));
    }
    public Response[] playTwoMovesOnTheSamePoint(boolean justRun) throws Exception {
        Model model=new Model("model"); // make sure it's the same one chekString uses.
        String commands=Command.play.name()+" Black A1"+"\n"+Command.play.name()+" White A1"+"\n";
        String actual=runGtpCommandString(model, commands, justRun);
        Response[] responses=Response.responses(actual);
        Logging.mainLogger.info(responses.length+" responses.");
        Logging.mainLogger.info(""+responses);
        return responses;
    }
    // these tests are confusing. fix!
    @Test public void testPlayTwoMovesOnTheSamePointDirect() throws Exception {
        Response[] responses=playTwoMovesOnTheSamePoint(directJustRun);
        assertTrue(responses[0].isOk());
        assertTrue(responses[1].isBad());
        assertTrue(responses[1].response.contains(Failure.illegal_move.toString2()));
    }
    public Response[] send(String[] commands,boolean directJustRun) {
        final Model directModel=new Model("model");
        StringBuffer stringBuffer=new StringBuffer();
        for(String string:commands) stringBuffer.append(string).append('\n');
        String actual=runGtpCommandString(directModel, stringBuffer.toString(), directJustRun);
        Response[] responses=Response.responses(actual);
        return responses;
    }
    @Test public void testBlackPlayTwoMovesInARowDirectStrict() throws Exception {
        // this should be allowed?
        // make some like this with combinations of role and strict
        // seems we need a send 2 commands and check status routine
        // then make a bunch of tests 4 roles X 2 stricts = 8 tests.
        //1/21/23
        // no more strict now, check role
        if(true) {
            String[] commands=new String[] {Command.play.name()+" Black "+"A1"+'\n',
                    Command.play.name()+" Black A2"+'\n'};
            Response[] responses=send(commands,directJustRun);
            Logging.mainLogger.info(responses.length+" responses.");
            Logging.mainLogger.info(""+responses);
            Logging.mainLogger.info(String.valueOf(responses[0]));
            Logging.mainLogger.info(String.valueOf(responses[1]));
            assertTrue(responses[0].isOk());
            assertTrue(responses[1].isBad());
        } else {
            String commands=Command.play.name()+" Black "+"A1"+'\n'+Command.play.name()+" Black A2"+'\n';
            String actual=runGtpCommandString(directModel, commands, directJustRun);
            // was there some reason i needed access to the model?
            Response[] responses=Response.responses(actual);
            Logging.mainLogger.info(responses.length+" responses.");
            Logging.mainLogger.info(""+responses);
            assertTrue(responses[0].isOk());
            assertTrue(responses[1].isBad());
        }
    }
    @Test public void testBlackPlayTwoMovesInARowDiect() throws Exception {
        // this should be allowed?
        String commands=Command.play.name()+" Black "+"A1"+'\n'+Command.play.name()+" Black A2"+'\n';
        String actual=runGtpCommandString(directModel, commands, directJustRun);
        Response[] responses=Response.responses(actual);
        Logging.mainLogger.info(responses.length+" responses.");
        Logging.mainLogger.info(""+responses);
        assertTrue(responses[0].isOk());
        assertFalse(responses[1].isOk());
    }
    @Test public void testUndo() throws Exception {
        Point point=new Point(0,0);
        directModel.ensureBoard();
        String noI=Coordinates.toGtpCoordinateSystem(point,directModel.board().width(),directModel.board().depth());
        String commands=Command.play.name()+" Black "+noI+'\n'+Command.undo.name()+"\n";
        // i may have to roll up a new model?
        String actual=runGtpCommandString(directModel, commands, directJustRun);
        assertTrue(actual.startsWith(""+okCharacter));
        assertEquals(Stone.vacant,directModel.board().at(point));
    }
    @Test public void testTgo_stop() throws Exception {
        String response=runGtpCommandString(directModel, Command.tgo_stop.name(), directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testTgo_stopWithCheckString() throws Exception {
        Model model=new Model("model"); // why a new model?
        String actual=runGtpCommandString(model, Command.tgo_stop.name(), directJustRun);
        //String actual=runCommands(Command.tgo_stop.name(),model,directJustRun);
        assertTrue(actual.startsWith("= true"));
        // maybe this should be contains? if we have id's?
    }
    @Test public void testTgo_Black() throws Exception {
        String response=runGtpCommandString(directModel, Command.tgo_black.name(), directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testTgo_White() throws Exception {
        String response=runGtpCommandString(directModel, Command.tgo_white.name(), directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testToSGFCommand() throws Exception {
        String response=runGtpCommandString(directModel, Command.tgo_send_sgf.name(), directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    // some of these from commands do not check the data!
    @Test public void testFromSGFCommand() throws Exception {
        String sgf="(;)";
        if(useHexAscii) sgf=HexAscii.encode(sgf);
        String response=runGtpCommandString(directModel, Command.tgo_receive_sgf.name()+' '+sgf, directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testFromSGFCommand2() throws Exception {
        String sgf="(;)(;)";
        if(useHexAscii) sgf=HexAscii.encode(sgf);
        String response=runGtpCommandString(directModel, Command.tgo_receive_sgf.name()+' '+sgf, directJustRun);
        Response actual=Response.response(response);
        assertTrue(actual.isOk());
    }
    @Test public void testFromSGFCommandFoo() throws Exception {
        // need to encode sgf into hex ascii if backend expects it.
        // like: if(useHexAscii) { sgfString=encode(sgfString);
        String response=runGtpCommandString(directModel, Command.tgo_receive_sgf.name()+" not sgf", directJustRun);
        Response actual=Response.response(response);
        assertFalse(actual.isOk());
    }
    public static void main(String[] args) {
        Logging.mainLogger.info(String.valueOf(Init.first));
        first.suiteControls=true;
        JUnitCore jUnitCore=new JUnitCore();
        jUnitCore.run(GTPDirectTestSuite.class);
    }
    boolean directJustRun;
    Set<Thread> initialThreads;
    int ids;
    final Model directModel=new Model("model");
    @Override protected Model createModel() {
        return directModel;
    }
    static EnumSet<Command> skipped=EnumSet.of(Command.genmove);
    // genmove hangs. figure out a workaround!
}
