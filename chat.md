Yes — your evil twin’s suggestions are broadly correct, and in the **fresh tar you just sent**, you’ve already implemented most of them (often in a slightly different shape than his bullets implied).

## 1) Encapsulate `TttSpec` (accessors + factory)

**Recommendation:** Agree.

**Status in your current tar:** Already done.

* `TttSpec` is a `record`, so it’s inherently encapsulated (accessors are the record components).
* You have a factory: `TttSpec.standard3x3()` and `TttSpec.of(int width,int height)`.
* You also validate inputs (`>0`), which is the right boundary.

Nothing urgent left here.

## 2) Move coordinate validation into `TttMove.Place` (don’t split between mapper/applier)

**Recommendation:** Mostly agree, but with one nuance.

**Status in your current tar:** Partially done.

* You added `TttMove.Place.of(TttSpec spec,TttPoint point)` which validates `spec.containsPoint(point)`.
* **However** the `Place` constructor is still `public`, which means callers can bypass validation.
* And `TttActionApplier` still checks `state.isInBounds(...)` (which is fine as a defensive check).

**Practical “baby step” improvement:**

* Make the `Place` constructor **package-private** (no modifier) and funnel construction through `Place.of(...)`.

  * That preserves your preference for default visibility.
  * It prevents “invalid Place” instances from being created outside `games.ttt`.
* Keep the applier’s bounds check anyway. In an engine/applier layer, it’s cheap insurance and improves error messages.

## 3) Rendering helper in `TttPlugin`

**Recommendation:** Agree.

**Status in your current tar:** Already done (and better than the suggestion).

* You have `TttRenderer.render(TttState)` and `TttRenderer.renderWinner(TttState)` and the plugin’s lambdas delegate to it.
* That’s exactly the “keep formatting in one place” boundary you want.

Nothing to change.

## 4) Remove or restrict `Action.Metadata`

**Recommendation:** Agree strongly.

**Status in your current tar:** Already done.

* `core.engine.Action` is now: `SetBoard`, `Play`, `Pass`, `Resign`. No `Metadata`.
* That’s the right call for keeping appliers exhaustively handled and boring.

## One more small, high-leverage tightening (TTT)

Right now `TttActionMapper` emits `new Action.Play(point.x(), point.y(), null)`.

That’s fine, but it implies “payload” exists and is unused. If you want to keep `Action.Play` truly minimal and avoid “mystery nulls”:

* Option A: keep it as-is (cheapest).
* Option B: drop the payload field from `Action.Play` entirely (if nothing else needs it). This is a small API break but very clarifying.

## Does this conflict with “Option B: RawProperty only”?

Not really.

* “Option B” is about **SGF extras/annotations**: keep non-core SGF stuff as `RawProperty` in `NodeAnnotations`, and don’t represent it as executable actions.
* Your **TTT action path** is independent of SGF; it uses `core.engine.Action` and game-specific appliers/mappers. That separation is compatible and, in fact, reinforces the same architectural idea: *formats at the boundary; engine gets only minimal domain events.*

If you want the cleanest story going forward:

* `core.engine.Action` = what the engine/appliers consume (game-agnostic verbs).
* `core.engine.NodeAnnotations/RawProperty` = lossless “stuff we’re not interpreting.”
* Format adapters (SGF, GTP) *only* produce Actions + Annotations; they do not leak their types inward.

If you want, the next “baby step” I’d recommend is: **make `TttMove.Place` constructor package-private and update any direct `new Place(...)` call sites to `Place.of(spec, point)`**. That’s a minimal change with immediate payoff.
