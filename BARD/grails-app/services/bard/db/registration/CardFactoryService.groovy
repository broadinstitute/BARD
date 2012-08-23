package bard.db.registration

import org.apache.commons.lang.StringUtils

class CardFactoryService {

    private static final String ASSAY_COMPONENT_ROLE = "assay component role"

    List<CardDto> createCardDtoListForAssay(Assay assay) {
        List<CardDto> cards = new ArrayList<CardDto>()
        if (assay == null || assay.getAssayContextItems() == null) {
            return cards
        }
        List<AssayContextItem> items = assay.getAssayContextItems() as List
        items.removeAll { item -> item.getAssayContext() != null } // TODO add isAssayLevelContextItem() method to domain class
        Map<Long, List<AssayContextItem>> grouping = items.groupBy { item ->
            if (item.parentGroup != null)
                return item.parentGroup.id
            else
                return item.id
        }

        List<Long> groupingIds = grouping.keySet() as List
        Collections.sort(groupingIds)
        for (Long groupingId : groupingIds) {
            List<AssayContextItem> itemsInGroup = grouping.get(groupingId)
            itemsInGroup = itemsInGroup.sort { a, b -> a.attributeElement?.label <=> b.attributeElement?.label }
            CardDto card = new CardDto()
            cards.add(card)
            for (AssayContextItem item : itemsInGroup) {
                if (item.parentGroup == null || item.parentGroup.id == item.id) {
                    card.title = generateCardTitle(item)
                }
                card.lines.add(createCardLineDtoForAssayContextItem(item))
            }
        }
        return cards
    }

    /**
     * @param item a parent context item
     * @return the appropriate title (with special handling for assay component role)
     */
    protected String generateCardTitle(AssayContextItem item) {
        if (item.attributeElement.label == ASSAY_COMPONENT_ROLE) {
            return item.valueDisplay
        }
        else {
            for (AssayContextItem child : item.getChildren()) {
                if (child.attributeElement.label == ASSAY_COMPONENT_ROLE) {
                    return child.valueDisplay
                }
            }
        }
        return item.attributeElement.label
    }

    private CardLineDto createCardLineDtoForAssayContextItem(AssayContextItem item) {
        CardLineDto line = new CardLineDto()
        // TODO change this to call out to Dictionary REST API by adding a DictionaryLookupService
        line.attributeLabel = item.attributeElement.label
        line.attributeDefinition = item.attributeElement.description
        line.valueLabel = item.valueDisplay
        if (item.valueElement != null) { // TODO add hasControlledVocabularyValue() method to AssayContextItem
            line.valueDefinition = item.valueElement.description
        }
        return line
    }
}

class CardDto {
    String title
    List<CardLineDto> lines = new ArrayList<CardLineDto>()
}

class CardLineDto {
    String attributeLabel;
    String attributeDefinition;
    String valueLabel;
    String valueDefinition;

    boolean isAttributeDefinitionAvailable() {
        return StringUtils.isNotBlank(attributeDefinition)
    }

    boolean isValueDefinitionAvailable() {
        return StringUtils.isNotBlank(valueDefinition)
    }
}