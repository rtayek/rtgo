package model;
import org.junit.Rule;
import utilities.MyTestWatcher;
import io.Logging;
import static org.junit.Assert.*;
import org.junit.Test;
import equipment.*;
import model.Model.*;
import model.Move2.MoveType;
public class RoleTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
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
        assertRoleMove(Role.playBlack,Stone.black,new Point(),false,MoveResult.legal);
    }
    @Test public void testPlayBlackWhenRoleIsPlayWhite() {
        assertRoleMove(Role.playWhite,Stone.black,new Point(1,1),false,MoveResult.badRole);
    }
    @Test public void testPlayWhiteWhenRoleIsPlayWhite() {
        assertRoleMove(Role.playWhite,Stone.white,new Point(1,1),true,MoveResult.legal);
    }
    @Test public void testPlayWhiteWhenRoleIsPlayBlack() {
        assertRoleMove(Role.playBlack,Stone.white,new Point(1,1),true,MoveResult.badRole);
    }
    @Test public void testCanNotPlayBlackWhenRoleIsObserver() {
        assertRoleMove(Role.observer,Stone.black,new Point(1,1),false,MoveResult.badRole);
    }
    @Test public void testCanNotPlayWhiteWhenRoleIsObserver() {
        assertRoleMove(Role.observer,Stone.white,new Point(1,1),false,MoveResult.badRole);
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
        Logging.mainLogger.info("ok: "+ok);
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
    private void assertRoleMove(Role role,Stone stone,Point point,boolean ensureWhiteTurn,MoveResult expected) {
        if(ensureWhiteTurn) model.move(Stone.black,new Point());
        model.setRole(role);
        boolean ok=model.checkAction(model.role(),What.move);
        MoveResult moveResult=model.moveAndPlaySound(stone,point);
        assertEquals(expected,moveResult);
        assertTrue(ok==MoveResult.legal.equals(moveResult));
    }
    final Model model=new Model();
    final Point point=new Point();
}

