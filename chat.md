ncapsulated TttSpec (private finals, accessors, on-board check, validation helper) and aligned TttState/TttPlugin/renderer usage with the new getters.
Added TttMove.Place.of(spec, point) with on-board validation and a getter for the point; TttActionApplier now uses the validated factory.
Simplified TttActionMapper signature (no spec argument) since moves can now be pre-validated.
Introduced TttRenderer helper and wired TttPlugin to use it, keeping rendering logic out of the core plugin.
Removed the Action.Metadata variant from the game-agnostic actions and pruned the TTT tests accordingly.