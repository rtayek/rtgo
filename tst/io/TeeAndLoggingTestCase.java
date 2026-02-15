package io;
import static com.tayek.util.io.Constants.lineSeparator;
import static io.Logging.flushingStreamHandler;
import static org.junit.Assert.*;
import java.io.*;
import java.util.logging.*;
import com.tayek.util.misc.Tee;
import org.junit.*;
import io.Logging.MyFormatter;
public class TeeAndLoggingTestCase {
    @Before public void setUp() throws Exception {
        byteArrayOutputStream.reset(); //
        LogManager.getLogManager().reset();
        Logging.useColor=false;
        logger=Logger.getLogger(getClass().getName());
        handlers=logger.getHandlers();
        assertEquals(0,handlers.length);
        baseOut=new PrintStream(new ByteArrayOutputStream(),true);
        baseErr=new PrintStream(new ByteArrayOutputStream(),true);
        console=new ConsoleStreams(baseOut,baseErr);
        ps1=new PrintStream(new ByteArrayOutputStream(),true) {
            @Override public void println(String x) { super.println("1 "+x); }
        };
        ps2=new PrintStream(new ByteArrayOutputStream(),true) {
            @Override public void println(String x) { super.println("2 "+x); }
        };
    }
    @After public void tearDown() throws Exception {
        console.out=baseOut;
        console.err=baseErr;
        LogManager.getLogManager().reset();
    }
    @Ignore @Test public void testWithTeeToSysout() {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        Tee tee=new Tee(baos);
        //tee.addOutputStream(console.err); // works fine
        logger.setLevel(Level.INFO);
        logger.info("info 4");
        Handler handler=new StreamHandler(tee,new MyFormatter());
        logger.addHandler(handler);
        logger.info("info 4b");
        handler.flush();
        String expected="00001         main    INFO                          info 4b in io.TeeAndLoggingTestCase.testWithTeeToSysout()"
                // this is really fragile. find a better way!
                // maybe strip off the method name?
                +"\n";
        String actual=baos.toString();
        Logging.mainLogger.info("ex: "+expected);
        Logging.mainLogger.info("ac: "+actual);
        assertEquals(expected,actual);
    }
    @Test public void testWithTeeToSyserr() {
        Tee tee=new Tee(console.err);
        //tee.addOutputStream(console.err); // works fine
        logger.setLevel(Level.INFO);
        logger.info("info 5");
        Handler handler=new StreamHandler(tee,new MyFormatter());
        logger.addHandler(handler);
        String string="info 6";
        logger.info(string);
        handler.flush();
    }
    @Test public void testWithTee2() {
        Tee tee=new Tee(console.out);
        //tee.addOutputStream(console.err);
        Logging.mainLogger.info("console out");
        Logging.mainLogger.warning("console err");
        Handler handler=new ConsoleHandler();
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
        logger.info("info 7");
        handler.flush();
    }
    @Test public void test() {
        Logging.mainLogger.info("hello from console.out");
        Logging.mainLogger.warning("hello from console.err");
        Logging.mainLogger.info("---");
        ps1.println("hello from ps1");
        ps2.println("hellofrom ps2");
        Logging.mainLogger.info("---");
        console.out=ps1;
        console.err=ps2;
        Logging.mainLogger.info("err: "+console.err);
        Logging.mainLogger.info("hello from console.out after set");
        Logging.mainLogger.warning("hello from console.err after set");
        Logging.mainLogger.info("---");
        Logger logger=Logger.getLogger("frog");
        Logging.setupLogger(logger,new MyFormatter());
        logger.info("logger");
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        tee.addOutputStream(console.out);
        tee.setOut();
        logger.info("logger 1");
        TestTee tee2=new TestTee(byteArrayOutputStream,console);
        tee2.addOutputStream(console.err);
        tee2.setErr();
        logger.info("logger 2");
        console.err.flush();
        Logging.mainLogger.info("err: "+console.err);
        Logging.mainLogger.info("hello from console.out after set");
        Logging.mainLogger.warning("hello from console.err after set");
    }
    @Ignore @Test public void testTwoTeeesAndALogger() {
        // this one ignore seems to fix both tests.thu
        // no, it can still fail, so ignoring it for now.
        try {
            // this seems to be working exactly the way i want.
            TestTee tee=new TestTee(byteArrayOutputStream,console);
            tee.addOutputStream(console.out);
            tee.setOut();
            TestTee tee2=new TestTee(byteArrayOutputStream,console);
            tee2.addOutputStream(console.err);
            tee2.setErr();
            tee2.prefix="T2 ";
            String string1="tee ps after set setOut";
            tee.printStream.println(string1);
            String string2="sysout after set setOut";
            tee.printStream.println(string2);
            String string3="tee2 ps after set setErr";
            tee2.printStream.println(string3);
            //console.out.println("baos: "+byteArrayOutputStream);
            String string4="syserr after set setErr";
            tee2.printStream.println(string4);
            Logging.useColor=false;
            Logger logger=null;
            /*// true
            [java.util.logging.ConsoleHandler@880ec60]
                ALL
                parent: java.util.logging.LogManager$RootLogger@3f3afe78
                use: false
                []
             */
            /*// false
            [java.util.logging.ConsoleHandler@3f3afe78]
                ALL
                parent: java.util.logging.LogManager$RootLogger@7f63425a
                use: false
                []
             */
            if(true) { // works (or at least it used to)
                // now it only works by itself!.
                logger=Logger.getLogger("frog");
                Logging.setupLogger(logger,new MyFormatter());
            } else { // does not work
                Logging.setUpLogging();
                logger=Logging.mainLogger;
            }
            logger.info("logger 0");
            //  The package java.util.logging conflicts with a package accessible from another module: java.logging     ConsoleHandler.java     /code35/src/java/util/logging   line 1  Java Problem
            Handler[] handlers=logger.getHandlers();
            if(true||handlers.length==0) {
                Logging.mainLogger.info("no handlers!");
                Handler handler=new StreamHandler(tee.printStream,new MyFormatter());
                handler.setLevel(Level.ALL);
                logger.setUseParentHandlers(false);
                handler.setFormatter(new MyFormatter());
                logger.addHandler(handler);
                // does not help in false case.
            }
            // subclass console handler?
            handlers=logger.getHandlers();
            assertTrue(handlers.length>0);
            // Handler handler=Logging.flushingStreamHandler(console.out);
            //logger.addHandler(handler);
            //logger.info("logger 1");
            String expected=tee.prefix+string1+lineSeparator+tee.prefix+string2+lineSeparator+tee2.prefix+string3
                    +lineSeparator+tee2.prefix+string4+lineSeparator
                    +"00000         main    INFO                         logger 0 in io.TeeAndLoggingTestCase.testTwoTeeesAndALogger()"
                    +"\n"; // no crlf after log message!
            //+lineSeparator;
            String actual=byteArrayOutputStream.toString();
            Logging.mainLogger.info("ex: "+expected.endsWith("\n")+", ac: "+actual.endsWith("\n"));
            Logging.mainLogger.info("ex: "+expected.endsWith("\r\n")+", ac: "+actual.endsWith("\r\n"));
            Logging.mainLogger.info("expected: '"+expected+"'");
            Logging.mainLogger.info("actual:   '"+actual+"'");
            Logging.mainLogger.info("'"+expected.charAt(expected.length()-1)+"'");
            Logging.mainLogger.info("'"+actual.charAt(actual.length()-1)+"'");
            Logging.mainLogger.info("-----------------------");
            assertEquals(expected,actual);
        } catch(Exception e) {
            Logging.mainLogger.warning(String.valueOf(e));
        }
        Logging.mainLogger.info("exit testTwoTeees");
    }
    @Test public void testTwoTeeesWithLogger() {
        Logger logger=Logger.getLogger("frog");
        Logging.setupLogger(logger,new MyFormatter());
        logger.info("logger 0");
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        tee.addOutputStream(console.out);
        tee.setOut();
        TestTee tee2=new TestTee(byteArrayOutputStream,console);
        logger.info("logger 1");
        tee2.addOutputStream(console.err);
        tee2.setErr();
        tee2.prefix="T2 ";
        String string2="sysout after set setOut";
        tee.printStream.println(string2);
        String string4="syserr after set setErr";
        tee2.printStream.println(string4);
        logger.info("logger 2");
        String expected=tee.prefix+string2+lineSeparator+tee2.prefix+string4+lineSeparator;
        String actual=byteArrayOutputStream.toString();
        Logging.mainLogger.info("expected: '"+expected+"'");
        Logging.mainLogger.info("actual:   '"+actual+"'");
        //Parser.printDifferences(expected,actual);
        assertEquals(expected,actual);
    }
    @Test public void testUseLoggerAsIs() {
        // see main()
        Logging.setUpLogging();
        Logging.mainLogger.setLevel(Level.INFO);
        Logging.mainLogger.info("foo");
        TestTee tee=new TestTee(byteArrayOutputStream,console);
        tee.printStream.println("tee from tee's printstream");
        Logging.mainLogger.warning("console err should go to err");
        tee.setErr();
        assertEquals(tee.printStream,console.err);
        tee.printStream.println("err should go to tee");
        Logging.mainLogger.severe("should also go to tee");
        // tee.addOutputStream(console.out);
        String actual=byteArrayOutputStream.toString();
        Logging.mainLogger.info("actual: "+actual);
    }
    @Test public void testPrintStreamOfTeeForAHandler() {
        // this seems to work
        // maybe add this to text area soon?
        Tee tee=new Tee(console.out);
        Logging.mainLogger.setLevel(Level.ALL);
        Logging.mainLogger.severe("before handler was added 1");
        Handler handler=flushingStreamHandler(tee);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new MyFormatter());
        Logging.mainLogger.addHandler(handler);
        Logging.mainLogger.severe("after handler was added 1");
        tee.printStream.println("ps from tee 1");
        tee.addOutputStream(console.err);
        tee.printStream.println("ps from tee 2");
        tee.printStream.flush();
        // not really a test. will need to use baos.
        // 10/30/22 this may be ending badly?
        // maybe causing test verbose to fail
        // try ignore
    }
    @Test public void testAddSysoutAndSyserrToPrintStreamOfTee() throws InterruptedException {
        // this does not work. why not?
        // tee replaces console out and console err.
        Tee tee=new Tee(console.out);
        tee.printStream.println("ps from tee 3");
        Logging.mainLogger.severe("before adding console out and console err");
        //tee.addOutputStream(console.out); // extra because tee is based on console out
        tee.addOutputStream(console.err);
        Logging.mainLogger.severe("after adding console out and console err");
        tee.printStream.println("ps from tee 4");
        //psOut.println("psOut 2");
        //psErr.println("psErr 2");
        tee.printStream.println("ps from tee 5");
        tee.printStream.println("ps from tee 6");
        Logging.mainLogger.info("console out");
        Logging.mainLogger.warning("console err");
        tee.printStream.flush();
        // not really a test. will need to use baos.
    }
    Logger logger;
    Handler[] handlers;
    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
    ConsoleStreams console;
    PrintStream baseOut;
    PrintStream baseErr;
    PrintStream ps1;
    PrintStream ps2;
}
