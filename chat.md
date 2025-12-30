Two separate topics here: (1) what “Option B: RawProperty only” really means in your current refactor, and (2) whether it conflicts with the new game-agnostic `core.engine.Action` path + TTT.

## 1) What “RawProperty only (annotations)” means in practice

If you choose Option B, the rule is:

* **Only “core-to-the-engine” semantics become actions.**

  * For Go/SGF that’s basically: board size/spec, topology/shape (if you want those treated as engine config), setup stones/edges, play/pass/resign.
* **Everything else is preserved losslessly as data**, stored on the node as `RawProperty` in `NodeAnnotations`.

So instead of doing:

* `DomainAction.Metadata(P2.KM, ["6.5"])`
* `DomainAction.Metadata(P2.HA, ["2"])`
* etc.

…you do:

* `annotations.unapplied = [ RawProperty("KM", ["6.5"]), RawProperty("HA", ["2"]), ... ]`

**Why it matters:**

* The engine stays SGF-ignorant and also *metadata-ignorant*.
* Round-trip is still lossless because you never drop properties; you just stop interpreting them in the core loop.
* When you later want UI display (“komi 6.5”, “handicap 2”), you read from annotations at the boundary/UI, not from the engine action stream.

**Implication:** your SGF mapper becomes stricter about what it interprets. Everything it doesn’t explicitly map to an engine/config action is routed into `RawProperty`.

A clean dividing line is:

* **Engine/config actions**: `SZ`, `B/W`, `AB/AW`, your private resign tags `ZB/ZW`, `RG` (if you keep it as “edge mask”), plus whatever you currently encode in comments for topology/shape.
* **RawProperty annotations**: `FF GM KM HA BL WL AP RE C` (except the topology/shape comment convention if you keep that).

You can still keep a “notes” list for human commentary (or keep comment as a `RawProperty("C", [...])` and let UI interpret it).

## 2) Does this conflict with the new `core.engine.Action` + TTT wiring?

Not inherently. It’s actually compatible—and if anything, Option B makes it *easier* to keep the engine neutral.

But you should decide which abstraction is “the one true action stream”:

### Recommended consolidation

* Treat `core.engine.Action` as the canonical, game-agnostic action stream.
* Each game supplies:

  * a mapper from its domain representation to `Action` (you already have `TttActionMapper`)
  * an applier from `Action` to its game state (`TttActionApplier`)
* SGF becomes just another boundary format that maps to `(List<Action>, NodeAnnotations)`.

In that world:

* `NodeAnnotations` (with `RawProperty`) is orthogonal and can live alongside any action list.
* You do **not** need `DomainAction.Metadata` at all.
* You might not need `model.engine.DomainAction` at all long-term; it can be an intermediate step during migration.

### Small-step approach (least churn right now)

If you want minimal disruption to what your twin just did:

1. Keep `model.engine.DomainAction` for Go/model for the moment.
2. Remove/stop emitting `DomainAction.Metadata` in `SgfDomainActionMapper`.
3. Anything not mapped becomes `RawProperty` in `NodeAnnotations`.
4. If/when you want to unify with `core.engine.Action`, do it later via a tiny adapter:

   * `DomainAction -> Action` mapping (straightforward for the core cases).

That keeps your current tests stable while moving you toward the neutral pipeline.

## What you should tell your “evil twin” to do for Option B

Concrete guidance:

* **In `SgfDomainActionMapper`:**

  * For every SGF property:

    * If it’s one of the “core” properties, emit real actions (move, pass, setup, board spec, etc.).
    * Otherwise, do *not* emit `Metadata` actions. Preserve it as an extra/annotation `RawProperty`.
* **At the node boundary:**

  * Convert SGF `SgfProperty` to `RawProperty(id, values)` once, store it in `NodeAnnotations`.
  * Keep these properties available for re-serialization, UI display, debugging, etc.
* **In the applier:**

  * The applier applies only core/config actions.
  * It does not touch `RawProperty` at all.

This aligns perfectly with your stated goal: “keep SGF out of the model” while not losing information.

## About the tarballs “disappearing”

I can’t currently open your uploaded tar in my tooling because the file has expired on my side (the environment reports that some previously uploaded files are no longer available). If you want me to verify the exact package layout and describe “all the properties floating around” from your current state, you’ll need to re-upload the latest `code7.tar.gz` again in this chat.

If you re-upload it, I’ll:

* list the package layout as it exists now (core vs games vs formats),
* enumerate which SGF properties are being treated as actions vs preserved as `RawProperty`,
* and point out any remaining “metadata leakage” into engine code.
