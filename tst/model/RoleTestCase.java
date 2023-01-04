package model;
import static org.junit.Assert.*;
import org.junit.*;
import equipment.*;
import model.Model.*;
import utilities.MyTestWatcher;
public class RoleTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    // many more cases here.
    // also gtp is supposed to be able to handle consecutive moves by the same player
    // this tests moveAndPlaySound().
    @Test public void testPlayBlackWhenRoleIsPlayBlack() {
        model.setRole(Role.playBlack);
        MoveResult ok=model.moveAndPlaySound(Stone.black,new Point());
        assertEquals(MoveResult.legal,ok);
    }
    @Test public void testPlayBlackWhenRoleIsNotPlayBlack() {
        model.setRole(Role.playWhite);
        MoveResult ok=model.moveAndPlaySound(Stone.black,new Point(1,1));
        assertNotEquals(MoveResult.legal,ok);
    }
    @Test public void testPlayWhiteWhenRoleIsPlayWhite() {
        model.move(Stone.black,new Point());
        model.setRole(Role.playWhite);
        MoveResult ok=model.moveAndPlaySound(Stone.white,new Point(1,1));
        assertEquals(MoveResult.legal,ok);
    }
    @Test public void testPlayWhiteWhenRoleIsNotPlayWhite() {
        model.move(Stone.black,new Point());
        model.setRole(Role.playBlack);
        MoveResult ok=model.moveAndPlaySound(Stone.white,new Point(1,1));
        assertNotEquals(MoveResult.legal,ok);
    }
    @Test public void testCanNotPlayWhiteWhenRoleIsObserver() {
        model.setRole(Role.observer);
        MoveResult ok=model.moveAndPlaySound(Stone.white,new Point(1,1));
        assertNotEquals(MoveResult.legal,ok);
    }
    final Model model=new Model(); // does this need a setRoot()?
}
