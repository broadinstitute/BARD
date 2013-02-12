package bard.db.registration

import bard.db.dictionary.Element

class ItemService {
    static class Item {
        String id
        AttributeType type
        Element attributeElement
        AssayContext assayContext
        List contextItems
    }

    def getLogicalItems(Collection<AssayContextItem> items) {
        def logicalItems = []
        items.each {
            if(it.attributeType != AttributeType.List) {
                logicalItems << new Item(id: "I${it.id}", type: it.attributeType, contextItems: [it], attributeElement: it.attributeElement, assayContext: it.assayContext)
            }
        }

        def listItems = items.findAll { it.attributeType == AttributeType.List }
        def grouped = listItems.groupBy { "L${it.assayContext.id}:${it.attributeElement.id}"}
        grouped.each {key, value ->
            logicalItems << new Item(id: key, type: AttributeType.List, contextItems: value, attributeElement: value[0].attributeElement, assayContext: value[0].assayContext)
        }

        return logicalItems
    }

    def get(String id) {
        if (id.startsWith("I")) {
            def item = AssayContextItem.get(id.substring(1))
            if (item == null)
                return null
            return new Item(id: id, type: item.attributeType, contextItems: [item])
        } else if (id.startsWith("L")) {
            int index = id.indexOf(":")
            String contextId = id.substring(1,index)
            String attributeTypeId = id.substring(index+1)

            Element attribute = Element.get(attributeTypeId)
            AssayContext context = AssayContext.get(contextId)

            def items = AssayContextItem.findAllByAttributeElementAndAssayContext(attribute, context)
            return new Item(id: id, type: AttributeType.List, contextItems: items, attributeElement: items[0].attributeElement)
        }  else {
            throw new RuntimeException("invalid id ${id}")
        }
    }
}
