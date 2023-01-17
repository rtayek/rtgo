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
    // 1/9/23 we need different tests for when this comes from gtp
    // we do that by setting role=anything.
    @Test public void testCheck() {
        assertTrue(model.check(Role.anything,Action.move));
        assertTrue(model.check(Role.playBlack,Action.move));
        assertFalse(model.check(Role.playWhite,Action.move));
        assertFalse(model.check(Role.observer,Action.move));
        model.moveAndPlaySound(Stone.black,new Point());
        assertTrue(model.check(Role.anything,Action.move));
        assertFalse(model.check(Role.playBlack,Action.move));
        assertTrue(model.check(Role.playWhite,Action.move));
        assertFalse(model.check(Role.observer,Action.move));
    }
    @Test public void testPlayBlackWhenRoleIsPlayBlack() {
        model.setRole(Role.playBlack);
        boolean ok=model.check(model.role(),Action.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.black,new Point());
        assertEquals(MoveResult.legal,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testPlayBlackWhenRoleIsPlayWhite() {
        model.setRole(Role.playWhite);
        boolean ok=model.check(model.role(),Action.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.black,new Point(1,1));
        assertNotEquals(MoveResult.legal,moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testPlayWhiteWhenRoleIsPlayWhite() {
        model.move(Stone.black,new Point()); // ensure that it's white's turn.
        model.setRole(Role.playWhite);
        boolean ok=model.check(model.role(),Action.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.white,new Point(1,1));
        assertEquals(MoveResult.legal,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testPlayWhiteWhenRoleIsPlayBlack() {
        model.move(Stone.black,new Point()); // ensure that it's white's turn.
        model.setRole(Role.playBlack);
        boolean ok=model.check(model.role(),Action.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.white,new Point(1,1));
        assertNotEquals(MoveResult.legal,moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testCanNotPlayBlackWhenRoleIsObserver() {
        model.setRole(Role.observer);
        boolean ok=model.check(model.role(),Action.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.black,new Point(1,1));
        assertNotEquals(MoveResult.legal,moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testCanNotPlayWhiteWhenRoleIsObserver() {
        model.setRole(Role.observer);
        boolean ok=model.check(model.role(),Action.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.white,new Point(1,1));
        assertNotEquals(MoveResult.legal,moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    final Model model=new Model();
}
