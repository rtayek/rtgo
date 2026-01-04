Here is a clear, candid answer.

---

## What I remember about you (persisted context)

From prior conversations, I have durable memory of the following **work-relevant preferences and background**:

* **Identity / background**

  * You are a **very experienced programmer** (≈60 years total), with deep Java, C, and C++ experience.
  * You think in terms of **architecture, invariants, and evolution over decades**, not green-field novelty.
  * You are comfortable refactoring large, old systems and are not afraid to “peel the onion”.

* **Coding preferences**

  * You prefer **minimal comments**; code structure should carry meaning.
  * You treat **tests as executable specifications**.
  * You dislike ceremony, over-abstraction, and “framework gravity”.
  * You prefer **package-private by default**, explicit visibility only when justified.
  * You prefer **camelCase enum constants** (not ALL_CAPS).
  * You prefer **fields at the bottom of classes**.
  * You are sensitive to **inner-loop performance and conceptual cleanliness**.
   * You an Object-Oriented purist, but not a performance purist.

* **Working style**

  * You work iteratively with **small, reversible steps**.
  * You value **round-trip correctness** (e.g., SGF load → model → save must be lossless).
  * You prefer **architecture that can survive multiple games, UIs, and protocols**.
  * You explicitly like having a “control case” (e.g., Tic-Tac-Toe) to keep abstractions honest.

* **Current project orientation**

  * Long-lived Java codebase (originating ~Java 1.0 era).
  * Actively refactoring toward:

    * SGF out of the core model
    * Game-agnostic engine/actions
    * Pluggable games (Go, TTT)
    * Adapter boundaries (SGF, GTP, UI, networking)
  * You are willing to temporarily regress or revert commits to regain invariants.

I **do not** retain personal, emotional, or irrelevant details beyond what affects technical collaboration.

---

## What personality I am using with you

I am currently operating under a **formal, senior-consultant / systems-architect mode**, characterized by:

* Direct, technical, and structured communication
* Bias toward **long-term maintainability and conceptual integrity**
* Willingness to say “this is a boundary violation” or “this abstraction leaks”
* Focus on **trade-offs**, not dogma
* Minimal fluff, minimal emojis, minimal motivational language

This aligns well with how you reason and how you have been driving this refactor.

Separately, I *also* know that you often enjoy a more **snarky / professor-style tone** in other contexts — but for this project, we have naturally converged on a **serious architectural review mode**, which is appropriate.

---

## Important clarification

I **do not remember code details unless they are reintroduced**, and uploaded tarballs expire.
What *does* persist is:

* Your architectural intent
* Your preferences
* The mental model of where the project is headed

That is why your request for a **handoff summary** and **design principles** was exactly the right move.

If you want, next we can:

* Produce a **one-page “project README for future-you”**
* Or a **“do not break these invariants” checklist**
* Or a **clean restart plan** that preserves only the good ideas

Just say which.
