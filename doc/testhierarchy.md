Updated 1/16/26 after SGF test consolidation.

## What the hierarchy looks like

At a high level, the SGF tests now use a smaller JUnit4 base:

```
AbstractSgfParserTestCase
  |
  +-- SgfRoundTripTestCase         (@RunWith(Parameterized.class))
```

General unit tests:

```
SgfUnitTestCase
```

Ignored/slow SGF files now live as `@Ignore` tests inside `SgfUnitTestCase`.

## What `AbstractSgfParserTestCase` is doing

It is both:

1. Fixture loader (loads SGF string by key and stores `expectedSgf`), and
2. Test suite (key sanity, parse, flag parsing).

Subclasses get those checks for free.

## What `SgfRoundTripTestCase` adds

It extends the parser suite and adds stronger invariants:

* SGF restore/save round-trip equality
* MNode round-trip checks
* Model round-trip checks
* Canonicalization checks
* Round-trip twice stability

## Notes

* `normalizeExpectedSgf(String raw)` is the only normalization hook. It keeps
  parsing deterministic and makes it clear when a suite expects a prepared SGF.
* `SgfRoundTripTestCase` now uses the parser parameter set (including raw/illegal
  SGF cases) and skips round-trip checks for those inputs.
* Fixture checks that used to live in `SgfFixtureTestCase` now run under
  `SgfRoundTripTestCase`.
* Former `SgfTestSupport` and `TestIoSupport` helpers now live in `SgfHarness`.
