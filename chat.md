Yes — I can see what you’ve done now, and you are broadly on the right track. Here’s a precise read on the four classes you named, plus the smallest “next step” recommendations.

## 1) `model.DomainAction`

### What you have

* A **sealed** `DomainAction` with three marker subinterfaces:

  * `EngineAction` (mutates board / gameplay state)
  * `ConfigAction` (board spec / topology / shape)
  * `MetadataAction` (things you don’t want the engine to “do”)
* Concrete actions are **records**, which is exactly the right direction for “simple, immutable domain events”.

### What’s good

* The 3-way split is still a good idea, and your implementation is clean.
* `applyTo()` centralizes semantics and keeps call sites simple.
* Your “extras/unapplied” story is emerging: unknown SGF properties are moved to `node.extraProperties()`.

### What I would change next (tiny, low-risk)

Right now `DomainAction` is doing *three jobs*:

1. define domain actions (good, keep)
2. **map SGF → DomainAction** (leak)
3. **apply DomainAction → Model** (maybe OK for now)

If you want SGF further away from the model, the smallest step is:

* Keep `DomainAction` as the domain algebra + `applyTo()`.
* Move these out of `DomainAction` into a dedicated mapper class (package-private is fine):

  * `mapPropertyToDomainActions`
  * `mapNodeToDomainActions`
  * `parseSgfPoint`
  * the `sgf.*` imports

Concretely: create something like `model.SgfToDomainAction` (or `model.SgfDomainActionMapper`) and move the SGF mapping there. That immediately removes `sgf.*` dependencies from `DomainAction` without changing behavior.

### One correctness nit to fix soon

`applyTo(Move)` currently does:

```java
case DomainAction.Move a -> {
    model.ensureBoard();
    if(a.point()==null) model.sgfPassAction();
    else model.sgfMakeMove(a.color(), a.point());
}
```

But your mapper never creates `Move(color, null)`; it creates `Pass(color)`. So the `null` branch is dead or defensive. Pick one and standardize:

* either remove the null branch (preferable), or
* stop having a `Pass` action and represent pass as `Move(color, null)`.

You already have `Pass`; I’d remove the null branch.

### One design smell you may want to avoid

`mapNodeToDomainActions()` mutates the SGF node:

```java
it.remove();
node.addExtraProperty(property);
```

This is fine if you *intend* to “normalize” nodes in-place. If not, it can be surprising during round-trip work. The low-risk alternative is: don’t remove from `sgfProperties()`, just *also* record it as extra. (Or only do the removal in the round-trip path, not the engine path.)

## 2) `model.NodeAnnotations`

### What you have

* `NodeAnnotations` holds:

  * `List<SgfProperty> unapplied`
  * `List<String> notes`

### Recommendation

If the goal is “SGF out of the model”, this type is currently the *main remaining coupling* because it embeds `sgf.SgfProperty`.

Minimal next step (no behavior change):

* Replace `List<SgfProperty>` with a model-owned “raw property” DTO:

  * `record RawProperty(String id, List<String> values) {}`

Then the SGF layer can convert `SgfProperty → RawProperty` when attaching annotations, and your model no longer imports `sgf.*`.

If you don’t want a new type yet: at least make `NodeAnnotations` a `record` so it’s clearly passive data.

## 3) `model.GameNode`

### What you have

* A simple tree node:

  * `actions : List<DomainAction>`
  * `annotations : NodeAnnotations`
  * `children : List<GameNode>`
  * `parent : GameNode` (mutable)

### Recommendation (tiny)

If you want low-level cleanup without semantic changes:

* Make `parent` a constructor parameter and `final` (or omit it entirely and compute parent via traversal when needed).
* If you keep it mutable, you’ve created a “partially-constructed object” pattern (construct node, then later set parent). It works, but it’s exactly the kind of thing that causes weirdness later.

You already build it in one place (`ModelHelper.toGameNode`), so it’s easy to pass parent in.

## 4) `model.ModelHelper`

### What you have

* `toGameNode()` builds a `GameNode` tree from `sgf.MNode`, using `DomainAction.mapNodeToDomainActions()`, and attaches `extraProperties()` as `NodeAnnotations`.
* `processProperty()` still contains the big legacy SGF switch (it’s the old “interpreter”).
* Round-trip helpers.

### Recommendation

You now effectively have **two SGF interpreters**:

* the “new” one: `DomainAction.map*` + `DomainAction.applyTo`
* the “old” one: `ModelHelper.processProperty` switch

That’s okay temporarily, but you’ll want to pick one as the authority.

Smallest step that helps immediately:

* Make `ModelHelper.processProperty()` *delegate* to the new mapper for the cases you’ve already mapped (AB/AW/B/W/RG/SZ/C/ZB/ZW/RE), and leave everything else as metadata/extras for now.

That keeps behavior stable but ensures you aren’t maintaining two competing truth tables forever.

## Do I still recommend the 3-way split?

Yes — and you’ve already implemented it correctly. The next improvement is simply: **enforce the split by moving SGF mapping out of `DomainAction`**, and replacing SGF types in `NodeAnnotations` with model-owned raw types.

## What doesn’t belong in `Model` anymore (in your current direction)

* Anything in `model.*` that imports `sgf.*` is the obvious boundary violation.
* The “SGF property decoding table” should live in an SGF-facing module/class (even if it’s just `model.sgf` as a transitional package).
* Your engine should ideally only see:

  * `DomainAction` (or `Move2` etc),
  * `GameNode` (your own node type),
  * and board/state types.

If you want, I can propose a very small package layout that accomplishes the above **without moving any logic yet** (just relocating classes and adjusting imports), so the codebase starts enforcing the architecture while you continue making “tiny steps.”
