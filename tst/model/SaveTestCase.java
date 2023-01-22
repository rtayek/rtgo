package model;
import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import equipment.Stone;
import utilities.MyTestWatcher;
public class SaveTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testA1A2() throws IOException {
        model.move(Stone.black,"A1",model.board().width());
        model.move(Stone.white,"A2",model.board().width());
        System.out.println("from scratch: "+model.root());
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue("save fails",ok);
        final String expected=stringWriter.toString();
        Model m=new Model();
        m.restore(new StringReader(expected));
        System.out.println("restored: "+m.root());
        stringWriter=new StringWriter();
        m.save(stringWriter);
        final String actual=stringWriter.toString();
        System.out.println("actual: "+actual);
        assertEquals(actual,expected);
    }
    @Test public void testA1A2restored() throws IOException {
        
    }
    String foo="(;B[as];W[ar])";
    Model model=new Model();
}
