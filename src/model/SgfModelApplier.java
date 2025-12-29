package model;
import java.util.List;

/**
 * Applies DomainActions to a Model. Keeps Model free of SGF/GTP decoding.
 */
public class SgfModelApplier {
    private final Model model;

    public SgfModelApplier(Model model) { this.model=model; }

    public void apply(DomainAction action) { DomainAction.applyTo(action,model); }
    public void applyAll(List<DomainAction> actions) {
        for(DomainAction action:actions) apply(action);
    }
}
