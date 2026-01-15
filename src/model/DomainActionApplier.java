package model;

import java.util.List;
import core.engine.DomainAction;
import equipment.Board;
import equipment.Stone;

/**
 * Applies DomainActions to a Model. Keeps core engine free of model dependencies.
 */
public final class DomainActionApplier {
    private final Model model;

    public DomainActionApplier(Model model) { this.model=model; }

    public void apply(DomainAction action) { applyTo(action,model); }

    public void applyAll(List<DomainAction> actions) {
        for(DomainAction action:actions) apply(action);
    }

    static void applyTo(DomainAction action,Model model) {
        switch(action) {
            case DomainAction.SetBoardSpec a-> {
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
