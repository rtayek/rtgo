# Core Abstractions

## DomainAction

* Unit of execution
* Format-independent state change

Examples:

* Move, Pass, Resign
* SetupAddStone, SetTopology, SetShape

Rule:

* Engine executes actions, never SGF properties directly

## DomainActionApplier

* Applies actions to state
* Centralizes mutation
* Must match legacy do_() semantics

## GameNode

* SGF-free tree node
* Contains:

  * List<DomainAction>
  * NodeAnnotations
  * children

## NodeAnnotations / RawProperty

* Preserve uninterpreted SGF data
* Ensure lossless round-trip

## SgfDomainActionMapper

* Maps SGF → DomainAction
* Preserves non-executed properties
* Keeps SGF logic out of engine

## Model (transitional)

* Tree/state orchestration
* Replay/navigation
* Action application coordination

## Game-specific

### Go

* Board, stones, blocks, liberties
* Topology and shape
* SGF/GTP mapping

### Tic-Tac-Toe

* TttState, TttMove, TttSpec
* Used to validate engine generality
