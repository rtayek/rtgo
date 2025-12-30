Implemented the tighter construction path for TTT moves and removed a lingering validation hole:

TttMove.Place constructor is now package-private; creation should go through Place.of(...).
TttMoveCodec now holds a TttSpec (defaults to 3x3) and validates via Place.of, so parsing can’t emit off-board moves.
TttPlugin initializes its codec with the default spec.
TttActionApplier already uses Place.of with state spec; left the applier’s defensive checks in place.
All tests pass (./gradlew test), with only existing warnings about deprecated APIs.