package io;
import static io.Constants.lineSeparator;
import static org.junit.Assert.assertEquals;
import java.io.*;
import java.util.logging.LogManager;
import org.junit.*;
import utilities.MyTestWatcher;
public class TeeTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // https://stackoverflow.com/questions/27825682/flushing-streamhandlers-during-debugging-using-java-util-logging-autoflush
    // https://gist.github.com/jiayao/36606389023f67fd5278d2d18656d388
    @Before public void setUp() throws Exception {
        byteArrayOutputStream.reset();
        LogManager.getLogManager().reset(); // can i really get rid of this?
    }
    @After public void tearDown() throws Exception { System.setOut(sysout); System.setErr(syserr); }
    @Test public void testTeesPrintStream() {
        Tee tee=new Tee(byteArrayOutputStream);
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        String actual=byteArrayOutputStream.toString();
        //sysout.println("expected: '"+expected+"'");
        //sysout.println("actual:   '"+actual+"'");
        assertEquals(expected,actual);
    }
    @Test public void testSetOut() {
        Tee tee=new Tee(byteArrayOutputStream);
        //tee.addOutputStream(System.out);
        //tee.addOutputStream(System.err);
        tee.setOut();
        assertEquals(tee.printStream,System.out);
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        // if we add sysout and syserr
        // then it also shows up in sysout and syserr .
        String actual=byteArrayOutputStream.toString();
        //sysout.println("expected: '"+expected+"'");
        //sysout.println("actual:   '"+actual+"'");
        assertEquals(expected,actual);
    }
    @Test public void testSetErr() {
        Tee tee=new Tee(byteArrayOutputStream);
        tee.setErr();
        assertEquals(tee.printStream,System.err);
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        String actual=byteArrayOutputStream.toString();
        //sysout.println("expected: '"+expected+"'");
        //sysout.println("actual:   '"+actual+"'");
        assertEquals(expected,actual);
    }
    @Test public void testVerbose() {
        Tee tee=new Tee(byteArrayOutputStream);
        tee.verbose=true;
        String string="foo";
        tee.printStream.println(string);
        String expected="0>"+tee.prefix+string+"0>"+lineSeparator; // second numeric prefix id from newline()
        String actual=byteArrayOutputStream.toString();
        //sysout.println("expected: '"+expected+"'");
        //sysout.println("actual:   '"+actual+"'");
        assertEquals(expected,actual);
    }
    @Test public void testTeeSetBoth() {
        Tee tee=new Tee(byteArrayOutputStream);
        tee.setOut();
        tee.setErr();
        assertEquals(tee.printStream,System.out);
        assertEquals(tee.printStream,System.err);
        //String string="tees print stream";
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        String outStruig="new sysout after set setOut";
        tee.printStream.println(outStruig);
        String errStrig="new syserr after set setErr";
        tee.printStream.println(errStrig);
        expected+=tee.prefix+outStruig+lineSeparator+""+tee.prefix+errStrig+lineSeparator;
        String actual=byteArrayOutputStream.toString();
        //sysout.println("expected: '"+expected+"'");
        //sysout.println("actual:   '"+actual+"'");
        assertEquals(expected,actual);
    }
    @Test public void testRestore() {
        Tee tee=new Tee(byteArrayOutputStream);
        tee.setOut();
        tee.setErr();
        tee.restoreErr();
        tee.restoreOut();
        assertEquals(sysout,System.out);
        assertEquals(syserr,System.err);
    }
    @Test public void testAddOutputStream() {
        Tee tee=new Tee(byteArrayOutputStream);
        ByteArrayOutputStream baosForOut=new ByteArrayOutputStream();
        PrintStream ps=new PrintStream(baosForOut);
        System.setOut(ps);
        tee.addOutputStream(System.out);
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        String actual=baosForOut.toString();
        //sysout.println("expected: '"+expected+"'");
        //sysout.println("actual:   '"+actual+"'");
        assertEquals(expected,actual);
    }
    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
    private final PrintStream sysout=System.out,syserr=System.err;
}
