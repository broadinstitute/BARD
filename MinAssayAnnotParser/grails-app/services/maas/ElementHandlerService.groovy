package maas

import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy

class ElementHandlerService {

    public static Map elementAndDescription = [
            'science officer' : ''
    ]

    // element parent
    public static Map elementParent = [
            'science officer' : 555  // id = 555   'project information'
    ]

    /**
     * Add missing elements and relationship
     * @param modifiedBy
     * @param elementAndDescription
     * @return
     */
    def addMissingElement(String modifiedBy, Map elementAndDescription, Map elementAndParent) {
        elementAndDescription.each() {key, value->
            def element = Element.findAllByLabel(key)
            if (!element) {
                element = new Element(label: key, modifiedBy: modifiedBy, description: value)
                element.save(flush: true)
            }
            if (elementAndParent[key]) {
                def parent = Element.findById(elementAndParent[key])
                addElementRelationship(parent, element, modifiedBy)
            }
        }
    }

    /**
     * Add element relationship if both parent and element are not null, and there is no relationship exists
     * @param parent
     * @param element
     * @return
     */
    def addElementRelationship(Element parent, Element element, String modifiedBy) {
        if (parent && element) {
            def relationship = ElementHierarchy.findByParentElementAndChildElement(parent, element)
            if (!relationship) {
                def elementHierarchy = new ElementHierarchy(parentElement: parent, childElement: element, modifiedBy: modifiedBy)
                elementHierarchy.save(flush: true)
            }
        }
    }
}
