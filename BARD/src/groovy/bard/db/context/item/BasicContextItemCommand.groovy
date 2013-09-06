package bard.db.context.item

import bard.db.ContextItemService
import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.dictionary.UnitConversion
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner
import bard.db.project.Project
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType
import grails.validation.Validateable
import grails.validation.ValidationErrors
import org.apache.commons.lang.StringUtils

import java.util.regex.Pattern

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
    public static final Map<String, Class> CONTEXT_NAME_TO_OWNER_CLASS = ['ProjectContext': Project, 'AssayContext': Assay, 'ExperimentContext': Experiment]
    public static final Map<String, Class> CONTEXT_NAME_TO_CLASS = ['ProjectContext': ProjectContext, 'AssayContext': AssayContext, 'ExperimentContext': ExperimentContext]
    public static final Map<String, Class> CONTEXT_NAME_TO_ITEM_CLASS = ['ProjectContext': ProjectContextItem, 'AssayContext': AssayContextItem, 'ExperimentContext': ExperimentContextItem]
    public static final Map<String, String> CONTEXT_NAME_TO_CONTROLLER = ['ProjectContext': 'project', 'AssayContext': 'assayDefinition', 'ExperimentContext': 'experiment']

    static Class getContextItemClass(String name) {
        return CONTEXT_NAME_TO_ITEM_CLASS[name]
    }

    static Class getContextClass(String name) {
        return CONTEXT_NAME_TO_CLASS[name]
    }

    static Class getContextOwnerClass(String name) {
        return CONTEXT_NAME_TO_OWNER_CLASS[name]
    }

//    MessageSource messageSource
    AbstractContext context
    AbstractContextItem contextItem
    ContextItemService contextItemService



    Long contextOwnerId
    Long contextId
    Long contextItemId
    Long version
    String contextClass = "ProjectContext"
    boolean providedWithResults = false
    String valueConstraintType

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
        valueConstraintType(nullable: true, inList: ["Free", "List", "Range"])
    }

    BasicContextItemCommand() {}

    BasicContextItemCommand(AbstractContextItem contextItem) {
        copyFromDomainToCmd(contextItem)
    }

    AbstractContext attemptFindContext() {
        return attemptFindById(getContextClass(contextClass), contextId)
    }

    AbstractContextItem attemptFindItem() {
        return attemptFindById(getContextItemClass(contextClass), contextItemId)
    }

    void copyFromDomainToCmd(AbstractContextItem contextItem) {
        this.context = contextItem.context
        this.contextItem = contextItem
        this.contextOwnerId = contextItem.context.owner.id
        this.contextId = contextItem.context.id
        this.contextItemId = contextItem.id
        this.version = contextItem.version
        this.contextClass = contextItem.context.simpleClassName

        this.attributeElementId = contextItem.attributeElement?.id
        this.valueElementId = contextItem.valueElement?.id
        this.extValueId = contextItem.extValueId

        this.qualifier = contextItem.qualifier
        this.valueNum = contextItem.valueNum ? new BigDecimal(contextItem.valueNum.toString()).stripTrailingZeros() : null
        this.valueMin = contextItem.valueMin ? new BigDecimal(contextItem.valueMin.toString()).stripTrailingZeros() : null
        this.valueMax = contextItem.valueMax ? new BigDecimal(contextItem.valueMax.toString()).stripTrailingZeros() : null
        this.valueConstraintType = null
        if (contextItem instanceof AssayContextItem) {
            AssayContextItem assayContextItem = contextItem
            if (assayContextItem.attributeType == AttributeType.Fixed) {
                this.valueConstraintType = null;
                this.providedWithResults = false;
            } else {
                this.valueConstraintType = assayContextItem.attributeType.name();
                this.providedWithResults = true;
            }
        }

        this.valueNumUnitId = contextItem.attributeElement.unit?.id

        this.valueDisplay = contextItem.valueDisplay

        this.dateCreated = contextItem.dateCreated
        this.lastUpdated = contextItem.lastUpdated
        this.modifiedBy = contextItem.modifiedBy
    }



    boolean createNewContextItem() {
        boolean createSuccessful = false
        context = attemptFindById(getContextClass(this.contextClass), contextId)
        if (validate()) {
            final AbstractContextOwner owningContext = context.getOwner()
            if (owningContext instanceof Assay) {
                return contextItemService.createAssayContextItem(owningContext.id, this)
            }
            else if (owningContext instanceof Project) {
                return contextItemService.createProjectContextItem(owningContext.id, this)
            }
            else if (owningContext instanceof Experiment) {
                return contextItemService.createExperimentContextItem(owningContext.id, this)
            }
            else {
                throw new RuntimeException("Unknown owning context: ${owningContext}")
            }
        }
        return createSuccessful
    }

    void copyFromCmdToDomain(AbstractContextItem contextItem) {
        contextItem.attributeElement = attemptFindById(Element, attributeElementId)

        // figure out the value type by what was provided.  Eventually it'd be nice to push value type into the
        // form submission too
        if (valueElementId) {
            Element valueElement
            valueElement = attemptFindById(Element, valueElementId)
            contextItem.setDictionaryValue(valueElement)
            contextItem.valueElement = valueElement

        } else if(!StringUtils.isBlank(contextItem.extValueId)) {
            contextItem.setExternalOntologyValue(StringUtils.trimToNull(extValueId), StringUtils.trimToNull(valueDisplay))

        } else if(valueNum != null) {
            contextItem.setNumericValue(StringUtils.isBlank(qualifier)?"= ":qualifier, convertToBigDecimal('valueNum', valueNum, contextItem.attributeElement?.unit)?.toFloat())
        } else if(valueMin != null) {
            contextItem.setRange(convertToBigDecimal('valueMin', valueMin, contextItem.attributeElement?.unit)?.toFloat(), convertToBigDecimal('valueMax', valueMax, contextItem.attributeElement?.unit)?.toFloat())
        }

        if (contextItem instanceof AssayContextItem) {
            AssayContextItem assayContextItem = contextItem;

            if (providedWithResults && valueConstraintType != null) {
                assayContextItem.attributeType = Enum.valueOf(AttributeType, valueConstraintType)
            } else {
                assayContextItem.attributeType = AttributeType.Fixed;
            }
        }
    }

    boolean update() {
        final AbstractContextOwner owner = this.findContext().owner
        if (owner instanceof Assay) {
            return validate() && contextItemService.updateAssayContextItem(owner.id, this)
        } else if (owner instanceof Project) {
            return validate() && contextItemService.updateProjectContextItem(owner.id,this)
        } else if (owner instanceof Experiment) {
            return validate() && contextItemService.updateExperimentContextItem(owner.id, this)
        }
        return false
    }

    boolean delete() {
        final AbstractContextOwner owner = this.findContext().owner
        if (owner instanceof Assay) {
            return contextItemService.deleteAssayContextItem(owner.id,owner, this)
        } else if (owner instanceof Project) {
            return contextItemService.deleteProjectContextItem(owner.id, owner, this)
        } else if (owner instanceof Experiment) {
            return contextItemService.deleteExperimentContextItem( owner.id,owner, this)
        }
    }

    AbstractContext findContext() {
        attemptFindById(CONTEXT_NAME_TO_CLASS.get(this.contextClass), contextId)
    }

    /**
     * place holder to add logic when other contextItems are supported
     * @return
     */
    String getOwnerController() {
        return CONTEXT_NAME_TO_CONTROLLER[contextClass]
    }

    BigDecimal convertToBigDecimal(String field, String value, Element unit) {
        BigDecimal convertedValue = null
        if (StringUtils.trimToNull(value)) {
            try {
                convertedValue = new BigDecimal(value).stripTrailingZeros()
                if (unit && valueNumUnitId != null && unit.id != this.valueNumUnitId) {
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
