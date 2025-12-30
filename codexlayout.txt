This layout looks good and aligns with Option B. I’d make one decision early: put the “engine algebra” (Action/DomainAction, GameNode, NodeAnnotations, RawProperty) in one place—core.engine—and drop the duplicate “model.engine” idea. That keeps imports simple and avoids “where does this go?” debates.

The import rules you stated are the right guardrails:

formats → core/games only
games → core, never the reverse
UI reads state, never owns rules
The target tree you showed is exactly what I’d standardize on:


src/
  core/
    api/
    engine/
      applier/
    formats/
      sgf/
      gtp/
    util/
  games/
    go/
      rules/
      adapters/
      render/
      ui/swing/
    ttt/
      rules/
      adapters/
      render/
  ui/
    cli/
    swing/
  legacy/

And to stay honest with RawProperty-only: 
keep SGF/GTP adapters emitting only real engine actions plus RawProperty;
 don’t reintroduce Action.Metadata into the apply path.