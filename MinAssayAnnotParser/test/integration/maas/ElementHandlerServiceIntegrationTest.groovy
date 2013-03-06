package maas

import bard.db.dictionary.Element

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/2/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
class ElementHandlerServiceIntegrationTest extends GroovyTestCase {

    def elementHandlerService
    public void testAddElements() {
        def elements = ["asb":"abcded", "abcd":"ttt"]
        def elementParent = ["asb":555, "abcd":555]
        elementHandlerService.addMissingElement("xiaorong", elements, elementParent)
        Element element = Element.findByLabel("asb")
        assert element.description == "abcded"
        assert element.modifiedBy == "xiaorong"
    }

    public void testLoadElements() {
        elementHandlerService.addMissingElement("xiaorong", ElementHandlerService.elementAndDescription, ElementHandlerService.elementParent)
    }
}
