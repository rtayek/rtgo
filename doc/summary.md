---

# RTGO / Multi-Game Engine – Project README (for Future-Me)

## Context

This project is a long-lived Java codebase (originating ~25 years ago) that began as a Go (baduk) application and has been progressively refactored into a **multi-game engine** with Go and Tic-Tac-Toe (TTT) as concrete implementations. The original architecture tightly coupled the model to SGF parsing, legacy Move hierarchies, and UI/IO concerns. Recent work has focused on **systematically decoupling domain logic from file formats (SGF, GTP), UI, and transport**, while preserving exact SGF round-trip fidelity.

The central refactor introduces a **game-agnostic action pipeline**:

* External formats (SGF, later GTP/networking) are mapped to **neutral domain actions**.
* Actions are applied to game state via **game-specific appliers**.
* All non-semantic or unsupported SGF properties are preserved verbatim as annotations to guarantee lossless round-tripping.

A minimal Tic-Tac-Toe implementation was added in parallel to keep abstractions honest and prevent Go-specific leakage into the core.

---

## Goals (Current Focus)

* Finish **decoupling SGF from the core model** while preserving exact SGF round-trip behavior.
* Eliminate remaining **legacy Move** usage in inner loops.
* Stabilize the **DomainAction → Applier** pipeline so Go and TTT both use it cleanly.
* Clean up **project root cruft** and converge on a sane `src/` package structure.
* Keep changes incremental (“baby steps”), with tests passing at each step.

---

## Non-Goals / Constraints

* **Not** rewriting the project or changing observable Go behavior.
* **Not** losing *any* SGF information (round-trip must be byte-stable modulo formatting).
* **Not** committing to client/server or networking yet (treated as adapters, deferred).
* **Not** prematurely optimizing or generalizing beyond Go + TTT.
* Hard constraints:

  * Java (current toolchain already in use).
  * Deterministic execution.
  * Eclipse-friendly refactoring (used heavily).
  * Tests are the specification.

---

## Current State

### What Exists and Works

* **Core action model**:

  * Neutral `DomainAction` (or `Action`) hierarchy.
  * Game-specific appliers (Go, TTT).
* **SGF boundary split**:

  * `SgfDomainActionMapper` converts SGF → actions + preserved extras.
  * `RawProperty` / `NodeAnnotations` store unapplied SGF properties losslessly.
* **Game tree**:

  * SGF-free `GameNode` tree holding actions + annotations.
  * Sentinel root node (`RT`) used as a no-op anchor (historically implicit).
* **Tic-Tac-Toe**:

  * Minimal but real implementation.
  * Uses the same action pipeline as Go.
  * Tests pass and validate abstraction boundaries.
* **Go**:

  * Board logic, captures, topology/shape handling still intact.
  * Legacy move usage mostly eliminated.
* Most unit tests pass; a few timeout-related tests are known and understood.

### What Was Tricky / Recently Fixed

* A regression where the new action path:

  * Called `setRoot()` too eagerly (clearing stacks).
  * Mishandled the SGF root/sentinel (`RT`) causing extra nodes.
* Root cause: mixing “apply actions” with “reset model” semantics.
* Fix involved reverting several commits and re-aligning with old `do_()` semantics:

  * Executing the sentinel node must be a no-op.
  * Root creation must be explicit and rare.

---

## Open Questions / Next Things to Work On

1. **Finalize SGF handling policy**

   * Temporarily *not* doing “Option B” (RawProperty-only) everywhere.
   * Possibly re-introduce limited `Metadata` actions for critical properties.
   * Decide which SGF properties are *semantic* vs *purely archival*.

2. **DomainAction responsibilities**

   * Should `SetBoardSpec` ever call `setRoot()`?
   * Or should root creation be fully external (loader/controller only)?

3. **Project structure cleanup**

   * Reduce the 60+ items at project root.
   * Decide final `src/` layout (core / games / formats / ui / legacy).
   * Quarantine remaining legacy code explicitly.

4. **Test stabilization**

   * Fix or reclassify timeout tests.
   * Add more round-trip and cross-game sanity tests.

5. **Networking (later)**

   * Client/server as adapter layer only.
   * Likely mirrors SGF/GTP approach (map → action → apply).

---

## Design Principles (Implicit but Important)

* **Lossless first**: never drop information at format boundaries.
* **Actions over commands**: parse first, apply later.
* **One-way dependencies**:

  * formats → core → games
  * never games → formats
* **No domain logic in UI or IO**.
* **Minimal abstractions, validated by TTT**.
* **Refactor in small, reversible steps**.

---
