package io;
import static org.junit.Assert.assertEquals;
import java.io.*;
import org.junit.*;
import io.IO.Pipe;
import utilities.MyTestWatcher;
public class PipeTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    static String writeFlushRead(BufferedReader in,Writer out,String string) throws IOException {
        out.write(string+'\n');
        out.flush();
        String actual=in.readLine();
        return actual;
    }
    @Test public void testWriteReadOnPipe() throws IOException {
        Pipe pipe=new Pipe();
        String expected="message";
        String actual=writeFlushRead(pipe.in,pipe.out,expected);
        assertEquals(expected,actual);
    }
    @Test() public void testWriteAndReadOnDifferentThreads() {
        Pipe pipe=new Pipe();
        String expected="Hello";
        Thread thread1=new Thread(new Runnable() {
            @Override public void run() {
                try {
                    pipe.out.write(expected);
                    pipe.out.write("\n");
                    pipe.out.flush();
                } catch(IOException e) {
                    Logging.mainLogger.info(this+" caught: "+e);
                }
            }
        });
        Thread thread2=new Thread(new Runnable() {
            @Override public void run() {
                try {
                    String actual=pipe.in.readLine();
                    assertEquals(expected,actual);
                } catch(IOException e) {
                    Logging.mainLogger.info(this+" caught: "+e);
                }
            }
        });
        thread1.start();
        thread2.start();
    }
    @Test() public void testCrossWiring() throws Exception {
        io.IO.Duplex duplex=new io.IO.Duplex();
        duplex.front.out().write("foo\n");
        duplex.front.out().flush();
        String s1=duplex.back.in().readLine();
        assertEquals("foo",s1);
        duplex.back.out().write("bar\n");
        duplex.back.out().flush();
        String s2=duplex.front.in().readLine();
        assertEquals("bar",s2);
    }
    @Test public void testCrossWiring2() throws IOException {
        io.IO.Duplex duplex=new io.IO.Duplex();
        String expected="message";
        String actual=writeFlushRead(duplex.back.in(),duplex.front.out(),expected);
        assertEquals(expected,actual);
        String actual2=writeFlushRead(duplex.front.in(),duplex.back.out(),expected);
        assertEquals(expected,actual2);
    }
    void writeFlushRunRead(Copy frontEnd1,Copy frontEnd2) throws IOException {
        frontEnd1.out.write("foo\n");
        frontEnd1.out.flush();
        frontEnd2.run(true,false); // needs to run with a thread.
        String s1=frontEnd1.in.readLine();
        assertEquals("foo",s1);
    }
    @Test() public void testwriteFlushRunRead() throws Exception {
        io.IO.Duplex duplex=new io.IO.Duplex();
        Copy frontEnd1=new Copy(duplex.front.in(),duplex.front.out()); // crosswired
        frontEnd1.name="1";
        Copy frontEnd2=new Copy(duplex.back.in(),duplex.back.out()); // crosswired
        frontEnd2.name="2";
        writeFlushRunRead(frontEnd1,frontEnd2);
        //writeFlushRunRead(frontEnd1,frontEnd2);
    }
    @Test() public void testwriteFlushRunRead2() throws Exception {
        io.IO.Duplex duplex=new io.IO.Duplex();
        Copy frontEnd1=new Copy(duplex.front.in(),duplex.front.out()); // crosswired
        frontEnd1.name="1";
        Copy frontEnd2=new Copy(duplex.back.in(),duplex.back.out()); // crosswired
        frontEnd2.name="2";
        writeFlushRunRead(frontEnd2,frontEnd1);
        //writeFlushRunRead(frontEnd1,frontEnd2);
    }
}
