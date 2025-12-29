package core.engine;

import java.util.List;

public final class GameNode {
    public final List<DomainAction> actions;
    public final NodeAnnotations annotations; // may be empty
    public final List<GameNode> children;
    public GameNode parent;

    public GameNode(List<DomainAction> actions,NodeAnnotations annotations,List<GameNode> children) {
        this.actions=actions;
        this.annotations=annotations;
        this.children=children;
    }
}
