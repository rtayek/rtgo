package controller;
import static org.junit.Assert.assertEquals;
import static sgf.Parser.getSgfData;
import java.io.StringReader;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import model.Model;
import sgf.*;
import utilities.MyTestWatcher;
// this may belong in the sgf package.
// this tests the custom gtp commands to send and receive sgf strings.
@RunWith(Parameterized.class) public class GTPDirectSendReceiveSgfTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        expectedSgf=getSgfData(key);
        //expectedSgf=Parser.options.removeUnwanted(expectedSgf);
        expectedSgf=Parser.options.prepareSgf(expectedSgf);
    }
    public GTPDirectSendReceiveSgfTestCase(String key) { this.key=key; }
    @After public void tearDown() throws Exception {}
    @Parameters public static Collection<Object[]> data() {
        //return Parser.sgfTestData(); // breaks some tests because key is string.
        return Parser.sgfData();
    }
    String getSgfFromModel(String expectedSgf) {
        original=new Model();
        original.restore(new StringReader(expectedSgf));
        String sendCommand=Command.tgo_send_sgf.name();
        GTPBackEnd gtpBackEnd=new GTPBackEnd(sendCommand,original);
        gtpBackEnd.useHexAscii=useHexAscii;
        String string=gtpBackEnd.runCommands(true);
        Response response=Response.response(string);
        String actualSgf=response.response;
        if(actualSgf.endsWith("\n\n"))
            actualSgf=actualSgf.substring(0,actualSgf.length()-2);
        if(useHexAscii) try {
            //Parser.printDifferences(expectedSgf,actualSgf);
            actualSgf=HexAscii.decodeToString(actualSgf);
        } catch(Exception e) {
            System.out.println("caught: "+e);
        }
        return actualSgf;
    }
    @Test public void testGetSgfFromModel() throws Exception {
        String actualSgf=getSgfFromModel(expectedSgf);
        //Parser.printDifferences(expectedSgf,actualSgf);
        assertEquals(key,expectedSgf,actualSgf);
    }
    String sendSgfToModel(String expectedSgf) {
        Model model=new Model();
        if(useHexAscii) expectedSgf=HexAscii.encode(expectedSgf.getBytes());
        String fromCommand=Command.tgo_receive_sgf.name()+" "+expectedSgf;
        GTPBackEnd gtpBackEnd=new GTPBackEnd(fromCommand,model);
        gtpBackEnd.useHexAscii=useHexAscii;
        String string=gtpBackEnd.runCommands(true);
        //String string=GTPBackEnd.runCommands(fromCommand,model,true);
        // see if this can be done without supplying the model like the real world.
        Response response=Response.response(string);
        String actualSgf=null;
        if(response.isOk()) {
            actualSgf=model.save();
            //actualSgf=options.remove(actualSgf);
        } else {
            // reinitialize expected
            if(false) throw new RuntimeException("?");
            //expectedSgf=options.remove(expectedSgf);
        }
        return actualSgf;
    }
    @Test public void testSendSgfToModel() throws Exception {
        String actualSgf=sendSgfToModel(expectedSgf);
        assertEquals(key,expectedSgf,actualSgf);
    }
    boolean useHexAscii=true;
    final String key;
    String expectedSgf;
    Model original;
}