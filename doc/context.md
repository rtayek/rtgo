• Project Context

  - Long-lived Java Go/TTT refactor aiming for a multi-game engine with strict separation:
    formats → actions → appliers → games/UI, keeping SGF round-trip lossless and isolating
    legacy code.
  - Core pipeline now maps SGF (and future adapters) to neutral DomainActions, stores
    unapplied SGF metadata (RawProperty/NodeAnnotations), and applies actions via game-
    specific appliers; TTT serves as a canary to guard against Go-specific assumptions.
  - Goals: finish stripping SGF from the core, eliminate legacy Move usage in hot paths,
    stabilize the DomainAction → Applier flow for Go and TTT, and tidy the root/src layout
    while keeping incremental, test-driven change.
  - Guardrails: dependencies flow inward (formats stay out of games/core), mapping stays pure,
    appliers mutate state, tests are the spec, and round-trip fidelity is non-negotiable;
    networking is treated as another adapter, not mixed into core logic.
