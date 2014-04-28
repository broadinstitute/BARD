/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
