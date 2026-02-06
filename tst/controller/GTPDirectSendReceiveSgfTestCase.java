package controller;
import io.Logging;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import model.*;
import sgf.*;
import utilities.*;
// this may belong in the sgf package.
// this tests the custom gtp commands to send and receive sgf strings.
@RunWith(Parameterized.class) public class GTPDirectSendReceiveSgfTestCase extends ControllerGtpTestSupport {
    private static final boolean verbose=false;
    @Before public void setUp() throws Exception {
        expectedSgf=getSgfData(key);
        expectedSgf=SgfHarness.prepareExpectedSgf(key,expectedSgf);
    }
    public GTPDirectSendReceiveSgfTestCase(Object key) { this.key=key; }
    @After public void tearDown() throws Exception {}
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(sgfDataKeySet());
        objects.addAll(sgfFiles());
        return ParameterArray.parameterize(objects);
    }
    String getSgfFromModel(String expectedSgf) {
        original=new Model();
        SgfHarness.restore(original,expectedSgf);
        String sendCommand=Command.tgo_send_sgf.name();
        String string=runGtpCommandString(original,sendCommand);
        Response response=Response.response(string);
        String actualSgf=response.response;
        if(actualSgf.endsWith("\n\n")) actualSgf=actualSgf.substring(0,actualSgf.length()-2);
        if(useHexAscii) try {
            //Parser.printDifferences(expectedSgf,actualSgf);
            actualSgf=HexAscii.decodeToString(actualSgf);
        } catch(Exception e) {
            Logging.mainLogger.info("caught: "+e);
        }
        return actualSgf;
    }
    private void logSgfComparison(String expected,String actual) {
        if(!verbose) return;
        Logging.mainLogger.info("expectedSgf:\n"+expected);
        Logging.mainLogger.info("actualSgf:\n"+actual);
    }
    @Test public void testGetSgfFromModel() throws Exception {
        String actualSgf=getSgfFromModel(expectedSgf);
        actualSgf=SgfNode.options.prepareSgf(actualSgf);
        //Parser.printDifferences(expectedSgf,actualSgf);
        logSgfComparison(expectedSgf,actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    String sendSgfToModel(String expectedSgf,Model model) {
        if(useHexAscii) expectedSgf=HexAscii.encode(expectedSgf.getBytes());
        String fromCommand=Command.tgo_receive_sgf.name()+" "+expectedSgf;
        String string=runGtpCommandString(model,fromCommand);
        //String string=GTPBackEnd.runCommands(fromCommand,model,true);
        // see if this can be done without supplying the model like the real world.
        Response response=Response.response(string);
        String actualSgf=null;
        if(response.isOk()) {
            actualSgf=model.save();
            //actualSgf=options.remove(actualSgf);
        } else {
            // reinitialize expected
            if(true) throw new RuntimeException("?");
            //expectedSgf=options.remove(expectedSgf);
        }
        return actualSgf;
    }
    @Test public void testSendSgfToModel() throws Exception {
        Model model=new Model();
        String actualSgf=sendSgfToModel(expectedSgf,model);
        assertTrue(model.currentNode().children().size()>0);
        assertTrue(Navigate.down.canDo(model));
        //Logging.mainLogger.info(model);
        actualSgf=SgfNode.options.prepareSgf(actualSgf);
        logSgfComparison(expectedSgf,actualSgf);
        assertEquals(key.toString().toString(),expectedSgf,actualSgf);
    }
    final Object key;
    String expectedSgf;
    Model original;
}

