# AGENTS.md

This file applies to the `rtgo` repository.

## Goals

- Keep behavior stable while refactoring.
- Reduce duplication, especially in SGF/model save/restore paths.
- Keep code easy to scan and reason about.

## Project Layout

- `src/` main code (model, sgf, gui, controller, etc.)
- `tst/`, `slow/`, `suites/` tests
- `resources/` runtime resources
- `legacy/src/` legacy code compiled with main sources

## Build And Test

- Compile only:
  - `./gradlew -q compileJava compileTestJava -PbuildDir=build-alt-<tag>`
- Run focused tests while refactoring SGF/model I/O:
  - `./gradlew -q test --tests sgf.SgfUnitTestCase --tests sgf.SgfRoundTripTestCase -PbuildDir=build-alt-<tag>`
  - `./gradlew -q test --tests model.SaveTestCase --tests model.TopologyAndShapeTestCase -PbuildDir=build-alt-<tag>`
- Use a unique `-PbuildDir=build-alt-<tag>` to avoid lock/contention with other runs.

## Refactoring Rules

- Prefer removing wrappers/convenience overloads when they only forward calls.
- Prefer direct calls to core methods over null-swallowing helper wrappers.
- Keep null handling at boundaries (file/input edges), not spread across many pass-through methods.
- Put independent methods first; methods that depend on them lower in the file.
- Keep fields near the bottom of the class.
- Keep comments minimal and technical.
- Avoid broad formatting churn; preserve compact style where possible.

## SGF / Save / Restore Guidance

- Treat `MNode.toString()` as diagnostic output, not serialization.
- Keep serialization explicit in dedicated save/restore methods.
- Current consolidation focus is in:
  - `src/model/ModelIo.java`
  - `src/sgf/MNode.java`
  - `src/sgf/SgfNode.java`
- Before adding new save/restore helpers, check those files first to avoid duplication.

## Architecture Constraints

- Core model/rules should not depend on UI.
- SGF is an adapter boundary; avoid leaking SGF-specific concerns into gameplay logic.
- Keep test behavior as the source of truth for functional intent.

## Change Discipline

- Make small, reversible edits.
- Compile after each meaningful step.
- If behavior changes are intentional, add/update tests in the same change.
