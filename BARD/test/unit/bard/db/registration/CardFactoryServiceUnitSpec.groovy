package bard.db.registration

import bard.db.dictionary.Element
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Unit tests for the CardFactoryService.
 */
@TestFor(CardFactoryService)
@Mock(Assay)
class CardFactoryServiceUnitSpec extends Specification {

    CardFactoryService cardFactoryService

    MeasureContextItem singleItem

    void setup() {
        cardFactoryService = Mock()
        singleItem = new MeasureContextItem(attributeElement: new Element(label: "Test"))
    }

    void "test createCardDtoListForAssay #label"() {

        given:
        if (assay != null) {
            assay.addToMeasureContextItems(items)
        }

        when:
        List<CardDto> cardDtos = cardFactoryService.createCardDtoListForAssay(assay)

        then:

        assert cardDtos.size() == cardCount

        where:
        label                           | assay         | items     | cardCount
        "assay with no items"           | new Assay()   | []        | 0
        "assay with 1 item"             | new Assay()   | []        | 1
        "assay with 2 items & 1 child"  | new Assay()   | []        | 3
        "null assay"                    | null          | []        | 0
    }

}
