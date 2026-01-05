package sgf;
import static org.junit.Assert.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractSgfRoundTripTestCase extends AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // add setup and check alwaysPrepare
    //         if(alwaysPrepare) prepare();
    @Override @Before public void setUp() throws Exception { super.setUp(); if(!alwaysPrepare) prepare(); }
    private Boolean specialCases(String actualSgf) {
        Boolean ok=false; // no more assertions are needed
        if(expectedSgf.equals("")) expectedSgf=null; //11/29/22
        else if(expectedSgf.equals("()")) expectedSgf=null;
        if(expectedSgf==null) {
            if(actualSgf==null) ok=true;
            else if(actualSgf.equals("")) { actualSgf=null; ok=true; }
        } else {
            if(!expectedSgf.equals(actualSgf)) {
                if(expectedSgf.contains("PC[OGS: https://online-go.com/")) {
                    System.out.println("passing ogs file");
                    ok=true;
                }
            }
        }
        return ok;
    }
    @Test public void testSgfSaveAndRestore() throws Exception {
        // but does a restore first, then a deep equals on the trees.
        if(expectedSgf!=null) assertFalse(expectedSgf.contains("\n"));
        SgfNode expected=SgfTestIo.restore(expectedSgf);
        SgfNode actualSgf=SgfTestIo.saveAndRestore(expected);
        if(expected!=null) assertTrue(key.toString(),expected.deepEquals(actualSgf));
    }
    @Test public void testSgfRoundTrip() throws Exception {
        if(expectedSgf==null) return;
        String actualSgf=SgfRoundTrip.restoreAndSave(expectedSgf);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        if(actualSgf.length()==expectedSgf.length()+1) if(actualSgf.endsWith(")")) {
            System.out.println(key+"removing extra ')' "+actualSgf.length());
            if(true) throw new RuntimeException(key+"removing extra ')' "+actualSgf.length());
            actualSgf=actualSgf.substring(0,actualSgf.length()-1);
        }
        // how to do this more often?
        //Boolean ok=specialCases(actualSgf);
        //if(ok) return;
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Ignore @Test public void testSPreordergfRoundTrip() throws Exception {
        if(expectedSgf==null) return;
        if(expectedSgf.equals("")) return;
        expectedSgf=SgfNode.options.prepareSgf(expectedSgf);
        String actualSgf=SgfNode.preorderRouundTrip(expectedSgf);
        int p=Parser.parentheses(expectedSgf);
        if(p!=0) System.out.println(" bad parentheses: "+p);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testRSgfoundTripeTwice() throws Exception {
        if(expectedSgf!=null) assertFalse(expectedSgf.contains("\n"));
        boolean isOk=SgfTestIo.roundTripTwice(expectedSgf);
        assertTrue(key.toString(),isOk);
    }
    @Test public void testSgfCannonical() {
        if(expectedSgf!=null) assertFalse(expectedSgf.contains("\n"));
        String actualSgf=SgfRoundTrip.restoreAndSave(expectedSgf);
        String actual2=SgfRoundTrip.restoreAndSave(actualSgf);
        assertEquals(key.toString(),actualSgf,actual2);
    }
}
