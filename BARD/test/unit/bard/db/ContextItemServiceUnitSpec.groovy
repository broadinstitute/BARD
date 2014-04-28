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

package bard.db

import bard.db.context.item.BasicContextItemCommand
import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import bard.db.enums.ValueType
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 10/3/13
 * Time: 8:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext, AssayContextItem, Element])
@Mock([Assay, AssayContext, AssayContextItem, Element])
@TestMixin(ServiceUnitTestMixin)
@TestFor(ContextItemService)
@Unroll
class ContextItemServiceUnitSpec extends Specification {
    def "test create external ontology context item"() {
        ContextItemService service = new ContextItemService()

        Element attribute = Element.build(expectedValueType: ExpectedValueType.EXTERNAL_ONTOLOGY, externalURL: "externalUrl")
        AssayContext context = AssayContext.build()
        BasicContextItemCommand command = new BasicContextItemCommand()
        command.context = context
        command.contextClass = "AssayContext"
        command.attributeElementId = attribute.id
        command.extValueId = "x"
        command.valueDisplay = "y"

        when:
        boolean created = service.createAssayContextItem(context.id, command)

        then:
        created
        AssayContextItem item = context.contextItems.first()
        item.valueType == ValueType.EXTERNAL_ONTOLOGY
        item.valueDisplay == "y"
        item.extValueId == "x"
    }

    def "test create free text context item"() {
        ContextItemService service = new ContextItemService()

        Element attribute = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        AssayContext context = AssayContext.build()
        BasicContextItemCommand command = new BasicContextItemCommand()
        command.context = context
        command.contextClass = "AssayContext"
        command.attributeElementId = attribute.id
        command.valueDisplay = "y"

        when:
        boolean created = service.createAssayContextItem(context.id, command)

        then:
        created
        AssayContextItem item = context.contextItems.first()
        item.valueType == ValueType.FREE_TEXT
        item.valueDisplay == "y"
    }

    def "test create value element context item"() {
        ContextItemService service = new ContextItemService()

        Element attribute = Element.build(expectedValueType: ExpectedValueType.ELEMENT)
        Element value = Element.build(expectedValueType: ExpectedValueType.NONE)
        AssayContext context = AssayContext.build()
        BasicContextItemCommand command = new BasicContextItemCommand()
        command.context = context
        command.contextClass = "AssayContext"
        command.attributeElementId = attribute.id
        command.valueElementId = value.id
        command.valueDisplay = "y"

        when:
        boolean created = service.createAssayContextItem(context.id, command)

        then:
        created
        AssayContextItem item = context.contextItems.first()
        item.valueType == ValueType.ELEMENT
        item.valueElement == value
    }

    def "test create numeric context item"() {
        ContextItemService service = new ContextItemService()

        Element attribute = Element.build(expectedValueType: ExpectedValueType.NUMERIC)
        Element value = Element.build(expectedValueType: ExpectedValueType.NONE)
        AssayContext context = AssayContext.build()
        BasicContextItemCommand command = new BasicContextItemCommand()
        command.context = context
        command.contextClass = "AssayContext"
        command.attributeElementId = attribute.id
        command.valueNum = "100.0"

        when:
        boolean created = service.createAssayContextItem(context.id, command)

        then:
        created
        AssayContextItem item = context.contextItems.first()
        item.valueType == ValueType.NUMERIC
        item.valueNum == 100.0

    }

    def "test create free context item"() {
        ContextItemService service = new ContextItemService()

        Element attribute = Element.build(expectedValueType: ExpectedValueType.ELEMENT)
        AssayContext context = AssayContext.build()
        BasicContextItemCommand command = new BasicContextItemCommand()
        command.context = context
        command.contextClass = "AssayContext"
        command.attributeElementId = attribute.id
        command.valueConstraintType = "Free"
        command.providedWithResults = true

        when:
        boolean created = service.createAssayContextItem(context.id, command)

        then:
        created
        AssayContextItem item = context.contextItems.first()
        item.valueType == ValueType.NONE
        item.attributeType == AttributeType.Free

    }

    def "test create range context item #label"() {
        ContextItemService service = new ContextItemService()

        Element attribute = Element.build(expectedValueType: ExpectedValueType.NUMERIC)
        Element value = Element.build(expectedValueType: ExpectedValueType.NONE)
        AssayContext context = AssayContext.build()
        BasicContextItemCommand command = new BasicContextItemCommand()
        command.context = context
        command.contextClass = "AssayContext"
        command.attributeElementId = attribute.id
        command.valueNum = null
        command.valueMin = valueMin
        command.valueMax = valueMax

        when:
        boolean created = service.createAssayContextItem(context.id, command)

        then:
        assert created == createdSuccessfully
        if (createdSuccessfully) {
            AssayContextItem item = context.contextItems.first()
            assert item.valueType == ValueType.RANGE
            assert item.valueMin == expectedValMin
            assert item.valueMax == expectedValMax
        }

        where:
        label                                        | valueMin  | valueMax  | expectedValMin | expectedValMax | createdSuccessfully
        "with valid range fields"                    | 1.0       | 100.0     | 1.0            | 100.0          | true
        "with invalid range's valueMin"              | "invalid" | 100.0     | "N/A"          | "N/A"          | false
        "with invalid range's valueMax"              | 1.0       | "invalid" | "N/A"          | "N/A"          | false
        "with invalid range's valueMin and valueMax" | "invalid" | "invalid" | "N/A"          | "N/A"          | false
    }

    def "test create numeric context item #label"() {
        ContextItemService service = new ContextItemService()

        Element attribute = Element.build(expectedValueType: ExpectedValueType.NUMERIC)
        Element value = Element.build(expectedValueType: ExpectedValueType.NONE)
        AssayContext context = AssayContext.build()
        BasicContextItemCommand command = new BasicContextItemCommand()
        command.context = context
        command.contextClass = "AssayContext"
        command.attributeElementId = attribute.id
        command.valueNum = valueNum

        when:
        boolean created = service.createAssayContextItem(context.id, command)

        then:
        assert created == createdSuccessfully
        if (createdSuccessfully) {
            AssayContextItem item = context.contextItems.first()
            assert item.valueType == ValueType.NUMERIC
        }

        where:
        label                             | valueNum  | expectedValNum | createdSuccessfully
        "with valid number field"         | 1.0       | 1.0            | true
        "with invalid numeric's valueNum" | "invalid" | "N/A"          | false
    }
}
