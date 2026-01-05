package io;
import static org.junit.Assert.assertEquals;
import java.io.Writer;
import org.junit.*;
import utilities.MyTestWatcher;
public class CopyTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test0NoThread0() throws Exception {
        String expected0="frog\ndog\n";
        Writer w=Copy.sendAndReceive(expected0,false);
        String actual=w.toString();
        assertEquals(expected0,actual);
    }
    @Test public void testWithThread0() throws Exception {
        String expected0="frog\ndog\n";
        Writer w=Copy.sendAndReceive(expected0,true);
        String actual=w.toString();
        assertEquals(expected0,actual);
    }
    @Test public void testWithThread() throws Exception {
        Writer w=Copy.sendAndReceive(expected,true);
        String actual=w.toString();
        assertEquals(expected,actual);
    }
    @Test public void testWithNoThread() throws Exception {
        Writer w=Copy.sendAndReceive(expected,false);
        String actual=w.toString();
        assertEquals(expected,actual);
    }
    @Test public void testWithThread2() throws Exception {
        Writer w=Copy.sendAndReceive(expected2,true);
        String actual=w.toString();
        if(!expected2.endsWith("\n")) actual=actual.substring(0,actual.length()-1);
        assertEquals(expected2,actual);
    }
    @Test public void testWithNoThread2() throws Exception {
        Writer w=Copy.sendAndReceive(expected2,false);
        String actual=w.toString();
        if(!expected2.endsWith("\n")) actual=actual.substring(0,actual.length()-1);
        assertEquals(expected2,actual);
    }
    static String expected="a\nb\n";
    static String expected2="a\nb";
}
