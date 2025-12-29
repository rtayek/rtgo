package model;

import java.util.List;

/**
 * Lossless per-node annotations decoupled from SGF types.
 */
public record NodeAnnotations(List<RawProperty> unapplied, List<String> notes) {
    public static NodeAnnotations empty() { return new NodeAnnotations(List.of(),List.of()); }
}
