Option B, finalized (what it means in code)

Rule of thumb:
Only game semantics become Actions. Everything else stays as data in NodeAnnotations.

What becomes an Action

Play (or Move)

Pass

Resign

SetBoard (size/topology/shape) — optional but reasonable

These must be:

deterministic

game-agnostic at the core.engine level

applied by a game-specific applier (GoActionApplier, TttActionApplier, etc.)

What becomes RawProperty (annotations only)

All non-semantic SGF properties, including:

KM, HA, FF, GM, AP, BL, WL, RE

comments not explicitly encoding board shape/topology

unknown / private properties

These live only in:

GameNode.annotations().unapplied(); // List<RawProperty>


They do not:

affect game state

appear in the action stream

require engine knowledge

Compatibility with the neutral Action + TTT work

Your evil twin’s changes:

neutral core.engine.Action

games.ttt.TttActionApplier

TttActionMapper

tests passing

This is fully compatible with Option B, provided that:

SGF mappers never emit Action.Metadata

Action.Metadata (if kept) is treated as optional and unused by SGF

TTT continues to reject unsupported actions (already good design)

You are not throwing away that work—you’re simply choosing that SGF is not a producer of metadata actions.

Small, safe next step (recommended)

Once you re-upload the tar, I recommend exactly this baby step:

Locate the SGF → action mapping class (e.g. SgfDomainActionMapper)

Remove / comment out any creation of:

DomainAction.Metadata

Action.Metadata

Replace that logic with:

nodeAnnotations.unapplied().add(new RawProperty(id, values));


Ensure only one place owns extras:

GameNode.annotations

not SGF nodes + annotations both

This should:

preserve round-trip tests

not break TTT

not affect core game semantics

make multi-game support cleaner

About Tic-Tac-Toe timing

You were right to do TTT now. It has already paid off by:

forcing game-agnostic actions

validating that SGF ≠ model

proving that apply logic belongs outside the model

Once SGF → RawProperty is clean, TTT becomes the reference implementation for how little a game needs to participate in the engine.

codex:

hanges made:

TttActionMapper.java: validate moves against a provided TttSpec, rejecting off-board coordinates.
TttActionApplier.java: exhaustive switch over all Action variants with clear rejections for unsupported types.
TttState.java: encapsulated state fields, added safe accessors and defensive copy helpers.
TttPlugin.java: updated to use new state accessors and defensive mark copying.
TttActionApplierTest.java: updated for mapper signature, added metadata rejection test.