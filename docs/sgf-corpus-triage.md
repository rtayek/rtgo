# SGF corpus rejection triage (2026-10-08)

## Baseline

From `mf`: `./gradlew sgfCorpus -PsgfDir=../rtgo`.

- 828 SGF files (case-insensitive extension), 771 parsed, 57 rejected.
- All 57 rejected paths are under `data/`; no additional failures outside that directory.
- The 57 are **parser rejections**, not proof of invalid SGF.
- Preserve originals and their existing directory structure until references/tests have been checked.

## Proposed categories (provisional)

| Proposed destination | Current source paths | Rationale / next check |
| --- | --- | --- |
| `data/test-cases/legacy/` | `data/strangesgf/FF[3] (SiZe, AddBlack,...)/` (12 rejected files) | Historical mixed-case properties such as `GaMe`, `SiZe`, `AddBlack`; requires explicit legacy dialect handling, not FF4 syntax relaxation. |
| `data/test-cases/wrapped/` | `data/strangesgf/Leading + trailing char/` (6 rejected files); `data/strangesgf/multi (collection files)/` (3); `data/sgf/nosize/smartgo43.sgf` | Some contain genuine SGF preceded/followed by mail headers or prose; `smartgo43.sgf` contains literal backslash-n between collections. Decide whether to extract/normalize rather than treating them as raw SGF. |
| `data/test-cases/malformed/` | `data/strangesgf/() not same count/`, `; missing/`, `Comments/`, `FileSize, too long Strings/`, `Labels/`, `exception (too many chars in comment and other IDs)/`, `file small + too small/`, `hand work errors/`, `instant Ko take back/`, `variations/`, `GM, FF/Test GM[0].sgf`, `data/excludedsgf/test1.sgf`, `data/wasinroot/a1a2.sgf` | Mostly structurally broken examples, but inspect each before assigning expected-rejection status. `GM[0]` is a semantic validity failure. |
| **Manual review** | `data/strangesgf/legal/035 tabs instead of spaces.sgf` and any uncertain members of the above | The inspected file has an empty variation `()`; tabs themselves are not the problem. Verify original intended test behavior. |

## Inspected examples

- `data/strangesgf/FF[3] (SiZe, AddBlack,...)/FF3 JIANG1.sgf`: starts with `(;GaMe[1]VieW[]SiZe[19]` and later `AddBlack[dd][dp][pp][pd]`; clearly older non-FF4 property vocabulary.
- `data/strangesgf/Leading + trailing char/valid + email header.sgf`: begins with email headers and then a game tree; not a bare SGF file.
- `data/strangesgf/multi (collection files)/multiEasy.sgf`: begins with `A collection of 40 easy problems.` followed by SGF trees.
- `data/strangesgf/Leading + trailing char/xyz(;..)xyz.sgf`: wraps an otherwise recognizable tree in `xyz` text.
- `data/strangesgf/legal/035 tabs instead of spaces.sgf`: has a `()` empty branch; tabs occur elsewhere but are allowed whitespace.
- `data/strangesgf/GM, FF/Test GM[0].sgf`: syntactically parseable but `GM[0]` is rejected by the game-type model.
- `data/strangesgf/Comments/], [ in C/test.sgf`: includes unescaped `]` inside a comment, terminating the SGF value early.
- `data/sgf/nosize/smartgo43.sgf`: includes literal `\\n` between SGF trees, rather than actual newlines.

## Before moving

1. Inventory references to these filenames in RTGo source, tests, docs, and scripts.
2. Produce a **file-by-file manifest** of all 57 with old path, proposed new path, and expected outcome. The table above groups candidates; it is not yet a verified per-file classification.
3. Inspect the legacy and wrapped examples and classify their intended input formats.
4. Move in one reviewable commit with `git mv`, preserving relative paths and recording old-to-new mappings.
5. Update path references and rerun the `mf` corpus baseline; retain negative tests instead of silently dropping them.

**No SGF files have been moved by this triage.**
