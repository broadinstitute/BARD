package bard.db.registration

import bard.db.dictionary.Element
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Unit tests for the CardFactoryService.
 */
@TestFor(CardFactoryService)
@Build([Assay, AssayContext, AssayContextItem, Element])
class CardFactoryServiceUnitSpec extends Specification {

    void setup() {

    }

    void "test createCardDtoListForAssay with null Assay"() {
        given:
        Assay assay = null

        when:
        Map<String, CardDto> cardDtoMap = service.createCardDtoMapForAssay(assay)

        then:
        assert cardDtoMap.size() == 0
    }

    void "test createCardDtoListForAssay with Assay with no AssayContextItems"() {
        given:
        Assay assay = new Assay()

        when:
        Map<String, CardDto> cardDtoMap = service.createCardDtoMapForAssay(assay)

        then:
        assert cardDtoMap.size() == 0
    }

    void "test createCardDtoListForAssay with Assay having 1 AssayContextItem"() {
        String cardTitle = 'cardTitle'
        String card1Label = "Label 1"
        String card1Value = "Value 1"
        given:
        Assay assay = Assay.build()
        AssayContext assayContext = AssayContext.build(contextName: cardTitle)
        assay.addToAssayContexts(assayContext)
//        assayContext.addToAssayContextItems(AssayContextItem.build(attributeElement: Element.build(label: card1Label), valueElement: Element.build(label: card1Value)))
        assayContext.addToAssayContextItems(new AssayContextItem(attributeElement: new Element(label: card1Label), valueDisplay: card1Value))

        when:
        Map<String, CardDto> cardDtoMap = service.createCardDtoMapForAssay(assay)
        println(cardDtoMap)
        assert cardDtoMap.size() == 1
        CardDto card1 = cardDtoMap.values().flatten().first()

        then:
        cardTitle == card1.title
        card1.lines.size() == 1

        CardLineDto line1 = card1.lines.get(0)
        card1Label == line1.attributeLabel
        card1Value == line1.valueLabel
        null == line1.attributeDefinition
        false == line1.attributeDefinitionAvailable

        null == line1.valueDefinition
        false == line1.valueDefinitionAvailable
    }

//    void "test createCardDtoMapForAssay with Assay having an assay-level AssayContextItem and a result-specific AssayContextItem"() {
//        given:
//        Assay assay = new Assay()
//        assay.assayContextItems = new HashSet<AssayContextItem>()
//        String card1Label = "Label 1"
//        String card1Value = "Value 1"
//        AssayContextItem item1 = new AssayContextItem()
//        item1.parentGroup = null
//        item1.attributeElement = new Element(label: card1Label)
//        item1.valueDisplay = card1Value
//        assay.assayContextItems.add(item1)
//        AssayContextItem item2 = new AssayContextItem(assayContext: new AssayContext())
//        assay.assayContextItems.add(item2)
//
//        when:
//        List<CardDto> cardDtos = service.createCardDtoMapForAssay(assay)
//
//        then:
//        assert cardDtos.size() == 1
//        CardDto card1 = cardDtos.get(0)
//        assert card1.title == card1Label
//        assert card1.lines.size() == 1
//        CardLineDto line1 = card1.lines.get(0)
//        assert line1.attributeLabel == card1Label
//        assert line1.valueLabel == card1Value
//        assert line1.attributeDefinition == null
//        assert !line1.attributeDefinitionAvailable
//        assert line1.valueDefinition == null
//        assert !line1.valueDefinitionAvailable
//    }
//
//    void "test createCardDtoMapForAssay with Assay having 2 AssayContextItems one with child"() {
//        given:
//        Assay assay = new Assay()
//        assay.assayContextItems = new HashSet<AssayContextItem>()
//        String card1Label = "Label 1"
//        String card1Value = "Value 1"
//        AssayContextItem item1 = new AssayContextItem()
//        item1.id = 1
//        item1.parentGroup = item1
//        item1.attributeElement = new Element(label: card1Label)
//        item1.valueDisplay = card1Value
//        assay.assayContextItems.add(item1)
//
//        AssayContextItem item2 = new AssayContextItem()
//        String item2Label = "Label 2"
//        String item2Value = "Value 2"
//        item2.id = 2
//        item2.parentGroup = item2
//        item2.attributeElement = new Element(label: item2Label)
//        item2.valueDisplay = item2Value
//        assay.assayContextItems.add(item2)
//
//        AssayContextItem child = new AssayContextItem()
//        String childLabel = "Child Label"
//        String childValue = "Child Value"
//        String childLabelDef = "Child Label Def"
//        String childValueDef = "Child Value Def"
//        child.attributeElement = new Element(label: childLabel, description: childLabelDef)
//        child.valueDisplay = childValue
//        child.valueElement = new Element(label: childValue, description: childValueDef)
//        item2.addToChildren(child)
//        assay.assayContextItems.add(child)
//
//        when:
//        List<CardDto> cardDtos = service.createCardDtoMapForAssay(assay)
//
//        then:
//        assert cardDtos.size() == 2
//
//        CardDto card1 = cardDtos.get(0)
//        assert card1.title == card1Label
//        assert card1.lines.size() == 1
//        CardLineDto line1 = card1.lines.get(0)
//        assert line1.attributeLabel == card1Label
//        assert line1.valueLabel == card1Value
//        assert line1.attributeDefinition == null
//        assert !line1.attributeDefinitionAvailable
//        assert line1.valueDefinition == null
//        assert !line1.valueDefinitionAvailable
//
//        CardDto card2 = cardDtos.get(1)
//        assert card2.title == item2Label
//        assert card2.lines.size() == 2
//        CardLineDto line2_1 = card2.lines.get(0)
//        assert line2_1.attributeLabel == childLabel
//        assert line2_1.valueLabel == childValue
//        assert line2_1.attributeDefinition == childLabelDef
//        assert line2_1.attributeDefinitionAvailable
//        assert line2_1.valueDefinition == childValueDef
//        assert line2_1.valueDefinitionAvailable
//        CardLineDto line2_2 = card2.lines.get(1)
//        assert line2_2.attributeLabel == item2Label
//        assert line2_2.valueLabel == item2Value
//        assert line2_2.attributeDefinition == null
//        assert !line2_2.attributeDefinitionAvailable
//        assert line2_2.valueDefinition == null
//        assert !line2_2.valueDefinitionAvailable
//
//    }

}
