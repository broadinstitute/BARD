package bard.db.registration

import bard.db.dictionary.Element
import bard.db.dictionary.ResultType
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import bard.db.dictionary.AssayDescriptor

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(AssayService)
@Mock([MeasureContextItem, Assay, MeasureContext, Measure, ResultType, Element, AssayDescriptor])
class AssayServiceUnitTests {

    void testGetMeasureContextItemsForAssay() {

        Element element = new Element()
        AssayDescriptor assayDescriptor = new AssayDescriptor(id: 3, label: "assay mode", element: element, "Pending": elementStatus).save()
        assert assayDescriptor.validate()
        assert AssayDescriptor.count() > 0
        MeasureContextItem item1 = new MeasureContextItem(attributeElement: element)

        Assay assay = new Assay()
        assay.addToMeasureContextItems(item1)

        Map map = service.getMeasureContextItemsForAssay(assay)

        assert map.assayDescriptors
        assert map.assayDescriptors.contains(item1)

    }
}
