package core.engine;

import equipment.Board;
import equipment.Point;
import equipment.Stone;

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

}
