package bard.db.context.item

import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.dictionary.UnitConversion
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import bard.db.project.ProjectService
import grails.validation.Validateable
import org.apache.commons.lang.StringUtils
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import bard.utils.NumberUtils;

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
	
	private static final Pattern SCIENTIFIC_NOTATION_PATTERN = Pattern.compile("^[-+]?[1-9][0-9]*\\.?[0-9]*([Ee][+-]?[0-9]+)")
    static final List<String> CONTEXT_TYPES = [ProjectContext].collect { it.simpleName }
    static final Map<String, Class> CONTEXT_NAME_TO_CLASS = ['ProjectContext': ProjectContext]
    AbstractContext context
    AbstractContextItem contextItem
    ProjectService projectService

    Long contextOwnerId
    Long contextId
    Long contextItemId
    Long version
    String contextClass = "ProjectContext"

    Long attributeElementId
    String extValueId
    Long valueElementId

    String qualifier
	BigDecimal valueMin
	BigDecimal valueMax
    BigDecimal valueNum
	String valueMinField
	String valueMaxField
	String valueNumField
    Long valueNumUnitId
	
    String valueDisplay

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static constraints = {
        qualifier(nullable: true, inList: ['= ', '< ', '<=', '> ', '>=', '<<', '>>', '~ '])
        attributeElementId(nullable: false)
		
		valueNumField(nullable: true)
		valueMinField(nullable: true)
		valueMaxField(nullable: true)
		
//        contextId(nullable: false)
////        contextType(nullable: false, inList: CONTEXT_TYPES)
//
//        valueElementId(nullable: true)
//        extValueId(nullable: true)
//        qualifier(nullable: true)
//        valueNum(nullable: true)
//        valueDisplay(nullable: true)
    }
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
        copyFromDomainToCmd(contextItem)
    }
	
	private convertValue(String value){
		BigDecimal convertedValue = NumberUtils.convertScientificNotationValue(value)
		return convertedValue ?: value
	}

    private copyFromDomainToCmd(AbstractContextItem contextItem) {
        this.context = contextItem.context
        this.contextItem = contextItem
        this.contextOwnerId = contextItem.context.owner.id
        this.contextId = contextItem.context.id
        this.contextItemId = contextItem.id
        this.version = contextItem.version
        this.contextClass = contextItem.context.class.simpleName

        this.attributeElementId = contextItem.attributeElement?.id
        this.valueElementId = contextItem.valueElement?.id
        this.extValueId = contextItem.extValueId

        this.qualifier = contextItem.qualifier
        this.valueNum = contextItem.valueNum?.toBigDecimal()
        this.valueNumUnitId = contextItem.attributeElement.unit?.id

        this.valueDisplay = contextItem.valueDisplay

        this.dateCreated = contextItem.dateCreated
        this.lastUpdated = contextItem.lastUpdated
        this.modifiedBy = contextItem.modifiedBy
    }



    boolean createNewContextItem() {
        boolean createSuccessful = false
        context = attemptFindById(CONTEXT_NAME_TO_CLASS.get(this.contextClass), contextId)
        if (validate()) {
            ProjectContextItem contextItem = new ProjectContextItem()
            copyFromCmdToDomain(contextItem)
            context.addToContextItems(contextItem)
            if (attemptSave(contextItem)) {
                copyFromDomainToCmd(contextItem)

                createSuccessful = true
            }
        }
        createSuccessful
    }

    private copyFromCmdToDomain(ProjectContextItem contextItem) {
        contextItem.attributeElement = attemptFindById(Element, attributeElementId)
        Element valueElement
        if (valueElementId) {
            valueElement = attemptFindById(Element, valueElementId)
        }
        contextItem.valueElement = valueElement
        contextItem.extValueId = StringUtils.trimToNull(extValueId)
        contextItem.valueDisplay = StringUtils.trimToNull(valueDisplay)
        contextItem.qualifier = qualifier
        contextItem.valueNum = valueNum
        if (valueNumUnitId != contextItem.attributeElement.unit?.id) {
            Element fromUnit = attemptFindById(Element, valueNumUnitId)
            UnitConversion unitConversion = UnitConversion.findByFromUnitAndToUnit(fromUnit, contextItem.attributeElement.unit)
            contextItem.valueNum = unitConversion?.convert(valueNum)
        }
        if (valueNum){
            contextItem.valueDisplay = contextItem.deriveDisplayValue()
        }
    }

    boolean update() {
        boolean updateSuccessful = false
        if (validate()) {
            ProjectContextItem contextItem = attemptFindById(ProjectContextItem, contextItemId)
            if (contextItem) {
                if (this.version?.longValue() != contextItem.version.longValue()) {
                    getErrors().reject('default.optimistic.locking.failure', [ProjectContextItem] as Object[], 'optimistic lock failure')
                    copyFromDomainToCmd(contextItem)
                } else {
                    copyFromCmdToDomain(contextItem)
                    updateSuccessful = attemptSave(contextItem)
                    copyFromDomainToCmd(contextItem)
                }
            }
        }
        updateSuccessful
    }

    boolean delete() {
        AbstractContext context = attemptFindById(CONTEXT_NAME_TO_CLASS.get(this.contextClass), contextId)
        if(context){
            return projectService.deleteContextItem(context,this.contextId)
        } else{
            return false
        }
    }

    /**
     * place holder to add logic when other contextItems are supported
     * @return
     */
    String getOwnerController() {
        'project'
    }

}
