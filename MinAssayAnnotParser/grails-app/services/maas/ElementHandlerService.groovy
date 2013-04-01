package maas

import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.StringUtils

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


    // Build map to
    private static final String MAPPING_FILE_NAME = "data/maas/missingElements.txt"    //  file with element, description, and pareentid
    private static final String ELEMENT_DESCRIPTION = "data/maas/elementDescription.txt"  // another file format with element and description
    private static final String ELEMENT_PARENT = "data/maas/elementParent.txt"           // another file format with element and parent

    static void build(String fileName, Map elementDescription, Map elementParent) {
        if (StringUtils.isBlank(fileName)) {
            fileName = MAPPING_FILE_NAME
        }

        new File(fileName).eachLine {String line, int cnt ->
            if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(StringUtils.trim(line), "//")) {     // skip comment field
                String[] elements = line.split("\t")
                if (elements.length >= 2)
                    elementDescription.put(StringUtils.trim(elements[0]), StringUtils.trim(elements[1]))
                else
                   elementDescription.put(StringUtils.trim(elements[0]), "")
                if (elements.length >= 3
                        && StringUtils.isNotBlank(elements[2])
                        && StringUtils.isNumeric(StringUtils.trim(elements[2])))
                    elementParent.put(StringUtils.trim(elements[0]), StringUtils.trim(elements[2]))
            }
        }
    }

    static void buildElementDescription(String fileName, Map elementDescription) {
        if (StringUtils.isBlank(fileName)) {
            fileName = ELEMENT_DESCRIPTION
        }

        new File(fileName).eachLine {String line, int cnt ->
            if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(StringUtils.trim(line), "//")) {     // skip comment field
                String[] elements = line.split("\t")
                if (elements.length >= 2)
                    elementDescription.put(StringUtils.trim(elements[0]), StringUtils.trim(elements[1]))
                else
                    elementDescription.put(StringUtils.trim(elements[0]), "")
            }
        }
    }

    static void buildElementParent(String fileName, Map elementParent) {
        if (StringUtils.isBlank(fileName)) {
            fileName = ELEMENT_PARENT
        }

        new File(fileName).eachLine {String line, int cnt ->
            if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(StringUtils.trim(line), "//")) {     // skip comment field
                String[] elements = line.split("\t")
                if (elements.length >= 2 && StringUtils.isNumeric(StringUtils.trim(elements[1])))
                    elementParent.put(StringUtils.trim(elements[0]), StringUtils.trim(elements[1]))
            }
        }
    }
}
