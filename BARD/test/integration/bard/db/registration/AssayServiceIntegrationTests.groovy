package bard.db.registration

import bard.db.dictionary.AssayDescriptor
import bard.db.dictionary.BiologyDescriptor
import bard.db.dictionary.InstanceDescriptor
import bard.db.dictionary.ResultType

/**
 * Created with IntelliJ IDEA.
 * User: sbrudz
 * Date: 5/4/12
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayServiceIntegrationTests extends GroovyTestCase {

    AssayService assayService

    void testGetMeasureContextItemsForAssay() {

        AssayDescriptor assayDescriptor = AssayDescriptor.findByLabel('nucleotide')
        AssayDescriptor assayDescriptor2 = AssayDescriptor.findByLabel('assay kit')
        BiologyDescriptor biologyDescriptor = BiologyDescriptor.findByLabel('molecular target')
        InstanceDescriptor instanceDescriptor = InstanceDescriptor.findByLabel('small-molecule perturbagen')
        ResultType resultType = ResultType.findByResultTypeName('IC50')

        Assay assay = new Assay(assayName: "Test assay service", assayVersion: 1, assayStatus: AssayStatus.get(1), description: "Test").save()
        assert assay.validate()

        MeasureContextItem assayItem = new MeasureContextItem(attributeElement: assayDescriptor.element, attributeType: AttributeType.NUMBER)
        MeasureContextItem assayKitItem = new MeasureContextItem(attributeElement: assayDescriptor2.element, attributeType: AttributeType.NUMBER)
        MeasureContextItem biologyItem = new MeasureContextItem(attributeElement: biologyDescriptor.element, attributeType: AttributeType.NUMBER)
        MeasureContextItem instanceItem = new MeasureContextItem(attributeElement: instanceDescriptor.element, attributeType: AttributeType.NUMBER)

        assay.addToMeasureContextItems(assayItem)
        assay.addToMeasureContextItems(assayKitItem)
        assay.addToMeasureContextItems(biologyItem)
        assay.addToMeasureContextItems(instanceItem)

        assert assayItem.validate()
        assert assayKitItem.validate()
        assert biologyItem.validate()
        assert instanceItem.validate()

        String contextName = "Context for IC50"
        MeasureContext context = new MeasureContext(contextName: contextName)
        assay.addToMeasureContexts(context)
        assert context.validate()

        Measure measure = new Measure(resultType: resultType)
        context.addToMeasures(measure)
        assert measure.validate()

        Map map = assayService.getMeasureContextItemsForAssay(assay)

        assert map.'ASSAY_DESCRIPTOR'.'assay component'.'nucleotide' == assayItem
        assert map.'ASSAY_DESCRIPTOR'.'assay component'.'assay kit' == assayKitItem
        assert map.'BIOLOGY_DESCRIPTOR'.'molecular target' == biologyItem
        assert map.'INSTANCE_DESCRIPTOR'.'assay instance'.'perturbagen collection'.'small-molecule collection'.'small-molecule perturbagen' == instanceItem
        assert map.'Result Types'[contextName]
        assert map.'Result Types'[contextName].get(0) == 'IC50'

    }
}
