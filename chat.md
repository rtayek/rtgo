This is a **good proposal**, and—more importantly—it is *consistent* with the architectural pressure you’ve been applying for weeks. I’ll answer in three parts:

1. **Verdict on the proposal (short)**
2. **Refinements I would make (small, not disruptive)**
3. **What it should look like as an `ls` tree**
4. **When TTT fits in (now vs later)**

---

## 1. Verdict

Yes — I endorse your evil twin’s proposal **with minor tightening**.

It gets the critical things right:

* **Directionality is explicit**

  * `formats → core → games → ui`
  * never the reverse
* **SGF is fully quarantined**
* **Games are self-contained**
* **Engine concepts are neutral**
* **Legacy is isolated**

This is exactly the structure you want if you intend to:

* support multiple games,
* support multiple formats (SGF, GTP, JSON, etc.),
* keep your inner loop clean,
* and eventually delete legacy code with confidence.

You are *past* the “experiment” stage — this is a stabilization structure.

---

## 2. Small refinements (not philosophical)

### A. Collapse `model/*` unless you *really* want it

Right now you effectively have:

* `core.engine` → neutral execution + tree
* `model.*` → helpers + traversal + historical glue

You have two valid options:

#### Option 1 (simpler, my preference now)

Fold `model/*` into `core/*`:

* `core.engine` → DomainAction, GameNode, NodeAnnotations
* `core.engine.tree` → traversal helpers
* `core.engine.util` → ModelHelper equivalents

This avoids a **three-layer core** (`core → model → games`) which is often overkill.

#### Option 2 (keep model)

If you keep `model`, make it **pure algebra**, no runtime:

* `core` = runtime + orchestration
* `model` = immutable domain concepts

Right now you are *very close* to Option 1 already.

**Recommendation:**
Don’t add new packages under `model`. Let it wither naturally and fold later.

---

### B. Keep `formats/sgf` split exactly as proposed

This is correct and important:

* `formats/sgf` → parser, SGF AST, serializer
* `core/formats/sgf` → *interpretation* (mapping → actions + RawProperty)

That distinction is clean and future-proof.

---

### C. Action vs DomainAction vs Metadata

You already made the right call:

* **Option B: RawProperty only** for non-core SGF data
* No `Action.Metadata` in the core execution path

This keeps appliers:

* exhaustive,
* switch-safe,
* and free of “maybe later” semantics.

UI can interpret `RawProperty` when needed.

Good call.

---

## 3. What this looks like as `ls`

Here is what I would want to see if I typed `ls src` today:

```
src/
├── core/
│   ├── api/
│   │   ├── GamePlugin.java
│   │   ├── GameSession.java
│   │   ├── Renderer.java
│   │   ├── RolePolicy.java
│   │   └── GameState.java
│   │
│   ├── engine/
│   │   ├── Action.java              # neutral (Play, Pass, Resign, SetBoard)
│   │   ├── GameNode.java
│   │   ├── NodeAnnotations.java
│   │   ├── RawProperty.java
│   │   ├── tree/
│   │   │   ├── TreeWalker.java
│   │   │   └── NodeCursor.java
│   │   └── applier/
│   │       ├── ActionApplier.java
│   │       └── DefaultApplier.java
│   │
│   ├── formats/
│   │   └── sgf/
│   │       └── SgfDomainActionMapper.java
│   │
│   └── util/
│       ├── Logging.java
│       └── Preconditions.java
│
├── formats/
│   ├── sgf/
│   │   ├── Parser.java
│   │   ├── SgfNode.java
│   │   ├── MNode.java
│   │   └── SgfProperty.java
│   └── gtp/
│       ├── GtpCodec.java
│       └── GtpCommand.java
│
├── games/
│   ├── go/
│   │   ├── rules/
│   │   │   ├── GoState.java
│   │   │   ├── Board.java
│   │   │   ├── Stone.java
│   │   │   ├── Block.java
│   │   │   └── Move2.java
│   │   ├── adapters/
│   │   │   ├── GoSgfMapper.java
│   │   │   └── GoGtpMapper.java
│   │   ├── render/
│   │   │   └── GoAsciiRenderer.java
│   │   └── GoPlugin.java
│   │
│   └── ttt/
│       ├── rules/
│       │   ├── TttState.java
│       │   ├── TttSpec.java
│       │   └── TttMove.java
│       ├── adapters/
│       │   ├── TttActionMapper.java
│       │   └── TttActionApplier.java
│       ├── render/
│       │   └── TttRenderer.java
│       └── TttPlugin.java
│
├── ui/
│   ├── cli/
│   │   └── Main.java
│   └── swing/
│       └── Shell.java
│
└── legacy/
    ├── move/
    ├── sgf/
    └── adapters/
```

This layout **enforces correctness by gravity**.
People will have to fight the structure to do the wrong thing.

---

## 4. Is it time for TTT?

Yes. In fact:

> **TTT is now serving its intended purpose.**

You’ve already learned from it:

* Neutral Action path works
* Applier split is viable
* Metadata can be excluded cleanly
* Game plugins don’t need SGF

At this point:

* Keep TTT **minimal**
* Don’t add SGF to it
* Use it as a **canary** for architectural regressions

If Go refactors break TTT, that’s a signal—not a problem.

---

## Bottom line

* Your structure is solid.
* The evil twin’s proposal is correct.
* You are past speculative design; this is consolidation.
* Keep doing *small steps*, but now **in the direction of deletion**, not invention.

If you want, next we can:

* identify what still belongs in `legacy/`,
* or define a **hard rule list** (“no imports from X into Y”) to lock this down.
