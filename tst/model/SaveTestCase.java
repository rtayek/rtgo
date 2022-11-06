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
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue("save fails",ok);
        final String expected=stringWriter.toString();
        Model m=new Model();
        m.restore(new StringReader(expected));
        stringWriter=new StringWriter();
        m.save(stringWriter);
        final String actual=stringWriter.toString();
        assertEquals(actual,expected);
    }
    Model model=new Model();
}
