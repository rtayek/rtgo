I can show you a **package layout** immediately. For the “describe the properties” part, I need to re-open your current source to be exact (your uploaded tar has expired again on my side), so below I’ll give you (a) the SGF property taxonomy you’re already dealing with, and (b) a structure that keeps “properties” from infecting the engine—while staying compatible with what you’ve built.

---

## Package layout (incremental, low-risk, keeps tests green)

Goal: **Engine and model do not import `sgf.*` or `gtp.*`**, while SGF/GTP adapters can still produce/consume your `DomainAction` and `GameNode`.

### A. Core engine (no SGF/GTP imports)

```
core/
  api/
    GamePlugin.java
    GameSession.java
    EngineAdapter.java

  engine/
    DomainAction.java           // sealed + action records, NO sgf imports
    DomainApplier.java          // optional choke point; tiny step
    EngineAction.java           // marker
    ConfigAction.java           // marker
    MetadataAction.java         // marker

  model/
    GameNode.java               // your engine tree node, no sgf imports
    NodeAnnotations.java        // replace sgf types with RawProperty (below)
    RawProperty.java            // record RawProperty(String id, List<String> values)
    Model.java                  // board/state + apply(engine actions)

  board/                        // optional name; could be equipment/
    Board.java
    Stone.java
    Point.java
    Topology.java
    Shape.java
```

### B. Format adapters (where SGF/GTP is allowed)

```
adapters/
  sgf/
    SgfProperty.java
    SgfNode.java / MNode.java
    SgfParser.java
    SgfWriter.java
    SgfToDomainActionMapper.java     // SGF → List<DomainAction> + extras
    DomainActionToSgfWriter.java     // optional later

  gtp/
    GtpClient.java / FrontEnd.java / BackEnd.java
    GtpMoveCodec.java               // Move2 ↔ string, etc.
    GtpToDomainActionMapper.java    // optional
```

### C. Go game plugin (domain logic, but engine-facing)

```
games/
  go/
    GoPlugin.java
    GoRules.java / GoEngine.java      // uses core.model + equipment
    GoMove2.java                      // if you have a new move type
    GoRenderer.java
    GoRolePolicy.java
```

### D. UI and CLI

```
ui/
  swing/
    Main.java
    GamePanel.java
    Mediator.java
    TreeView.java
cli/
  CliMain.java
server/
  GoServer.java
```

### Minimal “tiny step” move to get there

You don’t have to move everything at once. The smallest change that enforces architecture:

1. Move **SGF mapping** out of `DomainAction` into:

* `adapters.sgf.SgfToDomainActionMapper`

2. Change `NodeAnnotations` to stop referencing SGF classes:

* `List<RawProperty>` rather than `List<SgfProperty>`

That single change eliminates almost all `sgf.*` leakage into the engine.

---

## “We have a lot of properties floating around” — what they are

You’re dealing with *three* different “property” concepts that easily get conflated:

### 1) SGF properties (file format properties)

These are key/value(s) pairs in an SGF node: `ID[values...]`

**Where they belong:** `adapters.sgf.*`

**Examples you already handle:**

* **Moves:** `B[dd]`, `W[pq]`, pass as `B[]` / `W[]`
* **Setup stones:** `AB[aa][bb]`, `AW[...]`
* **Board size:** `SZ[19]` or `SZ[19:17]`
* **Game metadata:** `GM[1]` (Go), `FF[4]` (format), `AP[...]` (app)
* **Rules-ish metadata:** `KM[6.5]`, `HA[2]`
* **Timing:** `BL[...]`, `WL[...]`
* **Comments / annotations:** `C[...]`
* **Result:** `RE[...]`
* **Regions / markup:** you have `RG[...]` and some private ones
* **Private properties:** you mentioned `ZB`, `ZW`, maybe `RT`—your app-specific keys

These are not “engine domain actions.” They are **serialization inputs**.

### 2) Engine domain actions (your internal semantic commands)

These should be a **small closed set** your engine understands.

**Where they belong:** `core.engine.*`

Typical split (as you’ve already been doing):

* **EngineAction**: `PlayMove`, `Pass`, `Resign`, `AddStone`, `SetEdge`, etc.
* **ConfigAction**: `SetBoardSize`, `SetTopology`, `SetShape`
* **MetadataAction**: `Comment`, `ApplicationTag`, `Result`, `UnknownProperty`

These are what `Model.apply(...)` consumes.

### 3) Extras / annotations (unapplied SGF properties you want to preserve)

This is your “don’t lose SGF data” bucket.

**Where they belong:** `core.model.NodeAnnotations` but **as raw data**, not SGF classes.

That’s why I recommend:

```java
record RawProperty(String id, List<String> values) {}
```

So you can store *exactly what SGF contained* without coupling the model to SGF parsing types.

---

## Practical taxonomy: SGF properties → which bucket?

This is the mental model you and your twin need to stay sane:

### A) Properties that map to EngineAction (affect board/state)

* `B`, `W` (move or pass)
* `AB`, `AW` (setup stones)
* `RG` (if treated as board mask/edge)
* `ZB`, `ZW` (your resign markers)

### B) Properties that map to ConfigAction (configure the engine)

* `SZ` (board dimensions)
* your embedded topology/shape markers (currently in `C[...]` with prefixes)

### C) Properties that should become MetadataAction or Extras

* `FF`, `GM`, `AP`, `KM`, `HA`, `BL`, `WL`, `RE`, normal `C[...]`
* Any property you don’t interpret yet

If you want the engine completely “SGF-agnostic,” then even these should *not* become `MetadataAction`—they should become **Extras** (RawProperty list). But that’s a later call; your current middle ground is fine.

---

## Tiny next step I recommend you do next

1. Create `core.model.RawProperty`
2. Change `NodeAnnotations` to `List<RawProperty>`
3. In SGF adapter code, convert `SgfProperty → RawProperty` when attaching annotations
4. Move SGF mapping out of `DomainAction` into `adapters.sgf.SgfToDomainActionMapper`

This is small, local, and will reduce “properties floating around” immediately.

---

If you re-upload the current tar **one more time**, I’ll enumerate the *actual* property IDs you have in your `P2` enum / parser tables and classify each one into (EngineAction / ConfigAction / Metadata / Extras) so you have a definitive mapping list for the project.
