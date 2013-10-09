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
@Build([Assay, AssayContext,  AssayContextItem, Element])
@Mock([Assay, AssayContext,  AssayContextItem, Element])
@TestMixin(ServiceUnitTestMixin)
@TestFor(ContextItemService)
@Unroll
class ContextItemServiceUnitSpec extends Specification {
    def "test create external ontology context item"() {
        ContextItemService service = new ContextItemService()

        Element attribute = Element.build(expectedValueType: ExpectedValueType.EXTERNAL_ONTOLOGY)
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
}
