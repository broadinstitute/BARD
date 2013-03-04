package maas

import bard.db.dictionary.Element

class ElementHandlerService {
    def addMissingElement(String modifiedBy, Map elementAndDescription) {
        elementAndDescription.each() {key, value->
            def element = Element.findAllByLabel(key)
            if (!element) {
                element = new Element(label: key, modifiedBy: modifiedBy, description: value)
                element.save(flush: true)
            }
        }
    }
}
