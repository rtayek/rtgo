(in response to chatgpt.md

This is a solid target. It captures the key boundaries and keeps everything in one source root. The only nits to keep in mind:

Pick one home for the “engine algebra” (Action, GameNode, NodeAnnotations, RawProperty). Your tree puts it in core/engine, which is good—just don’t also keep a parallel “model” layer elsewhere.

Keep the import rules strict:

formats.* → core/games; never the reverse.
games.* → core.*; never core.* → games.*.
ui.* reads state, doesn’t contain rules.
The double “sgf” (core/formats/sgf vs formats/sgf) is fine as long as core/formats/sgf is just the mapper (SGF → actions/raw props) and the parser/types live in formats/sgf.

Legacy quarantine is good; use it early to avoid accidental reuse.

Otherwise, this layout should make multi-game support and Option B (RawProperty-only) much clearer to everyone working in the codebase.