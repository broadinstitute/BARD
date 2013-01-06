package bard.dm.minimumassayannotation

/**
 * Corresponds to an assay-context element that usually groups together few attributes
 */
class ContextGroup {
    String name;
    List<ContextItemDto> attributes = [];
}

class ContextDTO extends ContextGroup {
    Long aid
}