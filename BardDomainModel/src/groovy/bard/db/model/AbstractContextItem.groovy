package bard.db.model

import bard.db.dictionary.Element
import org.apache.commons.lang3.StringUtils
import org.springframework.validation.Errors

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/2/12
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractContextItem<T extends AbstractContext> {
    private static final int EXT_VALUE_ID_MAX_SIZE = 60
    private static final int VALUE_DISPLAY_MAX_SIZE = 500
    private static final int MODIFIED_BY_MAX_SIZE = 40

    Element attributeElement
    Element valueElement

    String extValueId
    String qualifier
    Float valueNum
    Float valueMin
    Float valueMax
    String valueDisplay

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static constraints = {

        attributeElement(nullable: false,
                validator: { Element field, AbstractContextItem instance, Errors errors ->
                    instance.valueValidation(errors)
                })
        valueElement(nullable: true)

        extValueId(nullable: true, blank: false, maxSize: EXT_VALUE_ID_MAX_SIZE)
        qualifier(nullable: true, blank: false, inList: ['= ', '< ', '<=', '> ', '>=', '<<', '>>', '~ '])

        valueNum(nullable: true)
        valueMin(nullable: true)
        valueMax(nullable: true)
        valueDisplay(nullable: true, blank: false, maxSize: VALUE_DISPLAY_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true,)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)

    }

    abstract T getContext()

    abstract void setContext(T context)

    /**
     * Note: the thought is that this would be useful for code creating or editing contextItems so at queryTime
     * clients will have a precomputed displayValue, this isn't meant to be a dynamic method used a queryTime
     *
     * @return String will try and create a reasonable displayValue based on the values populated in this item
     */
    String deriveDisplayValue() {
        String result = null
        if (valueElement) {
            result = valueElement.label
        } else if (valueNum) {
            result = [qualifier?.trim(), valueNum, attributeElement.unit?.abbreviation].findAll().join(' ')
        } else if (valueMin || valueMax) {
            result = [valueMin, valueMax].findAll().join(' - ')
        }
    }

    /**
     * Business rules for validating a contextItem
     * The value a contextItem holds can be held in 1 or more columns, but only certain combinations are valid and the
     * value of other fields particularly the attributeElement impact what state is valid
     * @see <a href="https://github.com/broadinstitute/BARD/wiki/Business-rules#general-business-rules-for-assay_context_item">general-business-rules-for-assay_context_item</a>
     * @param errors adding any errors via reject methods indicates the class is not valid
     */
    protected void valueValidation(Errors errors) {
        if (attributeElement) {
            if (attributeElement.externalURL) {
                externalOntologyContraints(errors)
            } else if (attributeElement.unit) {
                numericValueConstraints(errors)
            } else if (valueElement) {
                dictionaryConstraints(errors)
            }
        }
    }

    protected void numericValueConstraints(Errors errors) {
        //TODO add global error
        ensureFieldNotNull('valueNum', errors)
        ensureFieldsNull(['extValueId', 'valueElement', 'valueMin', 'valueMax'], errors)
    }

    protected void dictionaryConstraints(Errors errors) {
        //TODO add global error
        ensureFieldNotBlank('valueDisplay', errors)
        ensureFieldsNull(['extValueId', 'qualifier', 'valueNum', 'valueMin', 'valueMax'], errors)
    }

    protected void externalOntologyContraints(Errors errors) {
        if (StringUtils.isBlank(extValueId) || StringUtils.isBlank(valueDisplay)) {
            errors.reject('contextItem.attribute.externalURL.required.fields')
        }
        ensureFieldsNotBlank(['extValueId','valueDisplay'], errors)
        ensureFieldsNull(['valueElement', 'qualifier', 'valueNum', 'valueMin', 'valueMax'], errors)
    }

    protected void ensureFieldsNotBlank(List<String> fieldNames, Errors errors) {
        for (String fieldName in fieldNames) {
            ensureFieldNotBlank(fieldName, errors)
        }
    }

    protected void ensureFieldNotBlank(String fieldName, Errors errors) {
        if (StringUtils.isBlank(this[(fieldName)])) {
            errors.rejectValue(fieldName, "contextItem.${fieldName}.blank")
        }
    }

    protected void ensureFieldsNotNull(List<String> fieldNames, Errors errors) {
        for (String fieldName in fieldNames) {
            ensureFieldNotNull(fieldName, errors)
        }
    }

    private ensureFieldNotNull(String fieldName, Errors errors) {
        if (this[(fieldName)] == null) {
            errors.rejectValue(fieldName, "contextItem.${fieldName}.null")
        }
    }

    protected void ensureFieldsNull(List<String> fieldNames, Errors errors) {
        for (String fieldName in fieldNames) {
            if (this[(fieldName)] != null) {
                errors.rejectValue(fieldName, "contextItem.${fieldName}.not.null")
            }
        }
    }
}
