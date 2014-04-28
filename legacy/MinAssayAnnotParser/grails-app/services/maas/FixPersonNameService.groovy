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
import bard.db.registration.AssayContextItem
import bard.db.experiment.ExperimentContextItem
import bard.db.project.ProjectContextItem
import bard.db.model.AbstractContextItem
import bard.db.people.Person
import bard.db.registration.AttributeType
import org.apache.commons.lang.StringUtils

/**
 * This service ran after populating context to fix
 * (contextItemDto.key == 'project lead name' || contextItemDto.key == 'science officer' || contextItemDto.key == 'assay provider name'))
 *
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/14/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
class FixPersonNameService {
    def attributesString = ['project lead name', 'science officer', 'assay provider name']
    List<Element> getElements() {
        return Element.findAllByLabelInList(attributesString)
    }
    def fix(String modifiedBy) {
        List<Element> elements = getElements()
        List<AbstractContextItem> items = []
        elements.each{Element element->
//            items.addAll(AssayContextItem.findAllByAttributeElement(element)) // Names are free in the assaycontextitem so no need to fix, really, project lead name
            items.addAll(ExperimentContextItem.findAllByAttributeElement(element))
            items.addAll(ProjectContextItem.findAllByAttributeElement(element))
            println("fixing total ${items.size()} contextitems for ${element.label}")
            fixContextItems(items, modifiedBy)
        }

    }

    def fixContextItems(List<AbstractContextItem> items, String modifiedBy) {
        items.each{
            fixEachItem(it, modifiedBy)
        }
    }

    /**
     * Search valuedisply in person table, if there is one found, use the id to update contextItem
     * @param item
     * @return
     */
    def fixEachItem(AbstractContextItem item, String modifiedBy) {
        if (item.extValueId && StringUtils.isNumeric(item.extValueId))
            return
        if (!item.valueDisplay) {
            println("Context Item ${item.id}: No person name be found in valueDisplay field")
            return
        }
        Person person = Person.findByUserNameIlike(item.valueDisplay)
        if (!person){
            person = new Person(userName: item.valueDisplay, fullName: item.valueDisplay, modifiedBy: modifiedBy)
            if (!person.save(flush: true)) {
                println "error in saving Person ${person.errors.toString()}"
            }
        }
        item.extValueId = person.id
    }
}
