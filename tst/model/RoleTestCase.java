package model;
import static org.junit.Assert.*;
import org.junit.*;
import equipment.*;
import model.Model.*;
import model.Move2.MoveType;
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
        assertTrue(model.checkAction(Role.anything,What.move));
        assertTrue(model.checkAction(Role.playBlack,What.move));
        assertFalse(model.checkAction(Role.playWhite,What.move));
        assertFalse(model.checkAction(Role.observer,What.move));
        model.moveAndPlaySound(Stone.black,new Point());
        assertTrue(model.checkAction(Role.anything,What.move));
        assertFalse(model.checkAction(Role.playBlack,What.move));
        assertTrue(model.checkAction(Role.playWhite,What.move));
        assertFalse(model.checkAction(Role.observer,What.move));
    }
    @Test public void testPlayBlackWhenRoleIsPlayBlack() {
        model.setRole(Role.playBlack);
        boolean ok=model.checkAction(model.role(),What.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.black,new Point());
        assertEquals(MoveResult.legal,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testPlayBlackWhenRoleIsPlayWhite() {
        model.setRole(Role.playWhite);
        boolean ok=model.checkAction(model.role(),What.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.black,new Point(1,1));
        assertNotEquals(MoveResult.legal,moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testPlayWhiteWhenRoleIsPlayWhite() {
        model.move(Stone.black,new Point()); // ensure that it's white's turn.
        model.setRole(Role.playWhite);
        boolean ok=model.checkAction(model.role(),What.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.white,new Point(1,1));
        assertEquals(MoveResult.legal,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testPlayWhiteWhenRoleIsPlayBlack() {
        model.move(Stone.black,new Point()); // ensure that it's white's turn.
        model.setRole(Role.playBlack);
        boolean ok=model.checkAction(model.role(),What.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.white,new Point(1,1));
        assertNotEquals(MoveResult.legal,moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testCanNotPlayBlackWhenRoleIsObserver() {
        model.setRole(Role.observer);
        boolean ok=model.checkAction(model.role(),What.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.black,new Point(1,1));
        assertNotEquals(MoveResult.legal,moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    @Test public void testCanNotPlayWhiteWhenRoleIsObserver() {
        model.setRole(Role.observer);
        boolean ok=model.checkAction(model.role(),What.move);
        MoveResult moveResult=model.moveAndPlaySound(Stone.white,new Point(1,1));
        assertNotEquals(MoveResult.legal,moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    // from load existing
    // all models had role-anything.
    @Test public void testMakeLegalBlackMove() throws InterruptedException {
        model.setRole(Role.playBlack);
        Move2 move=new Move2(MoveType.move,Stone.black,point);
        if(!model.checkAction(model.role(),What.move)) throw new RuntimeException("check fails!");
        MoveResult moveResult=model.move(move);
        assertEquals(model.board().at(point),Stone.black);
        assertEquals(MoveResult.legal,moveResult);
    }
    @Test public void testMakeMoveOutOfTurn() throws InterruptedException {
        Move2 move=new Move2(MoveType.move,Stone.white,point);
        model.setRole(Role.playBlack);
        boolean ok=model.checkAction(model.role(),What.move);
        System.out.println("ok: "+ok);
        if(!ok) throw new RuntimeException("check fails!");
        MoveResult moveResult=model.move(move);
        assertEquals(MoveResult.notYourTurn,moveResult);
    }
    @Test public void testMakeMoveOutOfTurnAnything() throws InterruptedException {
        Move2 move=new Move2(MoveType.move,Stone.white,point);
        model.setRole(Role.anything);
        if(!model.checkAction(model.role(),What.move)) throw new RuntimeException("check fails!");
        MoveResult moveResult=model.move(move);
        assertEquals(MoveResult.legal,moveResult);
        assertEquals(model.board().at(point),Stone.white);
    }
    @Test public void testMoveOnOccupiedPoint() throws InterruptedException {
        model.setRole(Role.anything);
        Move2 move=new Move2(MoveType.move,Stone.black,point);
        MoveResult moveResult=model.move(move);
        assertEquals(model.board().at(point),Stone.black);
        assertEquals(MoveResult.legal,moveResult);
        move=new Move2(MoveType.move,Stone.white,point);
        moveResult=model.move(move);
        assertEquals(MoveResult.occupied,moveResult);
        // should this be allowed if role is anything?
    }
    final Model model=new Model();
    final Point point=new Point();
}
