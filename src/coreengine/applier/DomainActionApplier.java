package coreengine.applier;

import java.util.List;
import core.engine.DomainAction;
import model.Model;

/**
 * Applies DomainActions to a Model. Keeps Model free of decoding concerns.
 */
public class DomainActionApplier {
    private final Model model;

    public DomainActionApplier(Model model) { this.model=model; }

    public void apply(DomainAction action) { DomainAction.applyTo(action,model); }
    public void applyAll(List<DomainAction> actions) {
        for(DomainAction action:actions) apply(action);
    }
}
