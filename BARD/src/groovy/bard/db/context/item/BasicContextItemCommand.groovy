package bard.db.context.item

import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.experiment.ExperimentContext
import bard.db.model.AbstractContextItem
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import bard.db.project.ProjectExperimentContext
import grails.validation.Validateable
import groovy.transform.TypeChecked
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
class BasicContextItemCommand extends BardCommand{

    static final List<String> CONTEXT_TYPES = [ProjectContext, ExperimentContext, ProjectExperimentContext].collect { it.simpleName }

    Long contextId
    String contextClass = "ProjectContext"
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

    AbstractContextItem createNewContextItem() {
        AbstractContextItem contextItemToReturn = null
        if (validate()){
            AbstractContextItem contextItem = new ProjectContextItem()
            contextItem.attributeElement = attemptFindById(Element, attributeElementId)
            if (valueElementId){
                contextItem.valueElement = attemptFindById(Element, valueElementId)
            }
            contextItem.extValueId = StringUtils.trimToNull(extValueId)
            contextItem.valueDisplay = valueDisplay
            contextItem.qualifier = StringUtils.trimToNull(qualifier)
            contextItem.valueNum = valueNum
            ProjectContext context = attemptFindById(ProjectContext, contextId)
            context.addToContextItems(contextItem)
            if (attemptSave(contextItem)){
                contextItemToReturn = contextItem
            }
        }
        contextItemToReturn

    }

}
