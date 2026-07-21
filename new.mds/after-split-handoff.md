Good. This is actually a **high-quality handoff**—rare. Now let’s turn it into something *usable* instead of something you keep re-pasting like a caveman.

I’m going to do three things:

1. **Refactor it into proper `.md` files** (clean separation)
2. **Trim redundancy / sharpen constraints**
3. Give you a **drop-in starter prompt**

---

# 1. Split into canonical files

Your current doc is solid but **too monolithic**. Break it like this:

```
go/
  overview.md
  architecture.md
  abstractions.md
  constraints.md
  current-state.md
  next-steps.md
```

---

## overview.md

# Overview

Java-based Go program being refactored into a multi-game framework.

Primary goals:

* Preserve Go behavior correctness
* Preserve exact SGF round-trip behavior
* Separate engine from SGF/GTP/UI concerns
* Support multiple games via a shared action-based architecture

Games:

* Go (primary)
* Tic-Tac-Toe (validation / control)

---

## architecture.md

# Architecture

## Layers

### Core / Engine

* Execution model
* Game tree (GameNode)
* Action application
* Annotation preservation
* No dependency on SGF, GTP, or UI

### Game-specific logic

* Rules and legality
* State transitions
* Board semantics
* Renderers/adapters

### Format adapters

* SGF → DomainAction mapping
* Preserve uninterpreted properties
* Future: GTP, network codecs

### UI / Transport

* Rendering and input
* CLI / Swing / future client-server
* No game rules

## Dependency Direction

formats → core → games → UI
(runtime inversion allowed, compile-time firewall enforced)

## Data Flow

SGF
→ SgfDomainActionMapper
→ GameNode (DomainAction list)
→ DomainActionApplier
→ Model / GameState

## Round-trip Flow

SGF properties
→ actions (semantic)
→ annotations (non-semantic)
→ serialized back without loss or reordering

---

## abstractions.md

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

---

## constraints.md (this is your most important file)

# Constraints

## Non-negotiable

### Lossless SGF round-trip

Must preserve:

* property existence
* ordering
* sentinel/root behavior
* comments and custom properties

### Engine purity

* No SGF/GTP/UI dependencies in core

### Execution model

* Actions are the execution currency
* SGF is an adapter only

### Determinism

* Execution and tests must be deterministic

## Sentinel Root (RT)

* Acts as root marker
* Execution is a no-op
* Must not be duplicated or lost
* Detection must consider all property storage locations

## Initialization

* Distinguish:

  * one-time initialization
  * replay-time initialization
* Replay must not call destructive initialization (e.g., setRoot())

## Legacy compatibility

* Must preserve:

  * replay semantics
  * stack/history behavior
  * root handling behavior

---

## current-state.md

# Current State

## Working

* Action-based execution implemented
* SGF → action mapping working
* GameNode / annotations implemented
* Go functional
* TTT functional
* SGF round-trip restored
* RT sentinel behavior understood

## Transitional

* Package layout incomplete
* Model still overloaded
* SGF boundary fragile
* Test duplication in tst/sgf
* Multiple test harness styles

## Not complete

* Final package structure
* Full removal of legacy compatibility logic
* Initialization separation
* Networking/GTP adapter layer
* SGF metadata policy finalization

---

## next-steps.md

# Next Steps

## Immediate

* Preserve behavior and SGF round-trip stability
* Protect RT/root behavior
* Finish legacy Move removal

## Near-term

* Consolidate SGF tests into one pipeline
* Continue package cleanup (core / games / ui / formats)
* Separate initialization phases

## Medium-term

* Formalize engine boundaries (DomainAction, GameNode, Applier)
* Reduce Model responsibilities
* Define SGF metadata policy
* Add adapters (GTP, networking) outside core

---

# 2. What you just gained

You now have:

* **orthogonal documents**
* minimal duplication
* clear invariants (constraints.md is your “spec”)

This is now usable in chats.

---

# 3. How to start your next chat (exact prompt)

Use this:

You are working on a Java-based multi-game engine (Go + Tic-Tac-Toe).

Context:

[architecture.md] <PASTE>

[abstractions.md] <PASTE>

[constraints.md] <PASTE>

[current-state.md] <PASTE ONLY IF RELEVANT>

Rules:

* Do not violate constraints.md
* Do not invent architecture outside these documents
* Ask if something is unclear instead of guessing

Task: <your actual question>

---

# 4. Critical advice (this will save you)

### Do NOT paste everything every time

* Architecture question → architecture + constraints
* Bug → current-state + constraints
* Design → abstractions + constraints

### constraints.md is your guardrail

If the model drifts, point to it.

---

# 5. Blunt assessment

Your handoff was already strong.

The real problem wasn’t quality—it was:

> **lack of modularization and reuse discipline**

Now you have that.

---

If you want next-level rigor, I can:

* formalize your **import firewall rules**
* define **illegal dependency checks**
* or design the **DomainAction type hierarchy properly**

That’s where your architecture will either hold—or quietly rot.
