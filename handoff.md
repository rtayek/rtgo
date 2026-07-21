Go Program Canonical Handoff
Scope

A Java-based Go program being refactored into a multi-game framework with Go as the primary game and Tic-Tac-Toe as a secondary validation game.

Primary goals:

keep Go behavior correct
preserve exact SGF round-trip behavior
separate core engine from SGF/GTP/UI concerns
support additional games through a common action-based architecture
1. Architecture
High-level layers
Core / Engine

Responsible for:

neutral execution model
game tree structure
action application
annotations for lossless round-trip
no direct dependency on SGF, GTP, or UI
Game-specific logic

Responsible for:

rules and state transitions
move legality
board semantics
game-specific renderers and adapters

Current games:

Go
Tic-Tac-Toe
Format adapters

Responsible for:

mapping SGF to internal actions
preserving uninterpreted properties
later: GTP and other transport/codecs
UI / CLI / Transport

Responsible for:

rendering
input handling
future client/server or GTP integration
must not contain game rules
Current package direction

Target organization is roughly:

core/
  api/
  engine/
  engine/applier/
  formats/sgf/
  util/

games/
  go/
    rules/
    adapters/
    render/
    ui/swing/
  ttt/
    rules/
    adapters/
    render/

ui/
  cli/
  swing/

legacy/

This is directionally correct, but the repository is still transitional.

2. Core Abstractions
DomainAction / Action

The central execution abstraction.

Purpose:

represent game-relevant state changes in a format-independent way
replace direct SGF execution in the model
enable multiple games to share the same execution pipeline

Examples include:

Move
Pass
Resign
SetupAddStone
SetupSetEdge
SetBoardSpec
SetTopology
SetShape

Important rule:

actions are the unit of execution
SGF properties are not executed directly by the engine
DomainActionApplier

Responsible for:

applying actions to model/game state
enforcing execution semantics
keeping mutation in one place

Important constraint:

action application must match legacy do_() semantics
replay must not accidentally reset model state
GameNode

SGF-free tree node abstraction.

Contains:

List<DomainAction> actions
NodeAnnotations annotations
child nodes

Purpose:

represent the logical game tree independently of SGF
allow SGF to be treated as an adapter format
support non-SGF games
NodeAnnotations / RawProperty

Used to preserve non-core or uninterpreted SGF properties.

Purpose:

lossless round-trip
keep format details out of the engine core
avoid dropping comments, metadata, or custom properties

Important rule:

preservation must not reorder or lose SGF properties
SgfDomainActionMapper

Boundary object between SGF and the engine.

Responsible for:

mapping SGF properties to actions
preserving non-executed properties as raw annotations
keeping SGF logic out of the engine

Important constraint:

SGF mapping must not break exact round-trip behavior
moving properties between “main” and “extra” storage is dangerous unless ordering is preserved
Model

Current orchestration object for Go execution.

Still transitional.

Current responsibilities include:

holding tree/state
replay/navigation
applying actions
coordinating board state

Important constraint:

model behavior must remain compatible with legacy semantics while responsibilities are being extracted
Go-specific abstractions

Includes:

board representation
stones, points, coordinates, blocks
move legality
capture/liberty logic
topology and shape handling
SGF/GTP-specific Go mapping
TTT-specific abstractions

Includes:

TttSpec
TttState
TttMove
TTT action mapper/applier
renderer

Purpose:

validate that the architecture is not Go-specific
act as a control case for engine boundaries
Data flow
Current intended flow
SGF
  → SgfDomainActionMapper
  → GameNode / DomainAction list
  → DomainActionApplier
  → Model / Game state
Round-trip preservation flow
SGF properties
  → actions for semantic behavior
  → RawProperty / NodeAnnotations for lossless preservation
  → save back to SGF without loss or accidental synthesis
3. Important Design Decisions and Constraints
Core design decisions
SGF is an adapter, not the model
actions are the execution currency
game tree is SGF-free (GameNode)
non-semantic SGF data is preserved separately
Tic-Tac-Toe is used to validate multi-game architecture
networking/GTP/client-server are adapter concerns, not core concerns
Hard constraints
Lossless SGF round-trip

Must preserve:

property existence
property ordering
sentinel/root behavior
comments and custom properties
exact or near-exact serialization semantics

This is non-negotiable.

Legacy semantic compatibility

The new execution path must preserve key legacy behavior, especially around:

root handling
replay semantics
stack/history behavior
sentinel node behavior
Determinism

Tests and execution should be deterministic.

Small-step refactoring

Changes should be incremental and test-protected.

Sentinel root constraint

RT currently acts as a sentinel root marker.

Rules:

executing it is effectively a no-op
it must not be duplicated
it must not disappear during mapping
root handling must remain consistent across load, replay, and save
Initialization constraint

There is a distinction between:

one-time initialization
repeatable initialization / reconfiguration

This matters because some refactors incorrectly used setRoot() in replay paths and broke state semantics.

4. Current State
Working
DomainAction / action-based execution exists
SGF → action mapping exists
GameNode, NodeAnnotations, and RawProperty exist
Go still works through the current system
TTT exists and works as a second game
most legacy Move usage has been removed from key paths
most tests pass
SGF round-trip behavior has been restored after regressions
RT sentinel behavior is now understood
Partially complete / transitional
package layout is still in transition
model still owns more orchestration than ideal
SGF boundary is improved but still fragile
some tests are heavily duplicated in tst/sgf
multiple test harness styles coexist
root/project layout still has cleanup debt
some timeout-related tests remain
Not complete
final package reorganization
full elimination of legacy compatibility logic
clear separation of all initialization phases
clean networking/client-server adapter layer
final decision on all SGF metadata handling
complete consolidation of SGF test hierarchy
5. Known Problems / Refactoring Areas
SGF round-trip fragility

Main risks:

moving unmapped SGF properties into “extras” changes ordering/serialization
treating comments or metadata as non-semantic too early breaks round-trip
not checking both normal properties and extras can lose sentinel semantics
Root / sentinel handling

Problems observed:

extra sentinel/root nodes can be synthesized accidentally
root markers can be missed if searched only in one property collection
save/load behavior around synthetic/null root nodes is easy to break
setRoot() misuse

Major semantic hazard:

legacy replay did not call setRoot() during ordinary SGF execution
newer action paths sometimes did
this cleared stacks/history and changed behavior

This must remain carefully controlled.

Model responsibility overload

Model still mixes:

tree orchestration
replay semantics
state mutation
partial legacy behavior
adapter-era concerns

This is an active refactoring area.

SGF test duplication

tst/sgf contains too many overlapping styles:

inheritance-based parser harnesses
fixture-based tests
standalone tests
multiple round-trip pipelines

This increases maintenance cost and obscures intent.

Timeout tests

A few tests still fail due to timeout/flakiness.

Need classification:

true hangs
ordering/state leakage
infrastructure timing problems
Project structure debt

Root contains too much historical clutter.
src/ organization is not fully normalized yet.

6. Next Steps
Immediate priorities
1. Preserve behavioral stability
keep the action path aligned with legacy semantics
avoid new SGF round-trip regressions
protect RT/root behavior
2. Finish legacy move removal
eliminate remaining legacy Move usage from inner loops and core execution paths
3. Clean SGF boundary carefully
preserve lossless behavior
avoid mutating SGF structures in ways that change serialization
make sentinel detection robust
Near-term refactoring work
Test consolidation
reduce duplication in tst/sgf
converge on one primary SGF fixture/pipeline style
separate parser, round-trip, and engine semantics more clearly
Package cleanup
continue moving toward core / games / ui / formats / legacy
keep one project for now
clean project root separately from src/
Initialization cleanup
separate one-time bootstrapping from repeatable initialization/reconfiguration
ensure replay paths do not trigger destructive initialization
Medium-term work
Formalize engine boundary
stabilize DomainAction, GameNode, and applier contracts
reduce model responsibilities further
Clarify metadata policy
decide which SGF properties:
become executable actions
remain preserved-only annotations
defer “RawProperty-only everywhere” until exact round-trip is guaranteed
Prepare adapter expansion
reintroduce GTP/network/client-server only as adapter layers
keep them out of the engine
Summary

The program is a Java Go system being transformed into a multi-game architecture with:

action-based execution
SGF as an adapter
SGF-free game tree representation
lossless property preservation
Go as the primary game
TTT as the architecture validation game

The architecture direction is correct. The main remaining work is stabilization, cleanup, and consolidation without breaking legacy semantics or SGF fidelity.