package core.engine;

import java.util.List;

/**
 * Game-agnostic actions that higher-level games map to/from.
 * No dependency on game-specific types or models.
 */
public sealed interface Action permits Action.Play, Action.Pass, Action.Resign,
        Action.SetBoard {
    /**
     * Play at (x,y). Optional role identifies the actor if needed by the game.
     */
    record Play(int x,int y,String role) implements Action {}
    /**
     * Pass turn. Optional role identifies the actor if needed by the game.
     */
    record Pass(String role) implements Action {}
    /**
     * Resign. Optional role identifies the actor if needed by the game.
     */
    record Resign(String role) implements Action {}
    /**
     * Configure board size. Games may ignore depth if they are 2D.
     */
    record SetBoard(int width,int height) implements Action {}
}
