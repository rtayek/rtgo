---

## Context

This project is a long-running refactor and architectural cleanup of a Java game framework originally centered around **Go (Baduk)** with heavy **SGF** and **GTP** integration. The original codebase tightly coupled SGF parsing, game state mutation, move logic, UI updates, and persistence inside a monolithic `Model` and `Move` hierarchy.

Over time, this has been reworked into a **layered, decoupled architecture**:

* A **core / engine layer** that understands *game-agnostic domain actions* (play, pass, resign, setup, configuration).
* **Format adapters** (SGF, GTP) that *map external representations* into those domain actions.
* **Game-specific implementations** (Go, Tic-Tac-Toe) that apply actions to game state.
* A tree-based model that can still **round-trip SGF losslessly**, including unknown or unhandled properties.

A major theme has been **separating SGF from the model** while still preserving *exact round-trip fidelity*. SGF nodes are now interpreted into actions plus annotations, instead of directly mutating model state. A sentinel SGF property (`RT`) is used as a no-op root marker, matching legacy behavior.

A minimal **Tic-Tac-Toe (TTT)** implementation was added in parallel to keep abstractions honest and avoid Go-specific leakage.

---

## Goals

* Finish stabilizing the **DomainAction-based execution path** so it exactly matches legacy behavior.
* Preserve **exact SGF round-trip fidelity** (no added, removed, or reordered properties).
* Eliminate remaining uses of the **legacy Move class** in inner loops.
* Finalize the **package layout** so responsibilities are clear and enforceable.
* Clean up the **project root** (too many top-level folders / cruft).
* Keep Go and TTT both working as first-class games using the same core engine.

---

## Non-goals / Constraints

* **No behavior changes** to existing Go logic unless required for correctness.
* **No loss of SGF information**, even for properties not currently interpreted.
* **Java only**, no Kotlin/Scala.
* Deterministic, testable behavior is required (many unit tests already exist).
* Networking (client/server, GTP servers) is **treated as an adapter layer** and is not being expanded right now.
* Not splitting into multiple Gradle/Maven projects yet — staying in one project for now.
* Performance optimizations are secondary to correctness and clarity.

---

## Current State

### What exists and works

* **DomainAction system** (sealed interfaces / records) representing:

  * Engine actions (Move, Pass, Resign, Setup, etc.)
  * Config actions (SetBoardSpec, SetTopology, SetShape)
* **DomainActionApplier** applies actions to the model instead of SGF directly mutating it.
* **SGF → DomainAction mapping** lives in `core.formats.sgf.SgfDomainActionMapper`.
* **RawProperty + NodeAnnotations** preserve unhandled SGF properties for lossless round-trip.
* **GameNode tree** represents SGF structure without SGF types leaking into the engine.
* **RT property** acts as a sentinel root; executing it is a no-op (matching legacy `do_()`).
* **Tic-Tac-Toe game** implemented using the same action pipeline:

  * TTT rules/state
  * TTT action mapper + applier
  * Tests pass
* Most unit tests pass; failures have been traced and fixed by reverting problematic commits.

### What was fixed recently

* Incorrect use of `setRoot()` inside domain action execution (was clearing state incorrectly).
* Accidental introduction of extra `RT` nodes due to not checking **extra properties**.
* Ensured first node execution semantics match legacy behavior.
* Restored SGF comment handling to avoid breaking round-trip.

### What is fragile or unresolved

* SGF handling is still **easy to break** if mapping decisions change (e.g., treating comments as actions vs extras).
* Some timeout-related test failures remain (likely infrastructure / threading).
* Project root is cluttered (≈67 items).
* Package boundaries are conceptually clear but not fully enforced everywhere.
* Networking / GTP code still exists but is partially legacy.

---

## Open Questions / Next Steps

1. **Package layout finalization**

   * Should `core/engine` fully absorb `model/engine`, or keep them separate?
   * Where should legacy code live long-term (`legacy/` quarantine)?

2. **SGF policy**

   * Which SGF properties (if any) should *ever* become DomainActions vs always RawProperty?
   * Is it acceptable to treat everything except a minimal core as annotations?

3. **RT sentinel**

   * Is `RT` the best long-term sentinel, or should the engine treat root as implicit?
   * Should sentinel handling be centralized?

4. **Project root cleanup**

   * What can be deleted vs archived?
   * How aggressively to move old folders into `legacy/`?

5. **Client/server**

   * When to reintroduce networking adapters?
   * How to layer them cleanly above the core engine?

6. **Testing**

   * Which timeout tests are real bugs vs environmental?
   * Should long-running tests be isolated?

---
