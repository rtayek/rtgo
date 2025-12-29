package model;

import java.util.List;

/**
 * Simple, model-owned representation of an SGF-like property.
 */
public record RawProperty(String id, List<String> values) {}
