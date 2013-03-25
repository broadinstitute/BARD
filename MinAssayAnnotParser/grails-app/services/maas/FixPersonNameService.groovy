package maas

import bard.db.dictionary.Element
import bard.db.registration.AssayContextItem
import bard.db.experiment.ExperimentContextItem
import bard.db.project.ProjectContextItem
import bard.db.model.AbstractContextItem
import bard.db.people.Person
import bard.db.registration.AttributeType

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
           // items.addAll(AssayContextItem.findAllByAttributeElement(element)) // Names are free in the assaycontextitem so no need to fix
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
        Person person = Person.findByUserNameIlike(item.valueDisplay)
        if (!person){
            person = new Person(userName: item.valueDisplay, fullName: item.valueDisplay, modifiedBy: modifiedBy)
            if (!person.save(flush: true)) {
                println "error in saving ${person.errors.toString()}"
            }
        }
        item.extValueId = person.id
    }
}
