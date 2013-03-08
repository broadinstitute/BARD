package maas

import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import org.apache.commons.lang.StringUtils

class ElementHandlerService {
    /**
     * Add missing elements and relationship
     * @param modifiedBy
     * @param elementAndDescription
     * @return
     */
    def addMissingElement(String modifiedBy, Map elementAndDescription, Map elementAndParent) {
        elementAndDescription.each() {key, value->
            def element = Element.findByLabelIlike(key)
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

    def addElement(String modifiedBy, String elementLabel) {
        def element = Element.findByLabelIlike(elementLabel)
        if (!element) {
            element = new Element(label: elementLabel, modifiedBy: modifiedBy)
            element.save(flush: true)
        }
        return element
    }

    def addMissingName(String fileName) {
        new File(fileName).each{String line ->
            String[] elements = line.split(":")
            addElement("xiaorong-maas", StringUtils.trim(elements[1]))
        }
    }
}
