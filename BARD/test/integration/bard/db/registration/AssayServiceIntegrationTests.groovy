package bard.db.registration

import bard.db.dictionary.*

/**
 * Created with IntelliJ IDEA.
 * User: sbrudz
 * Date: 5/4/12
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayServiceIntegrationTests extends GroovyTestCase {

    AssayService assayService

    void testGetContextForAssay() {

    }

    void testGetMeasureContextItemsForAssay() {

        AssayDescriptor assayDescriptor = AssayDescriptor.findByLabel('nucleotide')
        AssayDescriptor assayDescriptor2 = AssayDescriptor.findByLabel('assay kit')
        BiologyDescriptor biologyDescriptor = BiologyDescriptor.findByLabel('molecular target')
        InstanceDescriptor instanceDescriptor = InstanceDescriptor.findByLabel('small-molecule perturbagen')
        ResultType resultType = ResultType.findByResultTypeName('IC50')
        Element negControl = Element.findByLabel('negative control')

        Assay assay = new Assay(assayName: "Test assay service", assayVersion: 1, assayStatus: AssayStatus.get(1), description: "Test").save()
        assert assay.validate()

        MeasureContextItem assayItem = new MeasureContextItem(attributeElement: assayDescriptor.element, attributeType: AttributeType.Number)
        MeasureContextItem assayKitItem = new MeasureContextItem(attributeElement: assayDescriptor2.element, attributeType: AttributeType.Number)
        MeasureContextItem negControlItem = new MeasureContextItem(attributeElement: negControl, attributeType: AttributeType.Number)
        MeasureContextItem biologyItem = new MeasureContextItem(attributeElement: biologyDescriptor.element, attributeType: AttributeType.Number)
        MeasureContextItem instanceItem = new MeasureContextItem(attributeElement: instanceDescriptor.element, attributeType: AttributeType.Number)

        assay.addToMeasureContextItems(assayItem)
        assay.addToMeasureContextItems(assayKitItem)
        assay.addToMeasureContextItems(negControlItem)
        assay.addToMeasureContextItems(biologyItem)
        assay.addToMeasureContextItems(instanceItem)

        assayItem.addToChildren(negControlItem)

        assayItem.save()
        assayKitItem.save()
        negControlItem.save()
        biologyItem.save()
        instanceItem.save()

        assert assayItem.validate()
        assert negControlItem.validate()
        assert assayKitItem.validate()
        assert biologyItem.validate()
        assert instanceItem.validate()

        String contextName = "Context for IC50"
        MeasureContext context = new MeasureContext(contextName: contextName)
        assay.addToMeasureContexts(context)
        context.save()
        assert context.validate()

        Measure measure = new Measure(resultType: resultType)
        assay.addToMeasures(measure)
        context.addToMeasures(measure)
        assert measure.validate()

        Map map = assayService.getMeasureContextItemsForAssay(assay)

        assert map.'Assay Context'.contains(assayItem)
        assert map.'Assay Context'.contains(negControlItem)
        assert map.'Assay Context'.contains(assayKitItem)
        assert map.'Assay Context'.contains(biologyItem)
        assert map.'Assay Context'.contains(instanceItem)
        assert map.'Result Context'[contextName]

    }
}
