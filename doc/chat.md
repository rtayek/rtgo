Good — the tarball helps. I’m going to give you a **concrete “do this next” plan**, not more philosophy, based on what I see in the project layout.

You are *exactly* at the point where the right move is **mechanical consolidation + guardrails**, not new abstractions.

---

## What the tarball tells me immediately

Top-level contents include (abridged):

```
.gradle/
build.gradle
gradlew*
legacy/
src/
tst/
scripts/
doc/
suites/
slow/
tmp/
c/
ours/
nosize/
config/
```

This confirms several things:

* You already **intuitively separated concerns**, but not yet **enforced** them.
* You already have a `legacy/` directory — excellent — but it is not yet a *hard quarantine*.
* The clutter problem is real, but **root cleanup is not the next step**. Doing it now would increase risk.

The correct next move is **dependency enforcement + execution choke-point isolation**.

---

## What I recommend you do next (in order)

### Step 1 — Establish a *compile-time firewall* (this is the single highest-leverage move)

Before touching behavior or moving files, make it *impossible* to regress.

#### 1.1 Define forbidden import rules (write them down)

Write this in `doc/architecture-guardrails.md` (or similar), verbatim and short:

* `core..*` must not import:

  * `..sgf..*`
  * `..gtp..*`
  * `..ui..*`
  * `legacy..*`
* `games..*` must not import:

  * `..sgf..*`
  * `..gtp..*`
  * `legacy..*`
* `adapters.sgf..*` may import `core..*` and `games..*`
* `legacy..*` may import anything

This is now a **project rule**, not a suggestion.

#### 1.2 Enforce it cheaply (do not over-engineer)

Add a script under `scripts/` such as:

```
scripts/check-imports.sh
```

that does simple greps like:

```sh
grep -R "import .*sgf" src/core && exit 1
grep -R "import .*legacy" src/core && exit 1
```

Crude is fine. You can refine later.
Run it manually or from a test suite.

**Why this is step #1:**
Once this exists, every subsequent refactor becomes safer and faster.

---

### Step 2 — Identify and lock the *execution choke point*

You already alluded to this repeatedly (“legacy Move in inner loops”).

Now do it explicitly.

#### 2.1 Find the single place where “a node is executed”

Search for where:

* SGF nodes are “applied”
* `Move.do_()` or equivalent is called
* Game state mutates in a loop

There should be **one or two hot paths**.

Name that place explicitly in your head as:

> **The execution choke point**

From now on:

* All execution must flow through **DomainAction application**
* Legacy execution is allowed **only behind a shim**

Do **not** eliminate legacy yet — just **isolate it**.

#### 2.2 Introduce a shim if needed (temporary, ugly is fine)

Example intent (not exact code):

```java
// legacy shim — must live in legacy.*
LegacyExecutor.execute(DomainAction action)
```

And *only there* is legacy behavior allowed.

This lets you:

* Remove legacy calls from loops
* Keep behavior identical
* Replace implementation incrementally

---

### Step 3 — Centralize RT handling (small, surgical, stabilizing)

Do this **before** broader refactors.

Checklist:

* RT is recognized **only** in the SGF adapter
* RT never becomes a DomainAction
* RT produces:

  * either a root marker annotation, or
  * a synthetic root node flag
* Engine never checks for RT

Add **one test**:

> SGF with RT root vs SGF without RT root
> → same DomainAction stream
> → same final state
> → same serialized SGF

After this, RT becomes boring — which is exactly what you want.

---

### Step 4 — Lock SGF round-trip *before* further cleanup

You already know this, but now make it operational.

Create a directory like:

```
tst/sgf/roundtrip-corpus/
```

Populate it with:

* files that previously broke
* files with unknown props
* comment-heavy files
* setup stones
* passes, empty nodes, variations

Add **one parameterized test**:

* load → execute → save
* assert byte-for-byte equality

Once this passes reliably:

* you can refactor aggressively
* you can delete legacy with confidence

---

### Step 5 — Only then: start deleting or moving things

After Steps 1–4:

* Move truly dead folders into `doc/archive/` or delete
* Push questionable code into `legacy/`
* Move production code toward:

  * `core/`
  * `games/go`
  * `games/ttt`
  * `adapters/sgf`
  * `adapters/gtp`

Do this **one package at a time**, running:

1. compile
2. tests
3. import firewall
   after each move.

---

## What I would *not* do next

* I would **not** redesign DomainAction again.
* I would **not** clean the root yet.
* I would **not** touch networking.
* I would **not** chase timeout tests until execution is isolated.

---

## The one-sentence recommendation

> Your next move is to **make illegal dependencies impossible**, then **force all execution through one DomainAction choke point**, and only after that continue refactoring.

If you want, the *very next* thing I can do is:

* point out the likely execution choke point(s) from the code, or
* draft the exact import-firewall script tailored to your package names, or
* sketch the minimal shim to quarantine legacy Move execution.

Tell me which one you want to do next.
