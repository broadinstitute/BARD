package bard.db.registration

import bard.db.dictionary.Descriptor
import bard.db.dictionary.DescriptorLabelComparator
import org.apache.commons.lang.StringUtils

class CardFactoryService {

    private static final String ASSAY_COMPONENT_ROLE = "assay component role"

    Map<String, CardDto> createCardDtoMapForAssay(Assay assay) {
        List<CardDto> cards = new ArrayList<CardDto>()
        if (assay == null || assay.getAssayContexts() == null) {
            return cards.groupBy {it.assaySection}
        }
        List<AssayContext> assayContexts = assay.assayContexts.sort {a, b -> a.id <=> b.id}
        assayContexts = assay.assayContexts.sort {a, b ->
            new DescriptorLabelComparator().compare(a.preferredDescriptor, b.preferredDescriptor)
        }
        for (AssayContext assayContext : assayContexts) {
            CardDto cardDto = createCardDto(assayContext)
            cards.add(cardDto)
        }
        return cards.groupBy {it.assaySection}
    }

    public CardDto createCardDto(AssayContext assayContext) {
        CardDto cardDto = new CardDto()
        cardDto.id = assayContext.id
        cardDto.title = assayContext.contextName
        cardDto.label = assayContext.preferredName

        Descriptor descriptor = assayContext.preferredDescriptor
        if(descriptor){
            cardDto.assaySection = descriptor.getPath().subList(0,1).collect{ it.label }.join(' > ')
        }
        else {
            cardDto.assaySection = 'unknown section'
        }

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
    String label
    String assaySection
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