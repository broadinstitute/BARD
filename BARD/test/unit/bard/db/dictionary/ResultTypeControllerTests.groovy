package bard.db.dictionary

import grails.test.mixin.TestFor
import grails.test.mixin.Mock

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ResultTypeController)
@Mock([ResultType, Element, ElementHierarchy])
class ResultTypeControllerTests {

    void testList() {

        Element root = new Element(label: "Root", elementStatus: "Published")
        root.setId(1)
        root.save()
        Element child = new Element(label: "Child", elementStatus: "Published")
        child.setId(2)
        child.save()
        ElementHierarchy link = new ElementHierarchy(parentElement: root, childElement: child, relationshipType: "has a")
        link.setId(1)
        link.save()

        assert root.validate()
        assert child.validate()

        ResultType rootResultType = new ResultType(resultTypeName: "Root", resultTypeStatus: "Published", element: root, parent: null)
        rootResultType.setId(4)
        rootResultType.save()
        ResultType childResultType = new ResultType(resultTypeName: "Child", resultTypeStatus: "Published", element: child)
        childResultType.setId(6)
        childResultType.save()

        rootResultType.addToChildren(childResultType)

        assert rootResultType.validate()
        assert childResultType.validate()

        assert Element.count() == 2
        assert ResultType.count() == 2
        assert ResultType.findByParentIsNull()


        controller.list()

        assert response.text == "{\"id\":4,\"text\":\"Root\",\"children\":[{\"id\":6,\"text\":\"Child\",\"leaf\":true}]}"
    }
}
