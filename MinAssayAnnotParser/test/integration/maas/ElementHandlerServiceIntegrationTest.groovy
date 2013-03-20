package maas

import bard.db.dictionary.Element
import org.junit.Ignore

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
        Map elementAndDescription = [
                'science officer' : ''
        ]

// element parent
        Map elementParent = [
                'science officer' : 555  // id = 555   'project information'
        ]
        elementHandlerService.addMissingElement("xiaorong", elementAndDescription, elementParent)
    }

    @Ignore
    public void testAddMissingName() {
        String fileWithUniqueName = 'test/exampleData/maas/missingNameUniq.txt'
        elementHandlerService.addMissingName(fileWithUniqueName)
    }

    public void testBuild() {
       def elementDescription = [:]
        def elementParent = [:]
       ElementHandlerService.build(null, elementDescription, elementParent)
        println(elementDescription)
        println(elementParent)
    }
}
