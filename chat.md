Yes — I still recommend the **3-way split of DomainAction**, and given where you are now, it’s the *right* time to lock it in. What you’ve done already (moving `processProperty`, shrinking legacy `Move`, introducing sub-interfaces) is exactly the momentum you want.

Below is a **small, concrete recommendation** you can hand directly to your “evil twin” and implement incrementally without destabilizing tests.

---

## 1. The 3-way DomainAction split (still recommended)

The split is **not arbitrary**; it matches *three fundamentally different responsibilities* that were previously entangled inside `Model.do_()` and SGF parsing.

### 1.1 Core split

```java
interface DomainAction {
    void apply(Model model);
}
```

Now split into **three sub-interfaces**:

```java
interface StateAction extends DomainAction {
    // mutates game state but not the board
}

interface BoardAction extends DomainAction {
    // mutates board contents
}

interface MetaAction extends DomainAction {
    // annotations, comments, metadata, round-trip info
}
```

Why this still holds:

| Action kind     | Examples                                     | Belongs where    |
| --------------- | -------------------------------------------- | ---------------- |
| **StateAction** | move number, player to move, pass, resign    | Engine semantics |
| **BoardAction** | place stone, clear point, mark edge/region   | Board semantics  |
| **MetaAction**  | comments, application tags, unused SGF props | Not engine       |

This gives you **compile-time clarity** and lets you later route actions differently (e.g., skip MetaActions in headless play).

---

## 2. What no longer belongs in `Model` (now)

You asked what should *leave* the model. At this stage, the rule is simple:

> **If it does not directly mutate engine state or board state, it should not live in `Model`.**

### Explicitly migrate out:

1. **SGF property decoding**

   * `P2`, `SgfProperty`, parsing logic
   * Already moving → good

2. **SGF → semantics mapping**

   * `processProperty`
   * Replace with:

     ```java
     List<DomainAction> actions = sgfInterpreter.map(property);
     ```

3. **Round-trip preservation**

   * Extra SGF properties
   * Comments not affecting play
   * Store on node, not in model state

4. **GTP formatting / coordinate conversion**

   * Already partially moved → finish this
   * Model should not know GTP exists

What stays in `Model`:

* `apply(DomainAction)`
* Board creation
* State stack
* Tree navigation (but not SGF semantics)

---

## 3. Recommendation for “unapplied SGF properties” (your quoted item)

Here is a **minimal, safe design** that preserves *all* SGF information without polluting the engine.

### 3.1 Store extras on the node, not the model

In `MNode` (or equivalent):

```java
final class NodeExtras {
    private final List<SgfProperty> unapplied = new ArrayList<>();

    void add(SgfProperty p) {
        unapplied.add(p);
    }

    List<SgfProperty> all() {
        return List.copyOf(unapplied);
    }
}
```

And in `MNode`:

```java
private NodeExtras extras = new NodeExtras();

public NodeExtras extras() {
    return extras;
}
```

### 3.2 Interpreter responsibility

Your SGF interpreter does this:

```java
List<DomainAction> actions = map(property);

if (actions.isEmpty()) {
    node.extras().add(property);
}
```

That’s it.

### Why this works

* **Round-trip safety**: nothing is lost
* **Engine purity**: model never sees SGF
* **Future extensibility**: later you can interpret more properties
* **Testable**: extras are deterministic and inspectable

---

## 4. About having non-SGF game nodes (you are right)

You are absolutely right to want:

> “our own game nodes that do not necessarily require any sgf”

The architectural rule:

* **GameNode** = engine tree
* **SgfNode** = serialization / interchange format

Mapping layer:

```
SgfNode ──► DomainActions ──► GameNode
GameNode ──► DomainActions ──► SgfNode
```

You are *already halfway there* with DomainAction.

This is what allows:

* multiple game formats later
* different board topologies
* AI training without SGF baggage

---

## 5. Tiny next step (as requested)

Since you said “just a tiny step”, here is the smallest useful move:

> **Make `apply` live outside Model, temporarily**

Create:

```java
final class DomainApplier {
    static void apply(Model model, DomainAction action) {
        action.apply(model);
    }
}
```

Then replace calls inside `Model.do2()`.

This:

* breaks dependency cycles
* makes Model thinner
* is fully reversible
* requires no test rewrites

---

## Summary for your evil twin

You can literally send this:

> Keep the 3-way DomainAction split (State / Board / Meta).
> Move SGF semantics entirely into an interpreter that produces DomainActions.
> Store unapplied SGF properties on nodes as extras for round-trip safety.
> The Model should only apply actions — never decode SGF or GTP.
> As a small step, move `apply` out of Model into a helper.

You are not “starting over” — you are **excavating the engine from 25 years of sediment**, and the structure you’re building is sound.

When you’re ready, the next big payoff is:

* **TTT plugin using the same DomainAction pipeline**
* or **a pure engine test with zero SGF involved**

Both will validate the architecture immediately.
