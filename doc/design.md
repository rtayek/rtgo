---

## Design principles

### 1) Separation of concerns by dependency direction

* **Core engine/domain** must not depend on **formats** (SGF/GTP) or UI.
* **Formats** are *adapters*: they translate external representations into **engine/domain actions + annotations**, and back.
* **Games** (go, ttt) depend on **core** (and optionally shared board/equipment), but should not import SGF types.
* **UI** renders state and routes input; it does not contain rules.

**Rule of thumb:** dependencies flow *inward*:
`formats → core/engine → games → UI` is the wrong direction; invert it:
`UI → core/api → games/rules` at runtime, but **code dependencies** should keep **formats** out of the engine and games.

### 2) “Actions” as the integration surface

* State mutation should happen by applying **small, explicit, domain-level actions** rather than executing SGF nodes directly in the model.
* SGF node/property interpretation is a *mapping step* that produces:

  * **Actions** (things the engine truly understands), and
  * **Raw/unapplied properties** (things preserved for round-trip, metadata, UI, etc.).

This avoids “SGF is the model” and makes multi-game support realistic.

### 3) Lossless SGF round-trip is non-negotiable

* If round-trip matters, **never drop or rewrite SGF properties**.
* Unrecognized or currently-unmodeled properties must be preserved as **extras/annotations**.
* If you introduce a sentinel (like `RT`) for internal structure, treat it as a **no-op** in the engine and ensure it round-trips identically.

### 4) Keep mapping pure, keep application explicit

* A mapper should ideally be:

  * deterministic,
  * side-effect-free (or at least very tightly controlled),
  * and not mutate core state except by emitting actions.
* The applier is the only place that mutates model/game state.

### 5) “Networking is an adapter”

* Treat client/server/network transport as another adapter layer:

  * it serializes/deserializes **actions** (or moves → actions),
  * it does not contain rules.
* You can remove or quarantine `net/` until you have a stable action protocol.

### 6) Multi-game support via plugins, not conditionals

* No `if(game == GO)` in the engine.
* A game contributes:

  * state/rules,
  * action applier or move→action mapper,
  * renderer(s),
  * optional codecs.

### 7) Tic-tac-toe exists to prevent self-deception

* Keep TTT minimal, clean, and fast.
* Use it as a canary for architectural drift: if a change makes TTT complicated, the abstraction is probably wrong.

---


### Tests define behavior

* Tests are the functional spec.
* Preserve round-trip tests and add regression tests for every “SGF weirdness” edge case you discover.
* Prefer small unit tests plus a few high-value end-to-end tests (e.g., SGF load → apply → save equals input).

### Error handling

* Fail fast on programmer errors (illegal state, impossible cases).
* For malformed external input (SGF/GTP), prefer:

  * capture as `RawProperty` extras when possible,
  * deterministic recovery,
  * explicit logging at adapter boundaries.

### Logging and performance

* Keep logs out of hot loops unless gated; “inner loop” paths should avoid chatter.
* Avoid allocations in tight loops when it’s easy not to (your “legacy move in inner loop” concern is exactly right).

### Refactoring practice (Eclipse-friendly)

* Prefer mechanical refactors:

  * Move method/class (keeps references correct),
  * Extract class,
  * Introduce parameter object,
  * Replace conditionals with polymorphism/sealed switch.
* Keep changes in “baby steps”: compile + tests at every small commit.

---

## Architectural guardrails to enforce (practical)

* Add package-level “don’t import X” discipline socially, and optionally enforce with:

  * a simple grep check in a script, or
  * build tooling later (but you said “one project for now,” so keep it light).
* Explicitly quarantine legacy:

  * `legacy.*` packages, or
  * `old.*`, or
  * `deprecated.*`,ls *.md
  
    so accidental reuse is obvious.

---
