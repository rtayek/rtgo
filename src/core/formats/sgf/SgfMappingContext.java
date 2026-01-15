package core.formats.sgf;

/**
 * Context for mapping SGF nodes to domain actions without model dependencies.
 */
public record SgfMappingContext(int depth,String boardTopologyPrefix,String boardShapePrefix) {}
