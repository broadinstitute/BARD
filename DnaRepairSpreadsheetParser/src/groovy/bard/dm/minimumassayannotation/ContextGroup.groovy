package bard.dm.minimumassayannotation

/**
 * Corresponds to an assay-context element that usually groups together few attributes
 */
class ContextGroup {
    String name;
    List<Attribute> attributes = [];
}

class ContextDTO extends ContextGroup {
    Long aid
}