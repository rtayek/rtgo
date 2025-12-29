Yes, I still recommend the **3-way split**, and what you have in the current tar is already heading in exactly the right direction.

## 1) Do I still recommend a 3-way split of DomainAction?

Yes—because it lets you keep the **engine inner loop** tight while still round-tripping “everything SGF said” without infecting the model with SGF concerns.

A practical split that matches your current code is:

1. **Engine actions (state mutation)**
   Things that change the *game position / rules state*.

   * Play / Pass / Resign
   * Setup stones (AB/AW)
   * Board edges / cutouts (RG) *if you model them as board state*
   * Capture resolution etc. (already internal to your go logic)

2. **Configuration actions (board spec / topology / shape)**
   Things that change “what kind of board are we on,” not a move.

   * SZ, topology, shape, regions-as-geometry, etc.

3. **Metadata actions (no-op to engine)**
   Everything else that should survive round-trip but should not touch gameplay.

   * FF, GM, KM, HA, BL/WL, AP, C comments (unless you deliberately interpret some “private comment protocol”), unknown properties, etc.

In your current `DomainAction`, you effectively already have:

* Engine-ish (`Move`, `Pass`, `Resign`, `SetupAddStone`, `SetupSetEdge`)
* Config-ish (`SetBoardSpec`, `SetTopology`, `SetShape`)
* Metadata-ish (`Metadata`, `RecordResult` (currently metadata-only))

That is good. If you want to make the split *explicit* without changing behavior, the cleanest low-risk step is:

* Keep `DomainAction` sealed.
* Introduce **marker subinterfaces**:

  * `sealed interface EngineAction extends DomainAction permits ...`
  * `sealed interface ConfigAction extends DomainAction permits ...`
  * `sealed interface MetadataAction extends DomainAction permits ...`
* Then your apply path can be:

  * engine applies only `EngineAction`
  * configuration applies only `ConfigAction`
  * metadata is stored only (never applied)

This will make it much harder for SGF “random stuff” to leak back into the inner loop later.

## 2) Recommendation to Codex: “unapplied SGF properties stored alongside nodes”

You already implemented the essence of it:

* `sgf.MNode` has `extraProperties` plus `addExtraProperty()` and you include extras in round-trip output (your `toBinaryTree()` appears to append `extraProperties` after `sgfProperties`).
* `DomainAction.mapNodeToDomainActions()` moves unknown/unhandled SGF properties out of `sgfProperties()` and into `extraProperties()`.
* `ModelHelper.toGameNode()` builds `NodeAnnotations` from `node.extraProperties()`.

So the concrete recommendation to your evil twin is:

1. **Make “extras/unapplied” strictly lossless and strictly inert**

   * Never interpret `extraProperties` in the engine.
   * Never drop them.
   * Round-trip serialization must always emit: `sgfProperties + extraProperties` (which you’ve basically done).

2. **Decide one canonical storage location**

   * Either:

     * Keep extras on `MNode` only (source-of-truth for SGF round-trip), and `NodeAnnotations` just mirrors it when you convert to `GameNode`.
   * Or:

     * Move the source-of-truth to `GameNode.annotations.unapplied` once you’ve fully decoupled from SGF trees.
   * For now, your current “extras live on `MNode`” approach is ideal: minimal disruption, preserves tests.

3. **Widen “extras” beyond unknown properties**

   * Today you move properties with `p2 == null` to extras.
   * Next low-risk improvement: also move “known but not mapped” properties to extras (instead of `Metadata`), *if* you want metadata to be SGF-only.
   * Or keep your current `Metadata(P2, values)` as “structured extras.” Both are defensible—just don’t apply it in the engine.

4. **Add one guardrail test (you basically did)**

   * A test that loads SGF with a property you don’t support (or don’t map), runs through model conversion/round-trip, and asserts the property survives byte-for-byte (or structurally).
   * If you want it stronger: assert ordering (SGF property order sometimes matters for picky tools).

That’s the tiny step that keeps you honest while you rip SGF out of the model.

## 3) What does not belong in the Model anymore?

Given where you are (tests passing, inner loop matters), I would draw a hard boundary:

**Model should own:**

* Current position / board reference
* Variation navigation (parent/children pointers) *in whatever node structure you decide*
* Applying *engine actions* to mutate state
* Minimal eventing needed by UI (but ideally via an adapter)

**Model should not own (or should be on a “containment leash”):**

* SGF parsing/serialization (`Parser`, `SgfProperty`, etc.)
* SGF interpretation rules (mapping SGF properties to actions)
* GTP protocol formatting/parsing
* Server/network concerns
* UI concerns (Swing widgets, view state)
* Disk I/O

You’ve already moved `processProperty` out; that is exactly the right kind of extraction. The next “temporary but valuable” move is:

* Keep `DomainAction.applyTo(model)` for now if you must (you said you temporarily moved apply there), **but treat it as a staging area**.
* Ultimately you want `model.apply(EngineAction)` and a separate `Interpreter` that produces `List<DomainAction>` from SGF nodes.

If you want, I can point to the next **low-risk extraction** in your current tar (based on what you already have): the mapping path `DomainAction.mapPropertyToDomainActions()` is now the “SGF interpreter nucleus.” The model should call it, but should not *contain* it.

If you paste (or point me to) the new test you added (filename), I can tell you whether it’s covering the right failure mode (lossless extras, ordering, or both).
