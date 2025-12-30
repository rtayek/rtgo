Your proposed layout is directionally solid: it draws a clean line between (a) game-agnostic “engine/API” concerns, (b) game implementations (Go, TTT), and (c) UI / transport / file formats as adapters. With one important adjustment for a single-project codebase:

* Prefer **packages** like `core.*` and `games.*` over creating new **top-level source folders** like `go/` and `ttt/` under `src/`.
* In other words, keep `src/` as the single source root, and express structure via package names.

That avoids Gradle/Eclipse sourceSet churn and keeps refactors mechanical.

## Recommended `src/` package structure (single project)

### 1) Core (game-agnostic)

Use **`core.*`** as the stable base:

* `core.api`
  Public-facing plugin surface: `GamePlugin`, `GameSession`, `Renderer`, `RolePolicy`, `GameState`, codecs, etc.
* `core.engine`
  Game-agnostic execution substrate: `Action` (or `DomainAction`), appliers, `GameNode`, `NodeAnnotations`, `RawProperty`, traversal utilities.
  (If you keep a single “neutral” action model, it belongs here.)
* `core.engine.applier`
  `DomainActionApplier` / `ActionApplier` interfaces + shared wiring.
* `core.formats.sgf`
  SGF boundary: `SgfDomainActionMapper` that converts **SGF → (core actions + RawProperty extras)**.
* `core.util`
  Truly shared helpers only (no “misc dump”). Logging wrappers, tiny collections utils, etc.

This is where your “Option B: RawProperty-only annotations” lives most naturally: keep **all non-core SGF data** as `RawProperty` stored in `NodeAnnotations`, produced only by format adapters (`core.formats.sgf`), never by engine code.

### 2) Games

Use **`games.<id>.*`** packages:

* `games.go.rules`
  Pure Go rules/state transitions that do not know about SGF/GTP/GUI.
* `games.go.adapters`
  Go-specific mapping/codec edges (GTP, Go-specific SGF interpretations if any remain).
* `games.go.render`
  CLI/ASCII renderer (and possibly GUI renderer façade).
* `games.go.ui.swing` (optional)
  Swing UI bits *if* you keep them Go-specific; otherwise put generic UI under `ui.swing`.
* `games.go`
  `GoPlugin`, wiring, factories.

And similarly:

* `games.ttt.rules`
* `games.ttt.adapters` (your `TttActionMapper`, `TttActionApplier`)
* `games.ttt.render`
* `games.ttt` (`TttPlugin`)

This keeps the “multi-game support” honest: each game owns its rule set, and adapters are explicitly not core.

### 3) UI / transports / legacy

* `ui.swing`
  App-level Swing, shared widgets, “host shell” UI. If your current GUI is mostly Go-specific, start with `ui.swing` anyway and migrate later.
* `cli`
  The top-level CLI host that selects a plugin by id and delegates.
* `formats.*` (optional)
  If you later add JSON/PGN/etc, keep them outside games unless they truly are game-specific.

## How this maps to your existing “too many packages”

Without needing to change behavior:

* `equipment.*` → either `games.go.rules.board.*` **or** `core.board.*`
  If you genuinely intend other games to reuse it, promote it to `core.board`. If it’s Go-shaped (liberties, captures, ko-ish assumptions), keep it under Go.
* `model.*` → split:

  * “engine-ish” parts → `core.engine.*`
  * “Go orchestration” → `games.go.*`
  * Any remaining “app model” glue → `ui.*` or `cli.*`
* `sgf.*` → `core.formats.sgf.*` (parsing/tree types)
  But keep “SGF is a file format” out of game rules and out of `core.engine`.
* `controller.*`, `server.*`, `gtp.*` → `games.go.adapters.gtp.*` (or `transports.gtp.*` if you generalize later)
* `io.*` → usually `core.util` (if generic) or `games.go.*` (if Go-only)
* `tree.*` → likely `core.util.tree` or `core.formats.sgf` depending on what it really represents.

## About your proposed layout snippet

Your proposed tree is conceptually fine, but I would express it like this **as packages**:

* `core.engine` / `core.api` / `core.formats.sgf` / `core.util`
* `games.go.rules` / `games.go.adapters` / `games.go.render` / `games.go.ui.swing`
* `games.ttt.rules` / `games.ttt.adapters` / `games.ttt.render`

That yields the same intent with less structural friction.

## When to do Tic-Tac-Toe work

If TTT already exists and tests pass, you’re at the right time. The next useful “keep us honest” step is not adding features, but ensuring your host surfaces are truly game-agnostic:

* CLI chooses plugin by id (already underway).
* Core action/application path is neutral.
* SGF stays in `core.formats.sgf`, and *extras* are **RawProperty annotations only**.

TTT is the canary: if you have to import `sgf.*` or Go board types to get TTT to work, your boundaries are still leaky.

