I can’t re-open or enumerate your latest tar right now: the previously uploaded archive(s) have expired on my side. If you want an exact “`ls`-style” listing of *your current* tree, please re-upload the current `code7.tar.gz` (or paste the output of `find src -maxdepth 4 -type d | sort` / `tree -d -L 4 src` from your machine).

That said, I *can* evaluate the proposal on its merits, and I think your evil twin’s layout is directionally correct and consistent with the boundaries you’ve been enforcing.

## Assessment of the proposed `src/` package layout

### What I agree with (keep)

* **`games/<id>/...`** as the unit of modularity is right. It forces “game-self-contained” thinking and makes TTT genuinely useful as a separation test.
* **`core/formats/sgf`** as the *adapter* boundary is right, *if and only if* it emits **core actions + RawProperty** and does not leak SGF types downstream.
* **A dedicated `ui/cli` host** package is right: plugin selection, session wiring, and command routing should live there, not in any game.
* **`legacy/...` quarantine** is absolutely worth doing early. It is the cheapest way to prevent regression-by-import.

### The one part I would tighten

Right now the proposal has both **`core/engine`** *and* optionally **`model/engine`**. That split can be clean, but it’s also where projects go to die from “where does X belong?” debates.

My recommendation: **pick one “engine algebra” home** and make it boring.

* If you want “core is framework, model is algebra”: put `Action/DomainAction`, `GameNode`, `NodeAnnotations`, `RawProperty` in **`model.engine`** and keep `core.*` as runtime wiring (plugins, sessions, applier interfaces).
* If you want “core owns the algebra”: fold it all into **`core.engine`** and delete `model.engine` entirely.

Either is fine. The only bad option is “both, forever.”

Given you explicitly chose **Option B: RawProperty-only**, I’d lean toward **putting the algebra in `core.engine`** unless you have a strong reason to keep a separate “model layer.” You already have “games as domains”; a second “model domain” layer can become redundant.

### Rule of imports (non-negotiable)

These three rules keep the architecture from collapsing:

1. **`formats.*` is outbound only**
   `formats.* → core.* / games.*`
   Never `games.* → formats.*`, never `core.engine → formats.*`.

2. **Games depend on core, not vice-versa**
   `games.* → core.api/core.engine`
   Never `core.* → games.*`.

3. **UI depends on core + games, but never contains rules**
   Rendering is read-only from state; state mutation happens via actions/appliers.

## A concrete “ls-style” tree (what I’d standardize on)

Here’s what I would show as the “target” directory list (directories only), in the style you asked for:

```
src/
  core/
    api/
    engine/
      applier/
    formats/
      sgf/
      gtp/
    util/
  games/
    go/
      rules/
      adapters/
      render/
      ui/
        swing/
    ttt/
      rules/
      adapters/
      render/
  ui/
    cli/
    swing/
  legacy/
```

Notes:

* I moved `core/formats/sgf` under `core/formats/` so SGF and GTP can sit side-by-side as “format adapters.”
* I left `ui/swing` as a place for shared shell/widgets; game-specific Swing panels stay under `games/*/ui/swing`.

## How this interacts with Option B (RawProperty-only)

If you go RawProperty-only, then:

* **Do not keep `Action.Metadata` as a general-purpose escape hatch** in the engine path. If you keep it, it should be *UI-only*, not part of “apply-to-state” flows. Otherwise every applier grows a junk drawer and you lose exhaustiveness.
* SGF mapping should produce:

  * `Action.Play/Pass/Resign/SetBoard...` (the true domain-affecting subset), plus
  * `RawProperty` stored in `NodeAnnotations.unapplied` for everything else.

That matches your goal: “preserve data without contaminating the engine.”

## On the evil twin’s specific bullets about TTT

All four suggestions are reasonable; I’d prioritize them in this order:

1. **Move coordinate validation to `TttMove.Place` (or its factory)**
   This keeps “invalid move” semantics in one place and reduces mapper/applier duplication.

2. **If `Action.Metadata` is not needed for core flows, delete or quarantine it**
   This supports your Option B choice and keeps appliers exhaustively handled.

3. **Encapsulate `TttSpec` and align it with `TttState` getters**
   Minor hygiene, but it prevents “spec as a public struct” drift.

4. **Renderer helper in `TttPlugin`**
   Fine, but cosmetic; do it when you touch rendering anyway.

---

If you re-upload the current tar, I’ll produce the *actual* `src/` folder structure “like `ls`” from your project, and I’ll also point out any boundary violations (imports crossing the wrong direction, SGF types leaking into games, etc.).
