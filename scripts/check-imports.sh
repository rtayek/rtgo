#!/bin/sh
# Import firewall for rtgo (POSIX / Bourne shell)
#
# Usage:
#   ./scripts/check-imports.sh
#   PHASE=1 ./scripts/check-imports.sh
#
# Phase 0: forbid obvious wrong-direction imports, allow model.*
# Phase 1: additionally forbid model.* from core.* and games.*

PHASE="${PHASE:-0}"

# Resolve repo root (portable)
SCRIPT_DIR=`dirname "$0"`
REPO_ROOT=`cd "$SCRIPT_DIR/.." && pwd`
SRC_ROOT="$REPO_ROOT/src"

if [ ! -d "$SRC_ROOT" ]; then
  echo "ERROR: src/ not found at: $SRC_ROOT" >&2
  exit 2
fi

fail=0

# ------------------------------------------------------------------------------
# check_forbidden_imports
#
# Arguments:
#   $1 = rule name (string)
#   $2 = directory (relative to repo root)
#   $3... = forbidden package prefixes (e.g. sgf. gui. controller.)
# ------------------------------------------------------------------------------
check_forbidden_imports() {
  rule_name="$1"
  dir="$2"
  shift 2

  abs_dir="$REPO_ROOT/$dir"

  # If directory does not exist, silently succeed
  if [ ! -d "$abs_dir" ]; then
    return 0
  fi

  # Build regex: ^\s*import\s+(sgf\.|gui\.|controller\.)
  joined=""
  for p in "$@"; do
    # escape dots for regex
    ep=`printf '%s' "$p" | sed 's/\./\\./g'`
    joined="${joined}${ep}|"
  done
  joined=`printf '%s' "$joined" | sed 's/|$//'`

  re="^[[:space:]]*import[[:space:]]+(${joined})"

  hits=`grep -RIn --include='*.java' -E "$re" "$abs_dir" 2>/dev/null`

  if [ -n "$hits" ]; then
    echo "FAIL: $rule_name"
    echo "$hits"
    echo
    fail=1
  fi
}

# ------------------------------------------------------------------------------
# Phase 0 rules
# ------------------------------------------------------------------------------

# core.* must not depend on formats, UI, controller, server, or IO-ish packages
check_forbidden_imports \
  "core.* must not import sgf/gui/simplegui/controller/server/io/iox/audio" \
  "src/core" \
  sgf. gui. simplegui. controller. server. io. iox. audio.

# games.* must not depend on sgf, UI, controller, or server
check_forbidden_imports \
  "games.* must not import sgf/gui/simplegui/controller/server" \
  "src/games" \
  sgf. gui. simplegui. controller. server.

# Extra safety: core.engine must not import UI directly
check_forbidden_imports \
  "core.engine* must not import gui/simplegui" \
  "src/core/engine" \
  gui. simplegui.

# ------------------------------------------------------------------------------
# Phase 1 rules (tighten architecture; expected to fail initially)
# ------------------------------------------------------------------------------

if [ "$PHASE" = "1" ]; then
  check_forbidden_imports \
    "PHASE=1: core.* must not import model.*" \
    "src/core" \
    model.

  check_forbidden_imports \
    "PHASE=1: games.* must not import model.*" \
    "src/games" \
    model.
fi

# ------------------------------------------------------------------------------
# Final result
# ------------------------------------------------------------------------------

if [ "$fail" -ne 0 ]; then
  echo "Import firewall: FAILED (PHASE=$PHASE)"
  exit 1
fi

echo "Import firewall: OK (PHASE=$PHASE)"
exit 0
