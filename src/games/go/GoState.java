package games.go;
import java.util.Stack;
import core.api.GameState;
import equipment.*;
import model.Model.State;
public final class GoState implements GameState {
    public Board board;
    public final Stone toPlay;
    public GoState(Board board,Stone toPlay) { this.board=board; this.toPlay=toPlay; }
    void push() { // see if we can eliminate copying the board
        Board copy=board.copy();
        State clone=null;
        try {
            clone=state.clone();
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        stack.push(state);
        state=clone;
        board=copy;
    }
    void pop() { state=stack.pop(); }
    State state=new State();
    Stack<State> stack=new Stack<>();
}
