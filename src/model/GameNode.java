package model;

import java.util.List;

final class GameNode {
    final List<DomainAction> actions;
    final NodeAnnotations annotations; // may be empty
    final List<GameNode> children;
    GameNode parent;

    GameNode(List<DomainAction> actions,NodeAnnotations annotations,List<GameNode> children) {
        this.actions=actions;
        this.annotations=annotations;
        this.children=children;
    }
}

