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
@Mock([AssayContextItem, Assay, AssayContext, Measure, ResultType, Element, AssayDescriptor])
class AssayServiceUnitTests {

    void testGetAssayContextItemsForAssay() {

        Element element = new Element(label: "Test", elementStatus: "Published", readyForExtraction: "Ready")
        element.setId(1)
        element.save()
        assert element.validate()
        AssayDescriptor assayDescriptor = new AssayDescriptor(label: "assay mode", element: element, elementStatus: "Published")
        assayDescriptor.setId(8)
        assayDescriptor.save()
        assert assayDescriptor.validate()
        assert AssayDescriptor.count() == 1
        AssayContextItem item1 = new AssayContextItem(attributeElement: element)

        Assay assay = new Assay()
        assay.addToAssayContextItems(item1)

        Map map = service.getAssayContextItemsForAssay(assay)

        assert map["Assay Context"]
        assert map["Assay Context"].contains(item1)

    }
}
