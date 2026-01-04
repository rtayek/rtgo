 Here are the highest-impact, low-risk refactors to pursue at the top level of this codebase:

Separate core from legacy/adapters: Create a clear core/ (engine, domain actions, game-agnostic model) vs adapters/ (SGF, GTP, GUI, server) vs legacy/ (old Move, Observable-era code) package structure. Move Swing/GTP/server code out of model and core packages to enforce the “engine is SGF-free and protocol-free” boundary.

Tighten Board hierarchy: Split Board.java into interface + concrete files (BoardImpl, BoardFactory, Neighborhood). Remove the static factory field; inject a Board.Factory or add a cloneFrom(Board) to avoid casts and make testing cleaner.

Immutable value types: Convert simple data holders to records (equipment.Point, lightweight DTOs in games/*, maybe controller message types). This fixes equality semantics and prevents accidental mutation across the codebase.

Modernize collections/Stacks: Replace Stack with Deque (e.g., games/go/GoState, model.Model state stacks). This is a safe drop-in and improves clarity/performance.

Centralize round-trip test helpers: Collapse duplicated SGF/Model/Tree round-trip test scaffolding into shared utilities (one parameterized SGF suite, one model round-trip suite using a common SGF key set, a WatchedTestCase base for the watcher rule). This reduces maintenance and makes regressions easier to spot.

Harden IO/socket utilities: In io/, server/, and controller/, add try-with-resources and socket timeouts to avoid hangs/timeouts in tests; keep network concerns out of core packages.

Observer/notification cleanup: Replace java.util.Observable in model.Model with a small listener interface or PropertyChangeSupport; this removes deprecation warnings and decouples UI/adapter listeners from the core model.

SGF isolation lint: Add a simple guard (build-time check or test) that core packages (core, model, games) must not import sgf.*, to enforce the architectural boundary you described.

These steps align with the multi-game, SGF-free core direction and reduce accidental coupling and duplication without large behavioral changes. If you want, I can start with the Board split and the SGF round-trip test consolidation.

how do i export your response?


You can select the text in this chat and copy it. If you want 