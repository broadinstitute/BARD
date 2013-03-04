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
        elementHandlerService.addMissingElement("xiaorong", elements)
        Element element = Element.findByLabel("asb")
        assert element.description == "abcded"
        assert element.modifiedBy == "xiaorong"
    }
}
