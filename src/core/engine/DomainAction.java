package core.engine;

import java.util.List;
import equipment.Board;
import equipment.Point;
import equipment.Stone;
import model.Model;

/**
 * Domain-level actions. SGF mapping lives in format adapters.
 */
public sealed interface DomainAction permits DomainAction.EngineAction, DomainAction.ConfigAction {
    // Marker subtypes to clarify intent (no behavior change)
    sealed interface EngineAction extends DomainAction permits SetupAddStone, SetupSetEdge, Move, Pass, Resign {}
    sealed interface ConfigAction extends DomainAction permits SetBoardSpec, SetTopology, SetShape {}

    record SetBoardSpec(int width,int depth) implements ConfigAction {} // creates board using current state.topology/state.shape
    record SetTopology(Board.Topology topology) implements ConfigAction {}
    record SetShape(Board.Shape shape) implements ConfigAction {}
    record SetupAddStone(Stone color,Point point) implements EngineAction {}
    record SetupSetEdge(Point point) implements EngineAction {}
    record Move(Stone color,Point point) implements EngineAction {}
    record Pass(Stone color) implements EngineAction {}
    record Resign(Stone color) implements EngineAction {}

    default void apply(model.Model model) { applyTo(this,model); }

    static void applyTo(DomainAction action,Model model) {
        switch(action) {
            case DomainAction.SetBoardSpec a-> {
                //model.setRoot(a.width(),a.depth(),model.boardTopology(),model.boardShape());
                model.setBoard(Board.factory.create(a.width(),a.depth(),model.boardTopology(),model.boardShape()));
            }
            case DomainAction.SetTopology a-> {
                model.setBoardTopology(a.topology());
                if(model.board()!=null) model.setBoard(Board.factory.create(model.board().width(),model.board().depth(),
                        model.boardTopology(),model.boardShape()));
            }
            case DomainAction.SetShape a-> {
                model.setBoardShape(a.shape());
                if(model.board()!=null) model.setBoard(Board.factory.create(model.board().width(),model.board().depth(),
                        model.boardTopology(),model.boardShape()));
            }
            case DomainAction.SetupAddStone a-> {
                model.ensureBoard();
                model.board().setAt(a.point(),a.color());
            }
            case DomainAction.SetupSetEdge a-> {
                model.ensureBoard();
                model.board().setAt(a.point(),Stone.edge);
            }
            case DomainAction.Move a-> {
                model.ensureBoard();
                if(a.point()==null) model.sgfPassAction();
                else model.sgfMakeMove(a.color(),a.point());
            }
            case DomainAction.Pass a->model.sgfPassAction();
            case DomainAction.Resign a->model.sgfResignAction();
            default->throw new IllegalArgumentException("Unexpected value: "+action);
        }
    }
}
