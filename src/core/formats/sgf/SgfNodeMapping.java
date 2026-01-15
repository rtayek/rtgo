package core.formats.sgf;

import java.util.List;
import sgf.MNode;
import sgf.SgfProperty;
import core.engine.DomainAction;

/**
 * Result of mapping an SGF node to domain actions plus unapplied properties.
 */
public record SgfNodeMapping(List<DomainAction> actions,List<SgfProperty> extras) {
    public static SgfNodeMapping empty() { return new SgfNodeMapping(List.of(),List.of()); }

    public void applyExtrasTo(MNode node) {
        if(node==null||extras.isEmpty()) return;
        node.sgfProperties().removeAll(extras);
        for(SgfProperty property:extras) node.addExtraProperty(property);
    }
}
