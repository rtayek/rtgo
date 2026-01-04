Below is a **clean, engine-handoff–ready project summary** you can paste into a new chat to re-establish full context quickly.

---

## Project Summary (RTGO / multi-game refactor)

### What this project is

A long-lived Java Go program (≈25 years old) undergoing a **major architectural refactor** to:

* Remove SGF and protocol logic from the core game model
* Support **multiple games** (Go + Tic-Tac-Toe already working)
* Introduce a **game-agnostic action pipeline**
* Preserve **lossless SGF round-tripping**
* Prepare for plugins, CLI, and future client/server adapters

The codebase currently builds cleanly and all functional tests pass, with a few timeout-related tests remaining.

---

## Core architectural decisions

### 1. Domain actions replace “do_()”

The original inner loop was:

```java
Model.do_(MNode node)
```

which:

* Walked SGF properties directly
* Mutated board/state inline
* Mixed parsing, rules, UI signals, and history

This has been replaced with:

```
SGF → DomainAction[] → DomainActionApplier → Model
```

**Key concept:**
SGF is *interpreted* into **DomainActions**, then applied.

---

### 2. DomainAction (engine-level, SGF-free)

`DomainAction` is now the **core execution unit**.

Typical actions:

* `SetBoardSpec(width, depth)`
* `SetTopology`
* `SetShape`
* `SetupAddStone`
* `SetupSetEdge`
* `Move`
* `Pass`
* `Resign`

Properties:

* **No SGF types**
* No parsing
* No UI knowledge
* Can be reused by any game (Go, TTT, future games)

Execution is centralized in:

```java
DomainActionApplier
```

---

### 3. SGF is now a format adapter

SGF logic lives exclusively under:

```
core.formats.sgf.SgfDomainActionMapper
```

Responsibilities:

* Convert SGF properties → DomainActions
* Preserve **unmapped / non-core properties** for round-trip
* Never mutate Model directly

**Important rule:**

> Core / engine code must never import SGF packages.

---

### 4. Lossless SGF round-tripping (critical)

SGF round-trip must be exact.

To support this:

* Unmapped SGF properties are preserved as **extras**
* Stored as:

  ```java
  RawProperty(id, values)
  ```
* Attached to nodes via:

  ```java
  NodeAnnotations
  ```

This ensures:

* Comments, metadata, unknown properties are not lost
* Reload → save produces identical SGF

---

### 5. GameNode replaces SGF-centric tree logic

The model now operates on:

```java
GameNode {
  List<DomainAction> actions;
  NodeAnnotations annotations;
  List<GameNode> children;
}
```

This is:

* SGF-free
* Game-agnostic
* Suitable for non-SGF games (TTT, future games)

SGF trees are converted **once** at the boundary.

---

### 6. RT property = sentinel node

* `RT` is a **sentinel root node**
* Executing it is a **no-op**
* The legacy code implicitly ignored it
* The new pipeline initially failed to, causing:

  * Extra root nodes
  * Stack resets via `setRoot()`
* This was fixed by:

  * Treating RT as a no-op
  * Ensuring root handling matches legacy behavior

RT could technically be *any* no-op marker; it is historical.

---

### 7. Tic-Tac-Toe added as a control case

A minimal TTT game was added to:

* Validate multi-game architecture
* Ensure SGF-free games work cleanly
* Prove DomainAction abstraction is sound

TTT uses:

* Its own state/spec/move types
* Action mapping + applier
* No SGF, no legacy dependencies

TTT tests pass.

---

### 8. What is *not* in the model anymore

The following are being pushed outward or quarantined:

* SGF parsing logic
* GTP protocol handling
* UI rendering
* Legacy Move subclasses
* Network/server code (treated as adapters)

Model responsibility is now:

> **Apply actions to state and maintain game tree**

---

## Current state

* Build succeeds
* Core refactor stable
* Legacy `Move` almost entirely removed
* SGF round-trip restored
* RT sentinel handled correctly
* Some tests still timeout (non-logic issue)

---

## Open / future work

* Project root cleanup (67 top-level items → prune cruft)
* Finalize `src/` package layout
* Move remaining legacy code into quarantine
* Add plugin selection in CLI
* Optional client/server adapter layer (non-core)

---

## One-sentence mental model

> *SGF and protocols are adapters that produce game-agnostic actions; the model only applies actions and owns the game tree.*

---

If you want, next I can:

* Produce a **one-page architecture diagram**
* Give a **step-by-step cleanup plan for the project root**
* Write a **“rules of the road” doc for future contributors**
* Help you re-introduce client/server cleanly as adapters
