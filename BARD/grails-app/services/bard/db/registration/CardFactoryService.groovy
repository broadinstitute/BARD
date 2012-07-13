package bard.db.registration

import org.apache.commons.lang.StringUtils

class CardFactoryService {

    List<CardDto> createCardDtoListForAssay(Assay assay) {
        List<CardDto> cards = new ArrayList<CardDto>()
        if (assay == null || assay.getMeasureContextItems() == null) {
            return cards
        }
        Set<MeasureContextItem> items = assay.getMeasureContextItems();
        List<MeasureContextItem> itemList = items.sort { a, b -> a.attributeElement?.label <=> b.attributeElement?.label }
        for (MeasureContextItem item : itemList) {
            // get all the assay-level context items at the top of the hierarchy
            // and create one card per item
            if (item.getMeasureContext() == null &&  // TODO add isAssayLevelContextItem() method to domain class
                item.getParentGroup() == null) { // TODO add isChildContextItem() to domain class

                CardDto card = new CardDto()
                cards.add(card)
                card.title = generateCardTitle(item) // TODO change this to call out to Dictionary REST API by adding a DictionaryLookupService

                List<MeasureContextItem> itemsForLine = new ArrayList<MeasureContextItem>()
                itemsForLine.add(item)
                if (item.children) {
                    itemsForLine.addAll(item.children) // TODO change this to get all descendants
                }

                for (MeasureContextItem lineItem : itemsForLine) {
                    card.lines.add(createCardLineDtoForMeasureContextItem(lineItem))
                }
            }
        }
        return cards
    }

    /**
     * @param item a parent context item
     * @return the appropriate title (with special handling for assay component role)
     */
    protected String generateCardTitle(MeasureContextItem item) {
        if (item.attributeElement.label == "assay component role") {
            return item.valueDisplay
        }
        else {
            for (MeasureContextItem child : item.getChildren()) {
                if (child.attributeElement.label == "assay component role") {
                    return child.valueDisplay
                }
            }
        }
        return item.attributeElement.label
    }

    private CardLineDto createCardLineDtoForMeasureContextItem(MeasureContextItem item) {
        CardLineDto line = new CardLineDto()
        // TODO change this to call out to Dictionary REST API by adding a DictionaryLookupService
        line.attributeLabel = item.attributeElement.label
        line.attributeDefinition = item.attributeElement.description
        line.valueLabel = item.valueDisplay
        if (item.valueElement != null) { // TODO add hasControlledVocabularyValue() method to MeasureContextItem
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