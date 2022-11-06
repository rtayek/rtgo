package io;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.util.logging.*;
import org.junit.*;
import io.Logging.MyFormatter;
import utilities.MyTestWatcher;
public class LoggingTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        Logging.useColor=false;
        LogManager.getLogManager().reset();
        logger=Logger.getLogger(getClass().getName());
        handlers=logger.getHandlers();
        assertEquals(0,handlers.length); // remove this
        checkHandlers(); // and remove this?
    }
    @After public void tearDown() throws Exception {}
    void checkHandlers() { // why do we need this?
        for(Handler handler:handlers) logger.removeHandler(handler);
        handlers=logger.getHandlers();
        if(handlers.length>1) System.out.println("still has one handler!");
    }
    @Test public void testWithNoHandlers() {
        Logging.toString(System.out,logger);
        logger.info("info 1");
        // should no see any output
    }
    @Test public void testWithConsoleHandler() {
        System.out.println("logger level: "+logger.getLevel());
        Handler handler=new ConsoleHandler();
        logger.addHandler(handler);
        System.out.println("handler level: "+handler.getLevel());
        //Logging.toString(logger);
        logger.info("message");
        assertEquals(null,logger.getLevel());
        // how to test, message come out in red on he console.
    }
    @Test public void testAddStreamHandler() {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        Handler handler=new StreamHandler(baos,new MyFormatter());
        logger.addHandler(handler);
        String string="info 3";
        logger.info(string);
        handler.flush();
        String actual=baos.toString();
        assertTrue(actual.contains(string));
    }
    Logger logger;
    Handler[] handlers;
}
