Understood. I’ll stay conceptual and precise and won’t rely on opening the tar.

You asked two things in this phase:

1. **Explain the RawProperty-only (Option B) choice clearly**
2. **Confirm whether this is the right moment for minimal Tic-Tac-Toe**

I’ll do both, anchored in what you and your evil twin have *already* built.

---

## 1. Option B (RawProperty-only annotations) — what it really means

You now have **three conceptual layers**, whether you name them that way or not:

### A. Engine / Game Core

This is the part that:

* knows **nothing** about SGF
* knows **nothing** about persistence formats
* only understands:

  * board
  * pieces
  * turns
  * legality
  * game-over conditions

This layer consumes **Actions** (or DomainActions) and mutates game state.

> This is where Go and TTT differ, but the *shape* of the API is the same.

---

### B. Action stream (the “semantic minimum”)

This is the *meaningful* sequence of things that affect game state:

Examples:

* `Play(color, point)`
* `Pass(color)`
* `Resign(color)`
* `SetBoard(width, height)`
* `SetupAddStone(color, point)`
* `SetTopology(...)`

These are:

* **format-independent**
* **game-relevant**
* **replayable**

If you replay just these, you get the same board and turn state.

---

### C. Annotations / Extras (RawProperty)

This is where Option B lives.

**RawProperty means:**

> “This information existed in the input format, but the engine does not care about it.”

Examples:

* SGF metadata: `KM`, `HA`, `FF`, `GM`
* comments: `C[...]`
* application tags: `AP[...]`
* unknown or future properties
* private extensions

These are:

* **lossless**
* **round-trippable**
* **engine-agnostic**
* **stored per node**

They live in:

```
NodeAnnotations
  └── List<RawProperty>
```

They do **not** become Actions.

---

### Why *not* `DomainAction.Metadata` for SGF?

That was the trade-off sentence you asked about. Here it is in plain terms:

#### If you use `DomainAction.Metadata`:

Pros:

* Easy to surface komi, handicap, comments in UI
* Looks “uniform” with other actions

Cons:

* Metadata starts flowing through the engine
* Every applier must ignore it
* Easy to accidentally depend on it
* Harder to support multiple formats cleanly

#### If you use **RawProperty only** (Option B):

Pros:

* Engine remains pure
* SGF stays at the boundary
* Format adapters own interpretation
* Easy to support other formats later (JSON, PGN-like, etc.)

Cons:

* UI must *ask annotations* instead of *listening to actions*

For a 25-year-old project being modernized, **Option B is the safer long-term choice**.

And importantly:

> **Option B does NOT prevent you from interpreting metadata later.**
> You can always add a *view-level* interpreter that reads RawProperty and displays komi, comments, etc.

You just don’t *force* it into the engine.

---

## 2. Does this conflict with what your evil twin just did?

Short answer: **No. It complements it.**

What your evil twin added:

> “Introduced neutral core.engine.Action (Play, Pass, Resign, SetBoard, Metadata)”

You can reconcile this cleanly:

### Recommendation

* **Keep `Action.Play`, `Action.Pass`, `Action.Resign`, `Action.SetBoard`**
* **Do NOT generate `Action.Metadata` from SGF**
* Allow `Action.Metadata` to exist for:

  * UI events
  * logging
  * debugging
  * non-persistent flows

In other words:

* SGF → `RawProperty`
* Engine → `Action`
* UI → may interpret either

That keeps both designs intact without churn.

---

## 3. Is this the right time for Tic-Tac-Toe?

Yes. This is actually *perfect timing*.

TTT is valuable **now** because:

* It has **no SGF**
* It has **no setup stones**
* It has **no metadata**
* It has **no exotic topology**

So it forces these questions:

* Can the engine run without SGF?
* Can Actions be truly generic?
* Can GameNode exist without format baggage?
* Does ModelHelper accidentally depend on SGF?

If TTT works cleanly:

* your separation is real
* not accidental

If TTT feels awkward:

* you’ve found remaining coupling early

That’s exactly why it’s a good “honesty test”.

---

## 4. Baby-step guidance (very important)

You said you want **small steps** and to keep things **temporarily in model**.

That’s wise.

### Next *very small* steps I recommend (in order):

1. **Stop producing `Action.Metadata` from SGF**

   * SGF mapper returns:

     * `List<Action>` (engine only)
     * `List<RawProperty>` (annotations)
2. **Ensure Model.do2 / apply loop never sees RawProperty**

   * Only Actions go through it
3. **TTT never sees RawProperty at all**

   * That’s your sanity check
4. **Leave UI interpretation for later**

   * No rush

No renames, no package explosions yet.

---

## Bottom line

* Option B (RawProperty only) is the *correct architectural choice* for where you are now.
* It does not conflict with the new Action path.
* TTT is exactly the right canary at this stage.
* You are no longer “refactoring blindly”; you’re converging on a real engine boundary.

When you’re ready, the *next* conversation can be:

> “What *still* does not belong in Model anymore?”

And that’s where the really satisfying cleanup begins.
