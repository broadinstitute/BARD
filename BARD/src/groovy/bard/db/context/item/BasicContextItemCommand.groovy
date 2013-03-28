package bard.db.context.item

import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import grails.validation.Validateable
import org.apache.commons.lang.StringUtils

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/25/13
 * Time: 11:37 PM
 * Has most of the fields possible for a context item, with the exception of the range attributes value_min and value_max
 * as these aren't used for most types of contextItems
 */
@Validateable
class BasicContextItemCommand extends BardCommand {

    static final List<String> CONTEXT_TYPES = [ProjectContext].collect { it.simpleName }
    static final Map<String, Class> CONTEXT_NAME_TO_CLASS = ['ProjectContext': ProjectContext]
    Long contextOwnerId
    Long contextId
    Long contextItemId
    Long version
    String contextClass = "ProjectContext"

    String attributeElementLabel
    Long attributeElementId
    Long valueNumUnitId

    Long valueElementId
    String extValueId
    String qualifier
    Float valueNum
    String valueDisplay

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

//    static constraints = {
//        importFrom(AbstractContextItem, exclude: ['dateCreated', 'lastUpdated', 'modifiedBy'])
//        attributeElementId(nullable: false)
//        contextId(nullable: false)
//        contextType(nullable: false, inList: CONTEXT_TYPES)
//
//        valueElementId(nullable: true)
//        extValueId(nullable: true)
//        qualifier(nullable: true)
//        valueNum(nullable: true)
//        valueDisplay(nullable: true)
//    }
//
//    /**
//     * a context item has a number of nullable fields, however, they should only ever be partially populated
//     *
//     * that is some columns should be mutually exclusive
//     * @return true if this item looks to be valid
//     */
//    boolean validateCommand() {
//        if(valueElementId){
//            return true
//        }
//        else if (StringUtils.isNotBlank(extValueId)){
//            return true
//        }
//        else if (valueNum){
//
//        }
//    }
    BasicContextItemCommand() {}

    BasicContextItemCommand(AbstractContextItem contextItem) {

        contextOwnerId = contextItem.context.owner.id
        contextId = contextItem.context.id
        contextItemId = contextItem.id
        version = version
        contextClass = contextItem.context.class.simpleName
        attributeElementLabel = contextItem.attributeElement.label
        attributeElementId = contextItem.attributeElement?.id
        valueNumUnitId

        valueElementId = contextItem.valueElement?.id
        extValueId = contextItem.extValueId
        qualifier = contextItem.qualifier
        valueNum = contextItem.valueNum
        valueDisplay = contextItem.valueDisplay

        dateCreated = contextItem.dateCreated
        lastUpdated = contextItem.lastUpdated
        modifiedBy = contextItem.modifiedBy
    }



    AbstractContextItem createNewContextItem() {
        AbstractContextItem contextItemToReturn = null
        if (validate()) {
            ProjectContextItem contextItem = new ProjectContextItem()
            contextItem.attributeElement = attemptFindById(Element, attributeElementId)
            if (valueElementId) {
                contextItem.valueElement = attemptFindById(Element, valueElementId)
            }
            contextItem.extValueId = StringUtils.trimToNull(extValueId)
            contextItem.valueDisplay = valueDisplay
            contextItem.qualifier = StringUtils.trimToNull(qualifier)
            contextItem.valueNum = valueNum

            ProjectContext context = attemptFindById(CONTEXT_NAME_TO_CLASS.get(this.contextClass), contextId)
            context.addToContextItems(contextItem)
            if (attemptSave(contextItem)) {
                contextItemToReturn = contextItem
            }
        }
        contextItemToReturn

    }

    boolean delete() {
        AbstractContext context = attemptFindById(CONTEXT_NAME_TO_CLASS.get(this.contextClass), contextId)
        AbstractContextItem contextItem = context.contextItems.find { it.id == this.contextItemId }
        if (contextItem) {
            context.contextItems.remove(contextItem)
            contextItem.delete(flush: true)
            return true
        }
        return false
    }

    /**
     * place holder to add logic when other contextItems are supported
     * @return
     */
    String getOwnerController() {
        'project'
    }

}
