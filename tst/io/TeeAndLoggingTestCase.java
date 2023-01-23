package io;
import static io.Constants.lineSeparator;
import static io.Logging.flushingStreamHandler;
import static org.junit.Assert.*;
import java.io.*;
import java.util.logging.*;
import org.junit.*;
import io.Logging.MyFormatter;
import utilities.Utilities;
class MyPrintStream {
    MyPrintStream(String prefix,PrintStream printStream) { this.printStream=printStream; }
    final PrintStream printStream;
}
public class TeeAndLoggingTestCase {
    @Before public void setUp() throws Exception {
        byteArrayOutputStream.reset(); //
        LogManager.getLogManager().reset();
        Logging.useColor=false;
        logger=Logger.getLogger(getClass().getName());
        handlers=logger.getHandlers();
        assertEquals(0,handlers.length); // remove this
    }
    @After public void tearDown() throws Exception {
        System.setOut(sysout); //
        System.setErr(syserr);
        LogManager.getLogManager().reset();
    }
    @Ignore @Test public void testWithTeeToSysout() {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        Tee tee=new Tee(baos);
        //tee.addOutputStream(System.err); // works fine
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
        System.out.println("ex: "+expected);
        System.out.println("ac: "+actual);
        Utilities.printDifferences(System.out,expected,actual);
        assertEquals(expected,actual);
    }
    @Test public void testWithTeeToSyserr() {
        Tee tee=new Tee(System.err);
        //tee.addOutputStream(System.err); // works fine
        logger.setLevel(Level.INFO);
        logger.info("info 5");
        Handler handler=new StreamHandler(tee,new MyFormatter());
        logger.addHandler(handler);
        String string="info 6";
        logger.info(string);
        handler.flush();
    }
    @Test public void testWithTee2() {
        Tee tee=new Tee(System.out);
        //tee.addOutputStream(System.err);
        System.out.println("sysout");
        System.err.println("syserr");
        Handler handler=new ConsoleHandler();
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
        logger.info("info 7");
        handler.flush();
    }
    @Test public void test() {
        System.out.println("hello from System.out");
        System.err.println("hello from System.err");
        sysout.println("---");
        ps1.println("hello from ps1");
        ps2.println("hellofrom ps2");
        sysout.println("---");
        System.setOut(ps1);
        System.setErr(ps2);
        sysout.println("err: "+System.err);
        System.out.println("hello from System.out after set");
        System.err.println("hello from System.err after set");
        sysout.println("---");
        Logger logger=Logger.getLogger("frog");
        Logging.setupLogger(logger,new MyFormatter());
        logger.info("logger");
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        Tee tee=new Tee(byteArrayOutputStream);
        tee.addOutputStream(System.out);
        tee.setOut();
        logger.info("logger 1");
        Tee tee2=new Tee(byteArrayOutputStream);
        tee2.addOutputStream(System.err);
        tee2.setErr();
        logger.info("logger 2");
        System.err.flush();
        sysout.println("err: "+System.err);
        System.out.println("hello from System.out after set");
        System.err.println("hello from System.err after set");
    }
    @Ignore @Test public void testTwoTeeesAndALogger() {
        // this one ignore seems to fix both tests.thu
        // no, it can still fail, so ignoring it for now.
        try {
            // this seems to be working exactly the way i want.
            Tee tee=new Tee(byteArrayOutputStream);
            tee.addOutputStream(System.out);
            tee.setOut();
            Tee tee2=new Tee(byteArrayOutputStream);
            tee2.addOutputStream(System.err);
            tee2.setErr();
            tee2.prefix="T2 ";
            String string1="tee ps after set setOut";
            tee.printStream.println(string1);
            String string2="sysout after set setOut";
            System.out.println(string2);
            String string3="tee2 ps after set setErr";
            tee2.printStream.println(string3);
            //sysout.println("baos: "+byteArrayOutputStream);
            String string4="syserr after set setErr";
            System.err.println(string4);
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
            Logging.toString(sysout,logger);
            logger.info("logger 0");
            //  The package java.util.logging conflicts with a package accessible from another module: java.logging     ConsoleHandler.java     /code35/src/java/util/logging   line 1  Java Problem
            Handler[] handlers=logger.getHandlers();
            if(true||handlers.length==0) {
                sysout.println("no handlers!");
                Handler handler=new StreamHandler(tee.printStream,new MyFormatter());
                handler.setLevel(Level.ALL);
                logger.setUseParentHandlers(false);
                handler.setFormatter(new MyFormatter());
                logger.addHandler(handler);
                // does not help in false case.
            }
            Logging.toString(sysout,logger);
            // subclass console handler?
            handlers=logger.getHandlers();
            assertTrue(handlers.length>0);
            // Handler handler=Logging.flushingStreamHandler(sysout);
            //logger.addHandler(handler);
            //logger.info("logger 1");
            String expected=tee.prefix+string1+lineSeparator+tee.prefix+string2+lineSeparator+tee2.prefix+string3
                    +lineSeparator+tee2.prefix+string4+lineSeparator
                    +"00000         main    INFO                         logger 0 in io.TeeAndLoggingTestCase.testTwoTeeesAndALogger()"
                    +"\n"; // no crlf after log message!
            //+lineSeparator;
            String actual=byteArrayOutputStream.toString();
            sysout.println("ex: "+expected.endsWith("\n")+", ac: "+actual.endsWith("\n"));
            sysout.println("ex: "+expected.endsWith("\r\n")+", ac: "+actual.endsWith("\r\n"));
            sysout.println("expected: '"+expected+"'");
            sysout.println("actual:   '"+actual+"'");
            sysout.println("'"+expected.charAt(expected.length()-1)+"'");
            sysout.println("'"+actual.charAt(actual.length()-1)+"'");
            sysout.println("-----------------------");
            Utilities.printDifferences(sysout,expected,actual);
            assertEquals(expected,actual);
        } catch(Exception e) {
            e.printStackTrace(sysout);
            sysout.println("caught: "+e);
        }
        sysout.println("exit testTwoTeees");
    }
    @Test public void testTwoTeeesWithLogger() {
        Logger logger=Logger.getLogger("frog");
        Logging.setupLogger(logger,new MyFormatter());
        logger.info("logger 0");
        Tee tee=new Tee(byteArrayOutputStream);
        tee.addOutputStream(System.out);
        tee.setOut();
        Tee tee2=new Tee(byteArrayOutputStream);
        logger.info("logger 1");
        tee2.addOutputStream(System.err);
        tee2.setErr();
        tee2.prefix="T2 ";
        String string2="sysout after set setOut";
        System.out.println(string2);
        String string4="syserr after set setErr";
        System.err.println(string4);
        logger.info("logger 2");
        String expected=tee.prefix+string2+lineSeparator+tee2.prefix+string4+lineSeparator;
        String actual=byteArrayOutputStream.toString();
        sysout.println("expected: '"+expected+"'");
        sysout.println("actual:   '"+actual+"'");
        //Parser.printDifferences(expected,actual);
        assertEquals(expected,actual);
    }
    @Test public void testUseLoggerAsIs() {
        // see main()
        Logging.setUpLogging();
        Logging.mainLogger.setLevel(Level.INFO);
        Logging.mainLogger.info("foo");
        Tee tee=new Tee(byteArrayOutputStream);
        tee.printStream.println("tee from tee's printstream");
        System.err.println("err should go to err");
        tee.setErr();
        System.err.println("err should go to tee");
        Logging.mainLogger.severe("should also go to tee");
        // tee.addOutputStream(System.out);
        String actual=byteArrayOutputStream.toString();
        sysout.println("actual: "+actual);
    }
    @Test public void testPrintStreamOfTeeForAHandler() {
        // this seems to work
        // maybe add this to text area soon?
        Tee tee=new Tee(System.out);
        Logging.mainLogger.setLevel(Level.ALL);
        Logging.mainLogger.severe("before handler was added 1");
        Handler handler=flushingStreamHandler(tee);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new MyFormatter());
        Logging.mainLogger.addHandler(handler);
        Logging.mainLogger.severe("after handler was added 1");
        tee.printStream.println("ps from tee 1");
        tee.addOutputStream(new PrintStream(System.err));
        tee.printStream.println("ps from tee 2");
        tee.printStream.flush();
        //System.out.println("sysout"); // just comes out on sysout
        //System.err.println("syserr"); // ditto
        // not really a test. will need to use baos.
        // 10/30/22 this may be ending badly?
        // maybe causing test verbose to fail
        // try ignore
    }
    @Test public void testAddSysoutAndSyserrToPrintStreamOfTee() throws InterruptedException {
        // this does not work. why not?
        // tee replaces sysin and sysout.
        Tee tee=new Tee(System.out);
        tee.printStream.println("ps from tee 3");
        Logging.mainLogger.severe("before adding sysout and syserr");
        //tee.addOutputStream(System.out); // extra because tee is base on sysout
        tee.addOutputStream(System.err);
        Logging.mainLogger.severe("after adding sysout and syserr");
        tee.printStream.println("ps from tee 4");
        //psOut.println("psOut 2");
        //psErr.println("psErr 2");
        tee.printStream.println("ps from tee 5");
        tee.printStream.println("ps from tee 6");
        System.out.println("sysout"); // just comes out on sysout
        System.err.println("syserr"); // ditto
        tee.printStream.flush();
        // not really a test. will need to use baos.
    }
    Logger logger;
    Handler[] handlers;
    final PrintStream ps1=new PrintStream(System.out,true) {
        @Override public void println(String x) { super.println("1 "+x); }
    };
    final PrintStream ps2=new PrintStream(System.err,true) {
        @Override public void println(String x) { super.println("2 "+x); }
    };
    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
    private final PrintStream sysout=System.out,syserr=System.err;
}
