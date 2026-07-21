# Overview

Java-based Go program being refactored into a multi-game framework.

Primary goals:

* Preserve Go behavior correctness
* Preserve exact SGF round-trip behavior
* Separate engine from SGF/GTP/UI concerns
* Support multiple games via a shared action-based architecture

Games:

* Go (primary)
* Tic-Tac-Toe (validation / control)
