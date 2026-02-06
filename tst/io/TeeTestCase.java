package io;
import static com.tayek.util.io.Constants.lineSeparator;
import static org.junit.Assert.assertEquals;
import java.io.*;
import java.util.logging.LogManager;
import org.junit.*;
import utilities.TestSupport;
public class TeeTestCase extends TestSupport {
    // https://stackoverflow.com/questions/27825682/flushing-streamhandlers-during-debugging-using-java-util-logging-autoflush
    // https://gist.github.com/jiayao/36606389023f67fd5278d2d18656d388
    @Before public void setUp() throws Exception {
        byteArrayOutputStream.reset();
        LogManager.getLogManager().reset(); // can i really get rid of this?
        baseOut=new PrintStream(new ByteArrayOutputStream(),true);
        baseErr=new PrintStream(new ByteArrayOutputStream(),true);
        console=new ConsoleStreams(baseOut,baseErr);
    }
    @After public void tearDown() throws Exception {
        console.out=baseOut;
        console.err=baseErr;
    }
    @Test public void testTeesPrintStream() {
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        String actual=byteArrayOutputStream.toString();
        assertEquals(expected,actual);
    }
    @Test public void testSetOut() {
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        tee.setOut();
        assertEquals(tee.printStream,console.out);
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        // if we add console out and console err
        // then it also shows up in both.
        String actual=byteArrayOutputStream.toString();
        assertEquals(expected,actual);
    }
    @Test public void testSetErr() {
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        tee.setErr();
        assertEquals(tee.printStream,console.err);
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        String actual=byteArrayOutputStream.toString();
        assertEquals(expected,actual);
    }
    @Test public void testVerbose() {
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        tee.verbose=true;
        String string="foo";
        tee.printStream.println(string);
        String expected="0>"+tee.prefix+string+"0>"+lineSeparator; // second numeric prefix id from newline()
        String actual=byteArrayOutputStream.toString();
        assertEquals(expected,actual);
    }
    @Test public void testTeeSetBoth() {
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        tee.setOut();
        tee.setErr();
        assertEquals(tee.printStream,console.out);
        assertEquals(tee.printStream,console.err);
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        String outStruig="new console out after set";
        tee.printStream.println(outStruig);
        String errStrig="new console err after set";
        tee.printStream.println(errStrig);
        expected+=tee.prefix+outStruig+lineSeparator+""+tee.prefix+errStrig+lineSeparator;
        String actual=byteArrayOutputStream.toString();
        assertEquals(expected,actual);
    }
    @Test public void testRestore() {
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        tee.setOut();
        tee.setErr();
        tee.restoreErr();
        tee.restoreOut();
        assertEquals(baseOut,console.out);
        assertEquals(baseErr,console.err);
    }
    @Test public void testAddOutputStream() {
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        ByteArrayOutputStream baosForOut=new ByteArrayOutputStream();
        PrintStream ps=new PrintStream(baosForOut);
        console.out=ps;
        tee.addOutputStream(console.out);
        String string="tees print stream";
        String expected=tee.prefix+string+lineSeparator;
        tee.printStream.println(string);
        String actual=baosForOut.toString();
        assertEquals(expected,actual);
    }
    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
    ConsoleStreams console;
    PrintStream baseOut;
    PrintStream baseErr;
}
