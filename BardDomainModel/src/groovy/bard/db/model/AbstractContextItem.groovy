package bard.db.model

import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode

import static bard.db.enums.ExpectedValueType.*
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

        attributeElement(nullable: false)
        valueElement(nullable: true)

        extValueId(nullable: true, maxSize: EXT_VALUE_ID_MAX_SIZE)
        qualifier(nullable: true, inList: ['= ', '< ', '<=', '> ', '>=', '<<', '>>', '~ '])

        valueNum(nullable: true)
        valueMin(nullable: true)
        valueMax(nullable: true)
        valueDisplay(nullable: true, maxSize: VALUE_DISPLAY_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true,)
        modifiedBy(nullable: true, maxSize: MODIFIED_BY_MAX_SIZE,
                validator: { String field, AbstractContextItem instance, Errors errors ->
                    instance.valueValidation(errors)
                })

    }

    abstract T getContext()

    abstract void setContext(T context)

    /**
     * Note: the thought is that this would be useful for code creating or editing contextItems so at queryTime
     * clients will have a precomputed displayValue, this isn't meant to be a dynamic method used a queryTime
     *
     * @return String will try and create a reasonable displayValue based on the values populated in this item
     */
    @TypeChecked(TypeCheckingMode.SKIP)
    String deriveDisplayValue() {
        String result = null
        if (valueElement) {
            result = valueElement.label
        } else if (valueNum != null) {
            result = [qualifier?.trim(), valueNum, attributeElement.unit?.abbreviation].findAll().join(' ')
        } else if (valueMin != null || valueMax != null) {
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
    @TypeChecked
    protected void valueValidation(Errors errors) {
        valueValidation(errors, true)
    }

    /**
     *
     * @param errors adding any errors via reject methods indicates the class is not valid
     * @param includeRangeConstraints range constraints be excluded by passing false here, assayContextItems need to do this
     */
    @TypeChecked
    protected void valueValidation(Errors errors, boolean includeRangeConstraints) {
        if (attributeElement) {
            final ExpectedValueType expectedValueType = attributeElement.expectedValueType
            if (EXTERNAL_ONTOLOGY == expectedValueType) {
                externalOntologyConstraints(errors)
            } else if (ELEMENT == expectedValueType) {
                dictionaryConstraints(errors)
            } else if (NUMERIC == expectedValueType) {
                if (includeRangeConstraints && attributeElement.expectedValueType == NUMERIC && (valueMin != null || valueMax != null)) {
                    rangeConstraints(errors)
                } else {
                    valueNumConstraints(errors)
                }
            } else if (FREE_TEXT == expectedValueType) {
                textValueConstraints(errors)
            } else if (NONE == expectedValueType) {
                noneValueConstraints(errors)
            } else {
                throw new RuntimeException("Unsupported ExpectedValueType ${attributeElement.expectedValueType}")
            }
        }
    }

    @TypeChecked
    private textValueConstraints(Errors errors) {
        final boolean valueDisplayBlank = rejectBlankField('valueDisplay', errors)
        final boolean otherValuesNotNull = rejectNotNullFields(['extValueId', 'valueElement', 'qualifier', 'valueNum', 'valueMin', 'valueMax'], errors)
        if (valueDisplayBlank || otherValuesNotNull) {
            errors.reject('contextItem.attribute.expectedValueType.FREE_TEXT.required.fields')
        }
    }

    @TypeChecked
    private noneValueConstraints(Errors errors) {
        if (rejectNotNullFields(['extValueId', 'valueElement', 'qualifier', 'valueNum', 'valueMin', 'valueMax', 'valueDisplay'], errors)) {
            errors.reject('contextItem.attribute.expectedValueType.NONE.required.fields')
        }
    }

    @TypeChecked
    protected void valueNumConstraints(Errors errors) {
        final boolean valueNumNull = rejectNullField('valueNum', errors)
        final boolean qualifierBlank = rejectBlankField('qualifier', errors)
        final boolean valueDisplayBlank = rejectBlankField('valueDisplay', errors)
        final boolean otherFieldsNotNull = rejectNotNullFields(['extValueId', 'valueElement', 'valueMin', 'valueMax'], errors)
        if (valueNumNull || qualifierBlank || valueDisplayBlank || otherFieldsNotNull) {
            errors.reject('contextItem.valueNum.required.fields')
        }
    }

    @TypeChecked
    protected void rangeConstraints(Errors errors) {
        final boolean rangeValuesNull = rejectNullFields(['valueMin', 'valueMax'], errors)
        final boolean valueDisplayFieldBlank = rejectBlankField('valueDisplay', errors)
        final boolean otherValueFieldsNotNull = rejectNotNullFields(['extValueId', 'valueElement', 'qualifier', 'valueNum'], errors)
        if (rangeValuesNull || valueDisplayFieldBlank || otherValueFieldsNotNull) {
            errors.reject('contextItem.range.required.fields')
        } else if (valueMin != null || valueMax != null) {
            if (valueMin >= valueMax) {
                errors.rejectValue('valueMin', 'contextItem.valueMin.not.less.than.valueMax')
                errors.rejectValue('valueMax', 'contextItem.valueMax.not.greater.than.valueMin')
                errors.reject('contextItem.range.requirements')
            }
        }
    }

    @TypeChecked
    protected void dictionaryConstraints(Errors errors) {
        //TODO check to ensure valueElement is a descendant of the attributeElement
        final boolean valueElementNull = rejectNullField('valueElement', errors)
        final boolean valueDisplayBlank = rejectBlankField('valueDisplay', errors)
        final boolean otherValueFieldsNotNull = rejectNotNullFields(['extValueId', 'qualifier', 'valueNum', 'valueMin', 'valueMax'], errors)
        if (valueElementNull || valueDisplayBlank || otherValueFieldsNotNull) {
            errors.reject('contextItem.attribute.expectedValueType.ELEMENT.required.fields')
        }
    }

    @TypeChecked
    protected void externalOntologyConstraints(Errors errors) {
        final boolean externalOntologyFieldsBlank = rejectBlankFields(['extValueId', 'valueDisplay'], errors)
        final boolean otherFieldsNotNull = rejectNotNullFields(['valueElement', 'qualifier', 'valueNum', 'valueMin', 'valueMax'], errors)
        if (externalOntologyFieldsBlank || otherFieldsNotNull) {
            errors.reject('contextItem.attribute.externalURL.required.fields')
        }
    }

    @TypeChecked
    protected boolean rejectNonBlankFields(List<String> fieldNames, Errors errors) {
        List rejectedFields = []
        for (String fieldName in fieldNames) {
            rejectedFields << rejectNonBlankField(fieldName, errors)
        }
        rejectedFields.grep { it == true }
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    protected boolean rejectNonBlankField(String fieldName, Errors errors) {
        if (StringUtils.isNotBlank(this[(fieldName)])) {
            errors.rejectValue(fieldName, "contextItem.${fieldName}.blank")
            return true
        }
        return false
    }

    @TypeChecked
    protected boolean rejectBlankFields(List<String> fieldNames, Errors errors) {
        List rejectedFields = []
        for (String fieldName in fieldNames) {
            rejectedFields << rejectBlankField(fieldName, errors)
        }
        rejectedFields.grep { it == true }
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    protected boolean rejectBlankField(String fieldName, Errors errors) {
        if (StringUtils.isBlank(this[(fieldName)])) {
            errors.rejectValue(fieldName, "contextItem.${fieldName}.blank")
            return true
        }
        return false
    }

    @TypeChecked
    protected boolean rejectNotNullFields(List<String> fieldNames, Errors errors) {
        List rejectedFields = []
        for (String fieldName in fieldNames) {
            rejectedFields << rejectNotNullField(fieldName, errors)
        }
        rejectedFields.grep { it == true }
    }

    @TypeChecked
    private boolean rejectNotNullField(String fieldName, Errors errors) {
        if (this[(fieldName)] != null) {
            errors.rejectValue(fieldName, "contextItem.${fieldName}.not.null")
            return true
        }
        return false
    }

    @TypeChecked
    protected boolean rejectNullFields(List<String> fieldNames, Errors errors) {
        List rejectedFields = []
        for (String fieldName in fieldNames) {
            rejectedFields << rejectNullField(fieldName, errors)
        }
        rejectedFields.grep { it == true }
    }

    @TypeChecked
    private boolean rejectNullField(String fieldName, Errors errors) {
        if (this[(fieldName)] == null) {
            errors.rejectValue(fieldName, "contextItem.${fieldName}.null")
            return true
        }
        return false
    }


}
