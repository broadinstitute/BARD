package bard.dm.minimumassayannotation

/**
 * Corresponds to an assay-context element that usually groups together few attributes
 */
class ContextGroup {
    String name;
    List<ContextItemDto> contextItemDtoList = [];
}

class ContextDTO extends ContextGroup {
    Long aid

    boolean wasSaved

    @Override
    String toString() {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append(name).append("\n")
        for (ContextItemDto contextItemDto : contextItemDtoList) {
            stringBuilder.append(contextItemDto).append("\n")
        }
        return stringBuilder.toString()
    }
}