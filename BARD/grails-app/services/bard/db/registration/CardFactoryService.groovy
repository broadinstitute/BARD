package bard.db.registration

import org.apache.commons.lang.StringUtils

class CardFactoryService {

    private static final String ASSAY_COMPONENT_ROLE = "assay component role"

    List<CardDto> createCardDtoListForAssay(Assay assay) {
        List<CardDto> cards = new ArrayList<CardDto>()
        if (assay == null || assay.getAssayContexts() == null) {
            return cards
        }

        for (AssayContext assayContext : assay.assayContexts.sort { a,b -> a.id <=> b.id}) {
            CardDto cardDto = createCardDto(assayContext)
            cards.add(cardDto)
        }
        return cards
    }

    public CardDto createCardDto(AssayContext assayContext) {
        CardDto cardDto = new CardDto()
        cardDto.id = assayContext.id
        cardDto.title = assayContext.contextName
        for (AssayContextItem assayContextItem : assayContext.assayContextItems) {
            CardLineDto line = createCardLineDtoForAssayContextItem(assayContextItem)
            if (line) {
                cardDto.lines.add(line)
            }
        }
        cardDto
    }



    private CardLineDto createCardLineDtoForAssayContextItem(AssayContextItem item) {
        CardLineDto line = null
        // TODO change this to call out to Dictionary REST API by adding a DictionaryLookupService
        if (item) {
            line = new CardLineDto()
            line.id = item.id
            line.attributeLabel = item.attributeElement.label
            line.attributeDefinition = item.attributeElement.description
            line.valueLabel = item.valueDisplay
            if (item.valueElement != null) { // TODO add hasControlledVocabularyValue() method to AssayContextItem
                line.valueDefinition = item.valueElement.description
            }
        }
        return line
    }
}

class CardDto {
    Long id
    String title
    List<CardLineDto> lines = new ArrayList<CardLineDto>()
}

class CardLineDto {
    String attributeLabel;
    String attributeDefinition;
    String valueLabel;
    String valueDefinition;
    Long id;

    boolean isAttributeDefinitionAvailable() {
        return StringUtils.isNotBlank(attributeDefinition)
    }

    boolean isValueDefinitionAvailable() {
        return StringUtils.isNotBlank(valueDefinition)
    }
}