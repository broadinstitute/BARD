package bard.db.context.item

import bard.db.ContextItemService
import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.dictionary.UnitConversion
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import bard.db.project.ProjectService
import grails.validation.Validateable
import grails.validation.ValidationErrors
import org.apache.commons.lang.StringUtils
import org.springframework.context.MessageSource

import java.util.regex.Pattern;

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

    MessageSource messageSource
    AbstractContext context
    AbstractContextItem contextItem
    ProjectService projectService
    ContextItemService contextItemService

    Long contextOwnerId
    Long contextId
    Long contextItemId
    Long version
    String contextClass = "ProjectContext"

    Long attributeElementId
    String extValueId
    Long valueElementId

    String qualifier
    String valueMin
    String valueMax
    String valueNum
    Long valueNumUnitId

    String valueDisplay

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static constraints = {
        qualifier(nullable: true, inList: ['= ', '< ', '<=', '> ', '>=', '<<', '>>', '~ '])
        attributeElementId(nullable: false)
    }

    BasicContextItemCommand() {}

    BasicContextItemCommand(AbstractContextItem contextItem) {
        copyFromDomainToCmd(contextItem)
    }

    void copyFromDomainToCmd(AbstractContextItem contextItem) {
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
        this.valueNum = contextItem.valueNum ? new BigDecimal(contextItem.valueNum.toString()).stripTrailingZeros() : null
        this.valueMin = contextItem.valueMin ? new BigDecimal(contextItem.valueMin.toString()).stripTrailingZeros() : null
        this.valueMax = contextItem.valueMax ? new BigDecimal(contextItem.valueMax.toString()).stripTrailingZeros() : null
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

    void copyFromCmdToDomain(ProjectContextItem contextItem) {
        contextItem.attributeElement = attemptFindById(Element, attributeElementId)
        Element valueElement
        if (valueElementId) {
            valueElement = attemptFindById(Element, valueElementId)
        }
        contextItem.valueElement = valueElement
        contextItem.extValueId = StringUtils.trimToNull(extValueId)
        contextItem.valueDisplay = StringUtils.trimToNull(valueDisplay)
        contextItem.qualifier = qualifier
        contextItem.valueNum = convertToBigDecimal('valueNum', valueNum, contextItem.attributeElement?.unit)?.toFloat()
        if(contextItem.valueNum!=null && StringUtils.isBlank(qualifier)){
            contextItem.qualifier = '= '
        }
        contextItem.valueMin = convertToBigDecimal('valueMin', valueMin, contextItem.attributeElement?.unit)?.toFloat()
        contextItem.valueMax = convertToBigDecimal('valueMax', valueMax, contextItem.attributeElement?.unit)?.toFloat()
        if (valueNum || valueMin || valueMax) {
            contextItem.valueDisplay = contextItem.deriveDisplayValue()
        }

    }

    boolean update() {
        return validate() && contextItemService.updateContextItem(this)
    }

    boolean delete() {
        contextItemService.delete(this)
    }

    AbstractContext findContext() {
        attemptFindById(CONTEXT_NAME_TO_CLASS.get(this.contextClass), contextId)
    }

    /**
     * place holder to add logic when other contextItems are supported
     * @return
     */
    String getOwnerController() {
        'project'
    }

    BigDecimal convertToBigDecimal(String field, String value, Element unit) {
        BigDecimal convertedValue = null
        if (StringUtils.trimToNull(value)) {
            try {
                convertedValue = new BigDecimal(value).stripTrailingZeros()
                if (unit && unit.id != this.valueNumUnitId) {
                    Element fromUnit = attemptFindById(Element, valueNumUnitId)
                    UnitConversion unitConversion = UnitConversion.findByFromUnitAndToUnit(fromUnit, unit)
                    BigDecimal unitConvertedValue = unitConversion?.convert(convertedValue)
                    if (unitConvertedValue) {
                        convertedValue = unitConvertedValue.stripTrailingZeros()
                    }
                }
            }
            catch (NumberFormatException nfe) {
                ValidationErrors localErrors = new ValidationErrors(this)
                localErrors.rejectValue(field, 'contextItem.valueNum.not.valid')
                addToErrors(localErrors)
            }
        }
        return convertedValue
    }


}
