Keeping things in `model` for a while is a sensible “baby step,” as long as you enforce **boundaries by type**, not by folder name. You can get most of the architectural benefit now, then physically move packages later when the dust settles.

Also: yes—**doing tic-tac-toe soon is a good idea**, but in a very specific, minimal way.

## 1) “Keep it in model for now” without losing separation

Use **subpackages** under `model` to create boundaries while keeping everything “in one place”:

```
model/
  engine/
    DomainAction.java
    GameNode.java
    NodeAnnotations.java
    Model.java

  formats/
    sgf/
      SgfToDomainActionMapper.java
      DomainActionToSgfWriter.java   // later
      RawProperty.java               // or keep in engine/
    gtp/
      GtpMoveCodec.java              // later

  go/
    GoDomainRules.java               // if you need go-only helpers

  util/
    ModelHelper.java                 // transitional glue, but keep it thin
```

Key rule: **`model.engine.*` must not import `sgf.*`**.
Even if `formats/sgf` is still inside `model`, the dependency direction is clean:

* `model.formats.sgf` → `model.engine`
* not the other way around

That gives you “one place” without the “everything depends on everything” problem.

### Practical baby step you can do immediately

* Keep `DomainAction.applyTo(Model)` where it is (fine for now).
* Move only the SGF mapping methods out of `DomainAction` into:

  * `model.formats.sgf.SgfToDomainActionMapper`

No functional change; you just stop importing SGF types inside the engine surface.

## 2) When is a good time to do tic-tac-toe?

**Soon, but tiny.** The right moment is:

> As soon as `GameNode + DomainAction + Model.apply(...)` compiles cleanly without SGF participation.

You are basically there.

### What tic-tac-toe is for (in your project)

Not “features.” It’s an **architecture test**:

* Can you run a full game without SGF?
* Can your “engine actions” be game-agnostic?
* Does your plugin/session boundary make sense?
* Does the CLI select a plugin by id cleanly?

### Minimal TTT scope (1–2 evenings, not a new project)

Do *only* this:

1. `games.ttt.TttPlugin` with an id (`"ttt"`)
2. `TttState` with a 3×3 array
3. `TttMove` as `(row, col)` (plus optional `pass` if you want symmetry, but not needed)
4. A renderer that prints the board
5. A CLI loop that plays two humans locally (no network, no SGF)

No AI. No server. No file formats.

### When *not* to do TTT yet

If you are still actively changing:

* how `Model` stores current state,
* how you represent moves,
* how apply/do2 is structured,

then do **Step 1 only**: a compiling `TttPlugin` skeleton that does nothing but identify itself. That still buys separation.

## 3) A “baby steps” sequence that keeps you moving

1. **Freeze engine API surface** (even if it lives under `model/`)

   * `DomainAction` (engine only)
   * `GameNode`
   * `NodeAnnotations` (prefer raw properties, but can wait)
   * `Model.apply(...)`

2. **Move SGF mapping behind a façade**

   * `SgfToDomainActionMapper` in `model.formats.sgf`
   * `Model` calls the mapper; does not interpret SGF

3. **Create TTT as a plugin that uses only engine types**

   * If this is easy, your boundaries are working.
   * If this is painful, the pain is telling you exactly where coupling remains.

## One important operational note

I can’t currently re-open your uploaded tar files in this session because they’ve expired on my side. If you want me to tailor the subpackage names and identify the exact files that should move (e.g., the SGF mapping methods currently sitting in `DomainAction`), please re-upload `code7.tar.gz` once more.

If you don’t want to re-upload, paste just the headers (package + imports) of:

* `model.DomainAction`
* `model.ModelHelper`
  and I can still tell you exactly where the dependency boundary is leaking and how to fix it with minimal edits.
