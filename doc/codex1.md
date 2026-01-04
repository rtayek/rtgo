Great. Here’s how I’d tackle those two items:

1) Centralize round-trip tests

Add tst/common/WatchedTestCase (abstract, installs @Rule MyTestWatcher).
Add tst/common/SgfFixtures with:
Collection<Object> sgfKeys() (union of sgfDataKeySet() + sgfFiles()).
String loadAndPrepare(Object key) to do today’s getSgfData + prepareSgf + strip control chars and parentheses validation.
Replace the multiple SGF parameterized classes with one SgfRoundTripParameterizedTest that uses @Parameters(name="{0}") sgfKeys() and runs parse/flags/hex/restore-save/canonical checks from a shared helper.
Replace the model round-trip variants with one parameterized ModelRoundTripTest that calls a helper assertRoundTrip(Model::new, key) (and Model::new("", true) for the old way if still needed).
Tree round-trip: keep one parameterized class that calls a shared assertTreeRoundTrip(...) helper; drop the duplicated fixed variant.
Remove the now-redundant abstract bases once helpers are in place.
2) Package/layout moves (high-level direction)

Core vs adapters vs legacy:
Keep core/ (engine, domain actions, game-agnostic model helpers) free of SGF/GTP/GUI imports.
Move SGF/GTP/server/GUI into adapters/ (or clearer package names like formats.sgf, protocol.gtp, ui.swing, net.server).
Quarantine legacy Move/Observable-era code into a legacy/ package.
Equipment/board:
Split nested classes in Board.java into separate files (BoardImpl, BoardFactory, Neighborhood) and inject Board.Factory instead of static.
Games:
Keep game-specific code under games.<game>; ensure they depend only on core + equipment, not SGF.
If you want, I can start by adding the WatchedTestCase + SgfFixtures and consolidating SGF/model round-trip tests, then propose a package move plan in a follow-up.