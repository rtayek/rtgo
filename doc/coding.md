
### Coding Standards

* Java records for immutable dat
* Default/package-private visibility unless there is a clear reason otherwise
* Enums use **lowerCamelCase** values
* Tests are the specification; code comments are secondary

### names

* **Constants/static finals:** prefer `camelCase` (your preference), not all-caps.
* Names should reflect intent: `mapper`, `applier`, `codec`, `adapter`, `plugin`, `renderer`.

### Class structure and organization

* Put **field declarations at the bottom** of the class (your preference).
* Keep classes small and single-purpose. When a class becomes a “directory,” split it.

### Data modeling

* Prefer:

  * `record` for immutable value types (`RawProperty`, small specs, ids, etc.).
  * sealed hierarchies for closed sets (`Action` / `DomainAction` families).
* Avoid inheritance when it’s just modeling a tagged union. Prefer an enum tag or sealed interface + records.

### Tests define behavior

* Tests are the functional spec.


