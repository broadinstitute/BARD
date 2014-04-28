/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1, 1)
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName2, label2, valueDisplay2, contextItemId2, 1)
        List<ContextItemDTO> contextItemDTOs = [contextItemDTO1, contextItemDTO2]

        when:
        SortedMap<String, List<ContextItemDTO>> m = ContextItemDTO.buildCardMap(contextItemDTOs)
        then:
        assert m.size() == 1
        assert m.get(contextItemDTO1.contextDTO) == m.get(contextItemDTO2.contextDTO)
    }

    void "test toContextItemDTOs"() {
        given:
        AssayContextItem assayContextItem = AssayContextItem.build()
        when:
        List<ContextItemDTO> contextItemDTOs = ContextItemDTO.toContextItemDTOs([assayContextItem])
        then:
        assert contextItemDTOs.size() == 1
        ContextItemDTO contextItemDTO = contextItemDTOs.get(0)
        assert contextItemDTO.contextDTO.contextName == assayContextItem.assayContext.preferredName
        assert contextItemDTO.contextDTO.contextId == assayContextItem.assayContext.id
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
        assert contextItemDTO.contextDTO.contextName == assayContextItem.assayContext.preferredName
        assert contextItemDTO.contextDTO.contextId == assayContextItem.assayContext.id
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
        final Long contextId = 1
        when:
        ContextItemDTO contextItemDTO = new ContextItemDTO(contextName, label, valueDisplay, contextItemId, contextId)
        then:
        assert contextItemDTO.contextDTO.contextName == contextName
        assert contextItemDTO.contextDTO.contextId == contextId
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
        final Long contextId1 = 1
        when:
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1, contextId1)
        and:
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName, label, valueDisplay, contextItemId, contextId)
        then:
        assert contextItemDTO1.hashCode() != contextItemDTO2.hashCode()

        where:
        desc                                    | contextName | label    | valueDisplay | contextItemId | compareValue | contextId
        "Not Equal, contextName is different"   | "CName1"    | "Label"  | "display"    | 1             | -1           | 1
        "Not Equal, value display is different" | "CName"     | "Label"  | "display1"   | 1             | -1           | 1
        "Not Equal, Label is different"         | "CName"     | "Label1" | "display"    | 1             | -1           | 1
    }

    void "test hashCode equals #desc"() {
        given:
        final String contextName1 = "CName"
        final String label1 = "Label"
        final String valueDisplay1 = "display"
        final Long contextItemId1 = 2
        final Long contextId1 = 2
        when:
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1, contextId1)
        and:
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName, label, valueDisplay, contextItemId, contextId)
        then:
        assert contextItemDTO1.hashCode() == contextItemDTO2.hashCode()

        where:
        desc                                | contextName | label   | valueDisplay | contextItemId | compareValue | contextId
        "Equal, exact args"                 | "CName"     | "Label" | "display"    | 2             | 0            | 1
        "Equal, contextItemId is different" | "CName"     | "Label" | "display"    | 1             | 0            | 1
    }


    void "test compareTo #desc"() {
        given:
        final String contextName1 = "CName"
        final String label1 = "Label"
        final String valueDisplay1 = "display"
        final Long contextItemId1 = 2
        final Long contextId1 = 2
        when:
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1, contextId1)
        and:
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName, label, valueDisplay, contextItemId, contextId)
        then:
        assert contextItemDTO1.compareTo(contextItemDTO2) == compareValue

        where:
        desc                                    | contextName | label    | valueDisplay | contextItemId | compareValue | contextId
        "Equal, exact args"                     | "CName"     | "Label"  | "display"    | 2             | 0            | 2
        "Equal, contextItemId is different"     | "CName"     | "Label"  | "display"    | 1             | 0            | 1
        "Not Equal, contextName is different"   | "CName1"    | "Label"  | "display"    | 1             | -1           | 1
        "Not Equal, value display is different" | "CName"     | "Label"  | "display1"   | 1             | -1           | 1
        "Not Equal, Label is different"         | "CName"     | "Label1" | "display"    | 1             | -1           | 1
    }

    void "test equals #desc"() {
        given:
        final String contextName1 = "CName"
        final String label1 = "Label"
        final String valueDisplay1 = "display"
        final Long contextItemId1 = 2
        final Long contextId1 = 2
        when:
        ContextItemDTO contextItemDTO1 = new ContextItemDTO(contextName1, label1, valueDisplay1, contextItemId1, contextId1)
        and:
        ContextItemDTO contextItemDTO2 = new ContextItemDTO(contextName, label, valueDisplay, contextItemId, contextId)
        then:
        assert contextItemDTO1.equals(contextItemDTO2) == isEquals

        where:
        desc                                    | contextName | label    | valueDisplay | contextItemId | isEquals | contextId
        "Equal, exact args"                     | "CName"     | "Label"  | "display"    | 2             | true     | 2
        "Equal, contextItemId is different"     | "CName"     | "Label"  | "display"    | 1             | true     | 1
        "Not Equal, contextName is different"   | "CName1"    | "Label"  | "display"    | 1             | false    | 1
        "Not Equal, value display is different" | "CName"     | "Label"  | "display1"   | 1             | false    | 1
        "Not Equal, Label is different"         | "CName"     | "Label1" | "display"    | 1             | false    | 1
    }
}
