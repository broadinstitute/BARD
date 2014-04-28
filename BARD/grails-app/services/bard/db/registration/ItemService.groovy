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

package bard.db.registration

import bard.db.dictionary.Element

class ItemService {
    static class Item {
        String id
        AttributeType type
        Element attributeElement
        AssayContext assayContext
        List contextItems

        String getDisplayLabel() {
            return attributeElement.label
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (!(o instanceof Item)) return false

            Item item = (Item) o

            if (id != item.id) return false

            return true
        }

        int hashCode() {
            return id.hashCode()
        }
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
            return new Item(id: id, type: item.attributeType, contextItems: [item], attributeElement: item.attributeElement, assayContext: item.assayContext)
        } else if (id.startsWith("L")) {
            int index = id.indexOf(":")
            String contextId = id.substring(1,index)
            String attributeTypeId = id.substring(index+1)

            Element attribute = Element.get(attributeTypeId)
            AssayContext context = AssayContext.get(contextId)

            def items = AssayContextItem.findAllByAttributeElementAndAssayContext(attribute, context)
            return new Item(id: id, type: AttributeType.List, contextItems: items, attributeElement: items[0].attributeElement, assayContext: items[0].assayContext)
        }  else {
            throw new RuntimeException("invalid id ${id}")
        }
    }
}
