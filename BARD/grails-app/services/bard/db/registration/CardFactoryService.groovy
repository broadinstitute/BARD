package bard.db.registration

import org.apache.commons.lang.StringUtils

class CardFactoryService {

    List<CardDto> createCardDtoListForAssay(Assay assay) {
        List<CardDto> cards = new ArrayList<CardDto>()
        for (MeasureContextItem item : assay.getMeasureContextItems()) {
            // get all the assay-level context items at the top of the hierarchy
            // and create one card per item
            if (item.getMeasureContext() == null &&  // TODO add isAssayLevelContextItem() method to domain class
                item.getParentGroup() == null) { // TODO add isChildContextItem() to domain class

                CardDto card = new CardDto()
                cards.add(card)
                card.title = item.attributeElement.label // TODO change this to call out to Dictionary REST API by adding a DictionaryLookupService

                List<MeasureContextItem> itemsForLine = new ArrayList<MeasureContextItem>()
                itemsForLine.add(item)
                itemsForLine.addAll(item.children) // TODO change this to get all descendants

                for (MeasureContextItem lineItem : itemsForLine) {
                    card.lines.add(createCardLineDtoForMeasureContextItem(lineItem))
                }
            }
        }
        return cards
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