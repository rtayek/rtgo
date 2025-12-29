Here is a concrete recommendation you can hand to your evil twin. It is designed to preserve SGF round-trip fidelity while keeping SGF out of the engine, and it is deliberately “local/low-risk” (no semantic changes to gameplay).

---

## Recommendation: preserve SGF via Node Annotations (extras) with lossless storage

### Goal

When importing SGF, we translate *known* properties into `DomainAction`s and apply them. Everything else must survive untouched so that exporting the game (or editing and re-saving) does not drop information.

### Key idea

Each internal game tree node gets:

1. `List<DomainAction> actions` (engine-facing)
2. `NodeAnnotations annotations` (format-facing, lossless SGF preservation)

`Model` only sees (1). SGF code owns (2).

---

## Proposed data model

### 1) Internal node type (engine-friendly)

Create a new internal node type (even if you keep SGF nodes around temporarily):

```java
final class GameNode {
    final List<DomainAction> actions;
    final NodeAnnotations annotations; // may be empty
    final List<GameNode> children;
    GameNode parent;
}
```

You can initially keep `GameNode` as a thin wrapper around your existing `MNode`/SGF node tree if that reduces churn.

### 2) NodeAnnotations (lossless SGF extras)

Store **unapplied** SGF as data, not as behavior:

```java
final class NodeAnnotations {
    // Raw, lossless SGF properties that were not mapped into actions.
    final List<SgfProperty> unapplied;

    // Optional: diagnostics or mapping decisions, not needed for round-trip
    final List<String> notes;
}
```

Important: `NodeAnnotations` is allowed to reference `sgf.*` types because it lives outside core. The engine never touches it.

---

## What counts as “unapplied” vs “applied”

During SGF import, for each property:

* If you map it to a DomainAction and “consume” it → **do not store**
* If you do not map it (unknown / unimplemented / intentionally ignored) → **store in `unapplied`**
* If a property is partially mapped (example: `C[...]` where you parse topology tokens but want to preserve the comment) → store either:

  * the original property as-is (simplest), OR
  * store a “residual” property if you rewrite it (avoid if possible)

I recommend simplest: **keep original property unchanged** unless you have a strong reason to rewrite.

---

## Export strategy (round-trip)

When exporting SGF from the internal node tree:

1. Emit SGF properties derived from `actions`
2. Then emit the `unapplied` properties
3. Keep ordering stable:

   * Either “actions first then extras”
   * Or preserve original order by storing an index (optional; not required for most SGF consumers)

### Ordering (optional refinement)

If you want near-perfect fidelity, store the original property order as “tokens”:

```java
sealed interface PropertyToken permits AppliedToken, UnappliedToken { }

record AppliedToken(int actionIndex) implements PropertyToken {}
record UnappliedToken(int propertyIndex) implements PropertyToken {}

final class NodeAnnotations {
    final List<SgfProperty> unapplied;
    final List<PropertyToken> order; // reconstruct original property order
}
```

This lets you export with almost identical ordering (useful for diff stability).

---

## Why not store “raw SGF text”?

You *can* store the exact original SGF substring, but it’s usually overkill and increases parsing complexity.

Storing parsed `SgfProperty` objects is the right balance:

* Still lossless (property id + list values)
* Easy to re-emit
* Does not pollute engine

If your parser normalizes whitespace/escaping and you care about byte-for-byte identical SGF output, then store raw text. Most projects do not need that.

---

## Minimal implementation plan (low-risk)

This is meant to be a small series of commits:

1. **Introduce `NodeAnnotations`** (no behavior change)
2. In SGF import, when mapping properties:

   * return `(actions, extras)`
3. Attach extras to the corresponding node
4. In SGF export, append extras after emitted properties

No Model changes required beyond consuming `actions`.

---

## Handling your current “private properties” (ZB/ZW/etc.)

Treat them like normal properties:

* If you map them to actions (`Resign(color)`) and want them to round-trip anyway, you have two options:

  * Do **not** store them (the action will regenerate them on export)
  * Store them as extras (ensures *exact* property preserved)

I recommend: **regenerate from actions**, unless a consumer depends on those exact properties being present even when redundant.

---

## Implementation constraints you should enforce

* `model.*` must never import `sgf.*`
* `DomainAction` must never reference SGF types
* `NodeAnnotations` (or SGF module) may reference both

This is the boundary that prevents SGF creep.

---

If you paste this to your evil twin, they can implement it without redesigning your entire tree immediately. The important part is: **extras are attached to the node**, not to the model, and are **treated as inert data** for export fidelity.
