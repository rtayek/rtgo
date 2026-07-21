# Architecture

## Layers

### Core / Engine

* Execution model
* Game tree (GameNode)
* Action application
* Annotation preservation
* No dependency on SGF, GTP, or UI

### Game-specific logic

* Rules and legality
* State transitions
* Board semantics
* Renderers/adapters

### Format adapters

* SGF → DomainAction mapping
* Preserve uninterpreted properties
* Future: GTP, network codecs

### UI / Transport

* Rendering and input
* CLI / Swing / future client-server
* No game rules

## Dependency Direction

formats → core → games → UI
(runtime inversion allowed, compile-time firewall enforced)

## Data Flow

SGF
→ SgfDomainActionMapper
→ GameNode (DomainAction list)
→ DomainActionApplier
→ Model / GameState

## Round-trip Flow

SGF properties
→ actions (semantic)
→ annotations (non-semantic)
→ serialized back without loss or reordering
