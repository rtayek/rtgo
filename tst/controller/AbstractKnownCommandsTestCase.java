package controller;
import utilities.MyTestWatcher;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import io.Logging;
import model.Model;
import com.tayek.util.core.ParameterArray;
// test that almost all of the gtp commands are known and recognized.
public abstract class AbstractKnownCommandsTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @RunWith(Parameterized.class) public static class ParameterizedTestCase extends AbstractKnownCommandsTestCase {
        public ParameterizedTestCase(Command command) { this.command=command; }
        @Parameters public static Collection<Object[]> data() {
            Collection<Object> objects=Arrays.asList((Object[])Command.values());
            return ParameterArray.parameterize(objects);
        }
    }
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testThatCommandIsKnown() throws Exception {
        if(skipped.contains(command)) { Logging.mainLogger.severe("skipping command: "+command); return; }
        String response1=new GTPBackEnd(Command.known_command+" "+command.name(),directModel)
                .runCommands(directJustRun);
        Response response=Response.response(response1);
        assertTrue(command.name(),response.isOk());
    }
    @Test public void testThatCommandIsRecognized() throws Exception {
        if(skipped.contains(command)) { Logging.mainLogger.severe("skipping command: "+command); return; }
        if(command.equals(Command.tgo_receive_sgf)||command.equals(Command.tgo_goto_node)) {
            Logging.mainLogger.severe("skipping command: "+command);
            return;
        }
        String string=new GTPBackEnd(command.sample(),directModel).runCommands(directJustRun);
        Response response=Response.response(string);
        // need to add some example arguments
        if(response.isBad()) Logging.mainLogger.info(String.valueOf(response));
        assertTrue(command.name(),response.isOk());
        assertFalse(command.name(),response.response.startsWith(GTPBackEnd.unknownCommandMessage));
        assertFalse(command.name(),GTPBackEnd.unknownCommandMessage.equals(response.response));
    }
    boolean directJustRun=true;
    Set<Thread> initialActiveThreads;
    Command command;
    int ids;
    final Model directModel=new Model("model");
    boolean useHexAscii=true;
    //static EnumSet<Command> skipped=EnumSet.of(Command.genmove,Command.tgo_goto_node);
    static EnumSet<Command> skipped=EnumSet.of(Command.genmove);
    // why am i skipping genmove?
    // can i use this (skipped) to skip the receive and gto commands instead of explicitly mentioning them?
    //static EnumSet<Command> skipped=EnumSet.noneOf(Command.class);
}

