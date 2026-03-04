
• From March 3 to March 4, 2026 (last ~24 hours), we did:

  1. Re-established context and constraints in rtgo by reading project .md files and using your AGENTS.md rules.
  2. Ran focused compile/test baselines; SGF failures were traced to the intentional fail-fast null throw path in MNode.toGeneralTree().
  3. Kept fail-fast behavior where you asked (no silent pass on missing inputs).
  4. Tightened file-not-found handling in SGF load paths, and added tests:
      - missing SGF file must throw
      - all keys in Parser.sgfDataKeySet() are loadable/parsible
  5. Worked the logging issue:
      - fixed logger reconfiguration/reset behavior
      - corrected Parser.main() init/level ordering so parser logs appear on console
      - aligned with your rule that Init.first.twice() should be first in main()
  6. Resolved the toReaderOrThrow confusion:
      - method exists in util source
      - ~/.m2 jar was stale
      - republished util to Maven local using alternate build dir (-PbuildDir=...) to bypass locked Eclipse build folders
      - verified new jar now includes toReaderOrThrow(File)
  7. Cleaned util scan artifact files (agents*.json) and rebuilt a proper util/agent.md from util/handoff.md.
  8. Current repo state:
      - util: clean
      - rtgo: still has multiple in-progress edits (logging/SGF/tests/CLI), not yet consolidated into a clean commit set.


• Here’s a concise recap of the last few days (March 3-4, 2026):

  1. rtgo refactor resumed and baseline re-established.
  2. SGF regressions were investigated; the fail-fast null throw in MNode.toGeneralTree() was kept (per your request) for debugging.
  3. File-not-found handling was tightened:
      - Parser.getSgfData(File) now throws on missing/non-file/unreadable input.
      - Added a regression test for missing SGF files.
      - Fixed CommandLine.java to use existing FileIO.toReader(...) with explicit fail-fast file checks (removed invalid toReaderOrThrow(...) call).
  4. Logging issue was fixed:
      - Parser.main() init order was corrected so Init.first.twice() runs first, then log levels are set.
      - Init parser default logger level changed to INFO so parser logs show on console.
      - Logging setup was hardened to reattach handlers after reset.
  5. Added SGF coverage test:
      - New test reads every key from Parser.sgfDataKeySet() and verifies load + parse.
  6. util directory cleanup:
      - Removed generated agents*.json scan artifacts (kept only two, then you decided they can be deleted).
      - Recreated a clean project guide as util/agent.md from util/handoff.md.
  7. Current util markdown state is now:
      - agent.md
      - handoff.md