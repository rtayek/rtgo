package model;

import java.util.List;
import sgf.SgfProperty;

final class NodeAnnotations {
    // Raw, lossless SGF properties that were not mapped into actions.
    final List<SgfProperty> unapplied;

    // Optional: diagnostics or mapping decisions, not needed for round-trip
    final List<String> notes;

    NodeAnnotations(List<SgfProperty> unapplied,List<String> notes) {
        this.unapplied=unapplied;
        this.notes=notes;
    }

    static NodeAnnotations empty() { return new NodeAnnotations(List.of(),List.of()); }
}
