package model;

import java.util.List;
import equipment.*;
import equipment.Board.Shape;
import sgf.P2;
import model.Model;

sealed interface DomainAction permits
DomainAction.SetBoardSpec,
DomainAction.SetTopology,
DomainAction.SetShape,
DomainAction.SetupAddStone,
DomainAction.SetupSetEdge,
DomainAction.Move,
DomainAction.Pass,
DomainAction.Resign,
DomainAction.RecordResult,
DomainAction.Metadata {

record SetBoardSpec(int width, int depth) implements DomainAction {} // creates board using current state.topology/state.shape
record SetTopology(Board.Topology topology) implements DomainAction {}
record SetShape(Shape shape) implements DomainAction {}

record SetupAddStone(Stone color, Point point) implements DomainAction {}
record SetupSetEdge(Point point) implements DomainAction {}

record Move(Stone color, Point point) implements DomainAction {}
record Pass(Stone color) implements DomainAction {}
record Resign(Stone color) implements DomainAction {}

// optional: keep RE but do not apply to core game end
record RecordResult(String result) implements DomainAction {}

// optional: FF/GM/HA/KM/AP etc as “extras” (no-op for core)
record Metadata(P2 p2, List<String> values) implements DomainAction {}

default void apply(Model model) { applyTo(this, model); }

static void applyTo(DomainAction action, Model model) {
    switch(action) {
        case DomainAction.SetBoardSpec a -> {
            model.setRoot(a.width(),a.depth(),model.boardTopology(),model.boardShape());
            model.setBoard(Board.factory.create(a.width(),a.depth(),model.boardTopology(),model.boardShape()));
        }
        case DomainAction.SetTopology a -> {
            model.setBoardTopology(a.topology());
            if(model.board()!=null)
                model.setBoard(Board.factory.create(model.board().width(),model.board().depth(),
                        model.boardTopology(),model.boardShape()));
        }
        case DomainAction.SetShape a -> {
            model.setBoardShape(a.shape());
            if(model.board()!=null)
                model.setBoard(Board.factory.create(model.board().width(),model.board().depth(),
                        model.boardTopology(),model.boardShape()));
        }
        case DomainAction.SetupAddStone a -> {
            model.ensureBoard();
            model.board().setAt(a.point(),a.color());
        }
        case DomainAction.SetupSetEdge a -> {
            model.ensureBoard();
            model.board().setAt(a.point(),Stone.edge);
        }
        case DomainAction.Move a -> {
            model.ensureBoard();
            if(a.point()==null) model.sgfPassAction();
            else model.sgfMakeMove(a.color(),a.point());
        }
        case DomainAction.Pass a -> model.sgfPassAction();
        case DomainAction.Resign a -> model.sgfResignAction();
        case DomainAction.RecordResult a -> { /* metadata only for now */ }
        case DomainAction.Metadata a -> { /* no-op */ }
        default -> throw new IllegalArgumentException("Unexpected value: "+action);
    }
}
}
