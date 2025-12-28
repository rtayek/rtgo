package model;

import java.util.List;
import equipment.*;
import equipment.Board.Shape;
import sgf.P2;

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
}
