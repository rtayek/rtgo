package controller;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import java.io.StringReader;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import model.*;
import sgf.*;
import utilities.*;
// this may belong in the sgf package.
// this tests the custom gtp commands to send and receive sgf strings.
@RunWith(Parameterized.class) public class GTPDirectSendReceiveSgfTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        expectedSgf=getSgfData(key);
        expectedSgf=SgfNode.options.prepareSgf(expectedSgf);
    }
    public GTPDirectSendReceiveSgfTestCase(Object key) { this.key=key; }
    @After public void tearDown() throws Exception {}
    @Parameters public static Collection<Object[]> data() {
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(sgfDataKeySet());
        objects.addAll(sgfFiles());
        return ParameterArray.parameterize(objects);
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
        if(actualSgf.endsWith("\n\n")) actualSgf=actualSgf.substring(0,actualSgf.length()-2);
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
        actualSgf=SgfNode.options.prepareSgf(actualSgf);
        //Parser.printDifferences(expectedSgf,actualSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    String sendSgfToModel(String expectedSgf,Model model) {
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
            if(true) throw new RuntimeException("?");
            //expectedSgf=options.remove(expectedSgf);
        }
        return actualSgf;
    }
    @Test public void testSendSgfToModel() throws Exception {
        Model model=new Model();
        String actualSgf=sendSgfToModel(expectedSgf,model);
        assertTrue(model.currentNode().children.size()>0);
        assertTrue(Navigate.down.canDo(model));
        //System.out.println(model);
        actualSgf=SgfNode.options.prepareSgf(actualSgf);
        assertEquals(key.toString().toString(),expectedSgf,actualSgf);
    }
    boolean useHexAscii=true;
    final Object key;
    String expectedSgf;
    Model original;
}