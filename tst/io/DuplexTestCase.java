package io;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.junit.*;
import controller.GTPFrontEnd;
import io.IOs.Duplex;
import utilities.MyTestWatcher;
public class DuplexTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test() public void testCreatTwoEnds() throws Exception {
        GTPFrontEnd frontEnd1=new GTPFrontEnd(duplex.front);
        frontEnd1.name="front end 1";
        GTPFrontEnd frontEnd2=new GTPFrontEnd(duplex.back);
        frontEnd2.name="front end 2";
    }
    @Test() public void testReadAndwrite2() throws Exception {
        // two front ends is not a very good test
        GTPFrontEnd frontEnd1=new GTPFrontEnd(duplex.front);
        frontEnd1.name="front end 1";
        GTPFrontEnd frontEnd2=new GTPFrontEnd(duplex.back);
        frontEnd2.name="front end 2";
        frontEnd1.out.write("foo\n");
        frontEnd1.out.flush();
        Thread thread2=new Thread(frontEnd2);
        thread2.start();
        String s1=frontEnd1.in.readLine();
        assertEquals("foo",s1);
    }
    void writeFlushRead(GTPFrontEnd frontEnd1,GTPFrontEnd frontEnd2) throws IOException {
        frontEnd1.out.write("foo\n");
        frontEnd1.out.flush();
        String s1=frontEnd1.in.readLine();
        assertEquals("foo",s1);
    }
    @Test() public void test1() throws Exception {
        GTPFrontEnd frontEnd1=new GTPFrontEnd(duplex.front); // crosswired
        frontEnd1.name="1";
        Thread thread1=new Thread(frontEnd1);
        thread1.start();
        GTPFrontEnd frontEnd2=new GTPFrontEnd(duplex.back); // crosswired
        frontEnd2.name="2";
        Thread thread2=new Thread(frontEnd2);
        thread2.start();
        writeFlushRead(frontEnd1,frontEnd2);
    }
    @Test() public void testFront12() throws Exception {
        GTPFrontEnd frontEnd1=new GTPFrontEnd(duplex.front); // crosswired
        frontEnd1.name="front end 1";
        GTPFrontEnd frontEnd2=new GTPFrontEnd(duplex.back); // crosswired
        frontEnd2.name="front end 2";
        frontEnd1.out.write("foo\n");
        frontEnd1.out.flush();
        Thread thread2=new Thread(frontEnd2);
        thread2.start();
        String s1=frontEnd1.in.readLine();
        assertEquals("foo",s1);
    }
    @Test() public void testFront21() throws Exception {
        GTPFrontEnd frontEnd1=new GTPFrontEnd(duplex.front); // crosswired
        frontEnd1.name="front end 1";
        GTPFrontEnd frontEnd2=new GTPFrontEnd(duplex.back); // crosswired
        frontEnd2.name="front end 2";
        frontEnd2.out.write("bar\n");
        frontEnd2.out.flush();
        Thread thread1=new Thread(frontEnd1);
        thread1.start();
        System.out.flush();
        String s2=frontEnd2.in.readLine();
        assertEquals("bar",s2);
    }
    final long timeout=0;
    IOs.Duplex duplex=new Duplex();
}
