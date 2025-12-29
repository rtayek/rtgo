Added a game-agnostic action path and wired Tic-Tac-Toe to it:

Introduced neutral core.engine.Action (Play, Pass, Resign, SetBoard, Metadata) with no game-specific types.
Added games.ttt.TttActionApplier to apply these Actions to TTT state (currently supports Play; others reject).
Added games.ttt.TttActionMapper to turn a TttMove.Place into an Action.Play.
Added tests (TttActionApplierTest) covering play via Action, mapper round-trip, and unsupported action rejection; tests pass.
This sets up a clean pipeline for TTT without touching the Go-specific DomainAction/apply path.