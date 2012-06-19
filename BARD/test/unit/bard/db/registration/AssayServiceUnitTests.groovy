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

        Element element = new Element(label: "Test", elementStatus: "Published", readyForExtraction: "Ready")
        element.setId(1)
        element.save()
        assert element.validate()
        AssayDescriptor assayDescriptor = new AssayDescriptor(label: "assay mode", element: element, elementStatus: "Published")
        assayDescriptor.setId(8)
        assayDescriptor.save()
        assert assayDescriptor.validate()
        assert AssayDescriptor.count() == 1
        MeasureContextItem item1 = new MeasureContextItem(attributeElement: element)

        Assay assay = new Assay()
        assay.addToMeasureContextItems(item1)

        Map map = service.getMeasureContextItemsForAssay(assay)

        assert map["Assay Context"]
        assert map["Assay Context"].contains(item1)

    }
}
