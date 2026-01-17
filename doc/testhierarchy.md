1/7/26 this  was true a few days ago. we have been refactoring.

Here is what you have, as a *test-class hierarchy*, and what I think about it.

## What the hierarchy looks like

At a high level, you’re using a classic JUnit4 “template base class” pattern:

```
AbstractSgfParserTestCase
  |
  +-- SgfParserTestCase            (@RunWith(Parameterized.class))
  |
  +-- AbstractSgfRoundTripTestCase
  |     |
  |     +-- SgfRoundTripTestCase   (@RunWith(Parameterized.class))
  |
  +-- AbstractModelRoundtripTestCase
        |
        +-- SgfModelRoundTripTestCase (@RunWith(Parameterized.class))
```

And then a *separate but related* base:

```
AbstractMNodeRoundTripTestCase
  |
  +-- SgfMNodeRoundTripTestCase    (@RunWith(Parameterized.class))
  (does not extend AbstractSgfParserTestCase)
```

Fixture-focused helpers:

```
SgfFixtureTestCase          (@RunWith(Parameterized.class))
SgfStructureTestCase
```

General unit tests:

```
SgfUnitTestCase
```

Ignored/slow files:

```
SgfSlowFilesTestCase
```

### What `AbstractSgfParserTestCase` is doing

It is both:

1. **Fixture loader** (takes `key()`, loads SGF string via `Parser.getSgfData(key)`, stores `expectedSgf`), and
2. **Test suite** (it defines multiple `@Test` methods: key sanity, parse, flag parsing).

That’s why subclasses often only implement `key()` and get “a whole suite” for free.

### What `AbstractSgfRoundTripTestCase` adds

It extends the above and adds the stronger invariants:

* restore/save round-trip equality
* deep tree equality after save/restore
* canonicalization checks
* round-trip twice stability

So: parse tests are the “smoke tests”; round-trip tests are the “gold tests.”

That is a solid split in *intent*.

## What I like about it

1. **Your base class is legitimately valuable**: it centralizes the SGF fixture acquisition (`key()` → `expectedSgf`) and keeps the tests consistent across many fixtures.

2. **Round-trip tests are appropriately strict**: you’re explicitly defending the “SGF must replicate exactly” constraint. That is the correct posture for your project given the engine/adapter split work.

3. **Parameterized subclasses are correctly using inheritance** (in JUnit4 terms): it is one of the few sane ways to reuse a common test suite across a fixture corpus.

## The main structural problem

The issue is not “inheritance” per se. The issue is **stateful base classes with mutable test inputs**.

Concretely:

* `AbstractSgfParserTestCase` keeps mutable instance state:

  * `key`, `expectedSgf`, `games`
  * plus option toggles (`alwaysPrepare`, and `prepare()` mutating `expectedSgf`)
* subclasses sometimes change preparation behavior by overriding or re-invoking `prepare()`.

That means your “fixture” is not purely a function of `key()`; it becomes a function of:

* `key()`
* which subclass you’re in
* whether `prepare()` ran (and when)
* global option state (`SgfNode.options.*`)

This is exactly the sort of thing that produces “it passes in isolation but fails when the suite is run” or “null key sometimes” behavior.

### The specific confusing bit

`AbstractSgfParserTestCase.setUp()` does:

* `if (alwaysPrepare) prepare();`

`AbstractSgfRoundTripTestCase.setUp()` does:

* `super.setUp();`
* `if (!alwaysPrepare) prepare();`

So `alwaysPrepare` now means “which class triggers `prepare()`,” not “should we prepare.” It’s an inversion that makes the mental model harder than it needs to be.

## What I would change (without changing test intent)

If you want to keep the current pattern (which is fine), here are the smallest refactors that improve reliability without re-architecting:

### 1) Make preparation deterministic and single-source

Replace the “double negative” (`alwaysPrepare` + subclass conditional) with a single hook:

* Base class always loads raw fixture
* Base class calls exactly one overridable method to normalize

Conceptually:

* `String rawSgf = load(key)`
* `String expectedSgf = normalize(rawSgf)`  ← default is identity; round-trip subclasses override

This removes the “did prepare run / twice / in which class” uncertainty.

### 2) Don’t mutate `expectedSgf` in place

Today, `prepare()` mutates `expectedSgf`. That makes debugging much harder because the original input disappears.

A tiny improvement: keep both:

* `rawSgf`
* `expectedSgf`

Even if you do nothing else, this makes failures dramatically easier to understand.

### 3) Avoid `Object key()`

If you can, make `key()` return `String` (or a small value object). `Object` is permissive and invites edge cases. Your “null key sometimes” symptom is consistent with either:

* a parameterized runner feeding something unexpected
* a subclass returning null
* an initialization ordering issue that becomes invisible because `Object` gives you no constraints

### 4) Make the base class do less test assertion work

Right now the base is doing a *bundle* of tests. That’s convenient, but it also means:

* every subclass gets every test, even if it only cares about one aspect
* it’s harder to control what is being validated for special fixtures

A minimal improvement (still inheritance-based): keep the fixture loading in the base, but split test suites into separate base classes if needed:

* `AbstractSgfLoadTestCase` (just “fixture loads, parses”)
* `AbstractSgfRoundTripTestCase` (strict invariants)

You already *mostly* have this. The recommendation is: ensure `AbstractSgfParserTestCase` doesn’t grow more “policy.”

## Bottom line recommendation

Your hierarchy is fundamentally sound for JUnit4 and for your project constraints (strict SGF fidelity, many fixtures, adapter separation). I would **keep it**, but I would do these two “high ROI” cleanups next:

1. **Replace `alwaysPrepare` with a single `normalizeExpectedSgf(String raw)` hook** and call it exactly once in the base `setUp()`.
2. **Keep `rawSgf` alongside `expectedSgf`** so normalization never destroys your input.

Those are small mechanical changes, low risk, and they directly address the kind of “suite-only failures” you’ve been seeing as the SGF/domain-action split evolves.

If you want, I can also sketch the exact new `AbstractSgfParserTestCase` shape (no behavioral changes, just determinism and clarity) in a way that won’t invalidate your existing subclasses.
