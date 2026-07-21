# Constraints

## Non-negotiable

### Lossless SGF round-trip

Must preserve:

* property existence
* ordering
* sentinel/root behavior
* comments and custom properties

### Engine purity

* No SGF/GTP/UI dependencies in core

### Execution model

* Actions are the execution currency
* SGF is an adapter only

### Determinism

* Execution and tests must be deterministic

## Sentinel Root (RT)

* Acts as root marker
* Execution is a no-op
* Must not be duplicated or lost
* Detection must consider all property storage locations

## Initialization

* Distinguish:

  * one-time initialization
  * replay-time initialization
* Replay must not call destructive initialization (e.g., setRoot())

## Legacy compatibility

* Must preserve:

  * replay semantics
  * stack/history behavior
  * root handling behavior
