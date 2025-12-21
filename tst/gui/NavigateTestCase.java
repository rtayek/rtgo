package gui;
import static model.Navigate.*;
import static org.junit.Assert.*;
import java.io.File;
import org.junit.*;
import io.IOs;
import model.*;
import utilities.MyTestWatcher;
public class NavigateTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testEach() {
        for(Navigate navigate:Navigate.values()) assertFalse("new model",navigate.canDo(model));
    }
    @Test public void testExample() {
        String filename="ff4_ex.sgf";
        File file=new File("sgf",filename);
        model.restore(IOs.toReader(file));
        for(Navigate navigate:Navigate.values()) {
            Boolean expected=navigate.equals(down)||navigate.equals(bottom);
            assertEquals(filename,expected,navigate.canDo(model));
        }
    }
    @Test public void testExampleAfterOneDown() {
        String filename="ff4_ex.sgf";
        File file=new File("sgf",filename);
        model.restore(IOs.toReader(file));
        down.do_(model);
        Boolean canDo=up.canDo(model);
        assertEquals(Boolean.TRUE,canDo);
    }
    Model model=new Model();
}
