package bard.db.dictionary

import grails.test.mixin.TestFor
import grails.test.mixin.Mock

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ResultTypeController)
@Mock([ResultType, Element, ElementStatus, ElementHierarchy])
class ResultTypeControllerTests {

    void testList() {
        ElementStatus status = new ElementStatus(elementStatus: "Approved").save()

        Element root = new Element(label: "Root", elementStatus: status).save()
        Element child = new Element(label: "Child", elementStatus: status).save()
        ElementHierarchy link = new ElementHierarchy(parentElement: root, childElement: child, relationshipType: "has a").save()

        assert root.validate()
        assert child.validate()

        ResultType rootResultType = new ResultType(id: 4, resultTypeName: "Root", resultTypeStatus: status, element: root, parent: null).save()
        ResultType childResultType = new ResultType(id: 6, resultTypeName: "Child", resultTypeStatus: status, element: child).save()

        rootResultType.addToChildren(childResultType)

        assert rootResultType.validate()
        assert childResultType.validate()

        assert Element.count() == 2
        assert ResultType.count() == 2
        assert ResultType.findByParentIsNull


        controller.list()

        assert response.text == ""
    }
}
