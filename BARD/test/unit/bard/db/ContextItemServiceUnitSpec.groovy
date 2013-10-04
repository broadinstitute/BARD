package bard.db

import bard.db.context.item.BasicContextItemCommand
import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import bard.db.enums.ValueType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentMeasure
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AssayContextMeasure
import bard.db.registration.Measure
import bard.db.registration.MergeAssayDefinitionService
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
}
