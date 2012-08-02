package bard.db.registration

import bard.db.dictionary.Element
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Unit tests for the CardFactoryService.
 */
@TestFor(CardFactoryService)
@Mock([Assay, MeasureContextItem])
class CardFactoryServiceUnitSpec extends Specification {

    void setup() {

    }

    void "test createCardDtoListForAssay with null Assay"() {
        given:
            Assay assay = null

        when:
            List<CardDto> cardDtos = service.createCardDtoListForAssay(assay)

        then:
            assert cardDtos.size() == 0
    }

    void "test createCardDtoListForAssay with Assay with no MeasureContextItems"() {
        given:
        Assay assay = new Assay()

        when:
        List<CardDto> cardDtos = service.createCardDtoListForAssay(assay)

        then:
        assert cardDtos.size() == 0
    }

    void "test createCardDtoListForAssay with Assay having 1 MeasureContextItem"() {
        given:
        Assay assay = new Assay()
        assay.measureContextItems = new HashSet<MeasureContextItem>()
        String card1Label = "Label 1"
        String card1Value = "Value 1"
        MeasureContextItem item1 = new MeasureContextItem()
        item1.id = 1
        item1.parentGroup = item1
        item1.attributeElement = new Element(label: card1Label)
        item1.valueDisplay = card1Value
        assay.measureContextItems.add(item1)

        when:
        List<CardDto> cardDtos = service.createCardDtoListForAssay(assay)

        then:
        assert cardDtos.size() == 1
        CardDto card1 = cardDtos.get(0)
        assert card1.title == card1Label
        assert card1.lines.size() == 1
        CardLineDto line1 = card1.lines.get(0)
        assert line1.attributeLabel == card1Label
        assert line1.valueLabel == card1Value
        assert line1.attributeDefinition == null
        assert !line1.attributeDefinitionAvailable
        assert line1.valueDefinition == null
        assert !line1.valueDefinitionAvailable
    }

    void "test createCardDtoListForAssay with Assay having an assay-level MeasureContextItem and a result-specific MeasureContextItem"() {
        given:
        Assay assay = new Assay()
        assay.measureContextItems = new HashSet<MeasureContextItem>()
        String card1Label = "Label 1"
        String card1Value = "Value 1"
        MeasureContextItem item1 = new MeasureContextItem()
        item1.parentGroup = null
        item1.attributeElement = new Element(label: card1Label)
        item1.valueDisplay = card1Value
        assay.measureContextItems.add(item1)
        MeasureContextItem item2 = new MeasureContextItem(measureContext: new MeasureContext())
        assay.measureContextItems.add(item2)

        when:
        List<CardDto> cardDtos = service.createCardDtoListForAssay(assay)

        then:
        assert cardDtos.size() == 1
        CardDto card1 = cardDtos.get(0)
        assert card1.title == card1Label
        assert card1.lines.size() == 1
        CardLineDto line1 = card1.lines.get(0)
        assert line1.attributeLabel == card1Label
        assert line1.valueLabel == card1Value
        assert line1.attributeDefinition == null
        assert !line1.attributeDefinitionAvailable
        assert line1.valueDefinition == null
        assert !line1.valueDefinitionAvailable
    }

    void "test createCardDtoListForAssay with Assay having 2 MeasureContextItems one with child"() {
        given:
        Assay assay = new Assay()
        assay.measureContextItems = new HashSet<MeasureContextItem>()
        String card1Label = "Label 1"
        String card1Value = "Value 1"
        MeasureContextItem item1 = new MeasureContextItem()
        item1.id = 1
        item1.parentGroup = item1
        item1.attributeElement = new Element(label: card1Label)
        item1.valueDisplay = card1Value
        assay.measureContextItems.add(item1)

        MeasureContextItem item2 = new MeasureContextItem()
        String item2Label = "Label 2"
        String item2Value = "Value 2"
        item2.id = 2
        item2.parentGroup = item2
        item2.attributeElement = new Element(label: item2Label)
        item2.valueDisplay = item2Value
        assay.measureContextItems.add(item2)

        MeasureContextItem child = new MeasureContextItem()
        String childLabel = "Child Label"
        String childValue = "Child Value"
        String childLabelDef = "Child Label Def"
        String childValueDef = "Child Value Def"
        child.attributeElement = new Element(label: childLabel, description: childLabelDef)
        child.valueDisplay = childValue
        child.valueElement = new Element(label: childValue, description: childValueDef)
        item2.addToChildren(child)
        assay.measureContextItems.add(child)

        when:
        List<CardDto> cardDtos = service.createCardDtoListForAssay(assay)

        then:
        assert cardDtos.size() == 2

        CardDto card1 = cardDtos.get(0)
        assert card1.title == card1Label
        assert card1.lines.size() == 1
        CardLineDto line1 = card1.lines.get(0)
        assert line1.attributeLabel == card1Label
        assert line1.valueLabel == card1Value
        assert line1.attributeDefinition == null
        assert !line1.attributeDefinitionAvailable
        assert line1.valueDefinition == null
        assert !line1.valueDefinitionAvailable

        CardDto card2 = cardDtos.get(1)
        assert card2.title == item2Label
        assert card2.lines.size() == 2
        CardLineDto line2_1 = card2.lines.get(0)
        assert line2_1.attributeLabel == childLabel
        assert line2_1.valueLabel == childValue
        assert line2_1.attributeDefinition == childLabelDef
        assert line2_1.attributeDefinitionAvailable
        assert line2_1.valueDefinition == childValueDef
        assert line2_1.valueDefinitionAvailable
        CardLineDto line2_2 = card2.lines.get(1)
        assert line2_2.attributeLabel == item2Label
        assert line2_2.valueLabel == item2Value
        assert line2_2.attributeDefinition == null
        assert !line2_2.attributeDefinitionAvailable
        assert line2_2.valueDefinition == null
        assert !line2_2.valueDefinitionAvailable

    }

}
