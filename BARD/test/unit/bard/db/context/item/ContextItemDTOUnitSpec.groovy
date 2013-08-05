package bard.db.context.item

import bard.db.registration.AssayContextItem
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

@Build([AssayContextItem])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class ContextItemDTOUnitSpec extends Specification {


    void "test buildCardMap"() {
        given:
        final String contextName1 = "CName"
        final String label1 = "Label"
        final String valueDisplay1 = "display"
        final Long contextItemId1 = 2


        final String contextName2 = "CName"
        final String label2 = "Label2"
        final String valueDisplay2 = "display2"
        final Long contextItemId2 = 2
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1)
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName2, label2, valueDisplay2, contextItemId2)
        List<ContextItemDTO> contextItemDTOs = [contextItemDTO1, contextItemDTO2]

        when:
        SortedMap<String, List<ContextItemDTO>> m = ContextItemDTO.buildCardMap(contextItemDTOs)
        then:
        assert m.size() == 1
        assert m.get(contextName1) == m.get(contextName2)
    }

    void "test toContextItemDTOs"() {
        given:
        AssayContextItem assayContextItem = AssayContextItem.build()
        when:
        List<ContextItemDTO> contextItemDTOs = ContextItemDTO.toContextItemDTOs([assayContextItem])
        then:
        assert contextItemDTOs.size() == 1
        ContextItemDTO contextItemDTO = contextItemDTOs.get(0)
        assert contextItemDTO.contextName == assayContextItem.assayContext.preferredName
        assert contextItemDTO.label == assayContextItem.attributeElement.label
        assert contextItemDTO.valueDisplay == assayContextItem.valueDisplay
        assert contextItemDTO.contextItemId == assayContextItem.id

    }

    void "test constructor with assay items"() {
        given:
        AssayContextItem assayContextItem = AssayContextItem.build()
        when:
        ContextItemDTO contextItemDTO = new ContextItemDTO(assayContextItem)
        then:
        println contextItemDTO
        assert contextItemDTO.contextName == assayContextItem.assayContext.preferredName
        assert contextItemDTO.label == assayContextItem.attributeElement.label
        assert contextItemDTO.valueDisplay == assayContextItem.valueDisplay
        assert contextItemDTO.contextItemId == assayContextItem.id

    }

    void "test constructor with multi args"() {
        given:
        final String contextName = "CName"
        final String label = "Label"
        final String valueDisplay = "display"
        final Long contextItemId = 2
        when:
        ContextItemDTO contextItemDTO = new ContextItemDTO(contextName, label, valueDisplay, contextItemId)
        then:
        assert contextItemDTO.contextName == contextName
        assert contextItemDTO.label == label
        assert contextItemDTO.valueDisplay == valueDisplay
        assert contextItemDTO.contextItemId == contextItemId

    }

    void "test hashCode not equals #desc"() {
        given:
        final String contextName1 = "CName"
        final String label1 = "Label"
        final String valueDisplay1 = "display"
        final Long contextItemId1 = 2
        when:
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1)
        and:
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName, label, valueDisplay, contextItemId)
        then:
        assert contextItemDTO1.hashCode() != contextItemDTO2.hashCode()

        where:
        desc                                    | contextName | label    | valueDisplay | contextItemId | compareValue
        "Not Equal, contextName is different"   | "CName1"    | "Label"  | "display"    | 1             | -1
        "Not Equal, value display is different" | "CName"     | "Label"  | "display1"   | 1             | -1
        "Not Equal, Label is different"         | "CName"     | "Label1" | "display"    | 1             | -1
    }

    void "test hashCode equals #desc"() {
        given:
        final String contextName1 = "CName"
        final String label1 = "Label"
        final String valueDisplay1 = "display"
        final Long contextItemId1 = 2
        when:
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1)
        and:
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName, label, valueDisplay, contextItemId)
        then:
        assert contextItemDTO1.hashCode() == contextItemDTO2.hashCode()

        where:
        desc                                | contextName | label   | valueDisplay | contextItemId | compareValue
        "Equal, exact args"                 | "CName"     | "Label" | "display"    | 2             | 0
        "Equal, contextItemId is different" | "CName"     | "Label" | "display"    | 1             | 0
    }

    void "test compareTo #desc"() {
        given:
        final String contextName1 = "CName"
        final String label1 = "Label"
        final String valueDisplay1 = "display"
        final Long contextItemId1 = 2
        when:
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1)
        and:
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName, label, valueDisplay, contextItemId)
        then:
        assert contextItemDTO1.compareTo(contextItemDTO2) == compareValue

        where:
        desc                                    | contextName | label    | valueDisplay | contextItemId | compareValue
        "Equal, exact args"                     | "CName"     | "Label"  | "display"    | 2             | 0
        "Equal, contextItemId is different"     | "CName"     | "Label"  | "display"    | 1             | 0
        "Not Equal, contextName is different"   | "CName1"    | "Label"  | "display"    | 1             | -1
        "Not Equal, value display is different" | "CName"     | "Label"  | "display1"   | 1             | -1
        "Not Equal, Label is different"         | "CName"     | "Label1" | "display"    | 1             | -1
    }

    void "test equals #desc"() {
        given:
        final String contextName1 = "CName"
        final String label1 = "Label"
        final String valueDisplay1 = "display"
        final Long contextItemId1 = 2
        when:
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1)
        and:
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName, label, valueDisplay, contextItemId)
        then:
        assert contextItemDTO1.equals(contextItemDTO2) == isEquals

        where:
        desc                                    | contextName | label    | valueDisplay | contextItemId | isEquals
        "Equal, exact args"                     | "CName"     | "Label"  | "display"    | 2             | true
        "Equal, contextItemId is different"     | "CName"     | "Label"  | "display"    | 1             | true
        "Not Equal, contextName is different"   | "CName1"    | "Label"  | "display"    | 1             | false
        "Not Equal, value display is different" | "CName"     | "Label"  | "display1"   | 1             | false
        "Not Equal, Label is different"         | "CName"     | "Label1" | "display"    | 1             | false
    }
}