package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.HierarchyType
import bard.db.registration.additemwizard.*
import org.apache.commons.lang.StringUtils
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextService {

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public AssayContext addItem(AssayContextItem sourceItem, AssayContext targetAssayContext, Long id) {
        if (sourceItem && sourceItem.assayContext != targetAssayContext) {
            return addItem(targetAssayContext.assayContextItems.size(), sourceItem, targetAssayContext)
        }
    }
    /**
     * This should not be called outside of this service. Making protected so we can test
     * @param index
     * @param sourceItem
     * @param targetAssayContext
     * @return
     */
    protected AssayContext addItem(int index, AssayContextItem sourceItem, AssayContext targetAssayContext) {
        AssayContext sourceAssayContext = sourceItem.assayContext
        sourceAssayContext.removeFromAssayContextItems(sourceItem)
        sourceItem.assayContext = targetAssayContext
        targetAssayContext.assayContextItems.add(index, sourceItem)
        return targetAssayContext
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public AssayContext updateCardName(Long assayContextId, String name, Long id) {
        AssayContext assayContext = AssayContext.get(assayContextId)
        if (assayContext && assayContext.contextName != name) {
            assayContext.contextName = name
        }
        return assayContext
    }

    @PreAuthorize("hasPermission(#id,'bard.db.registration.Assay',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public Measure changeParentChildRelationship(Measure measure, HierarchyType hierarchyType, Long id) {
        measure.parentChildRelationship = hierarchyType
        measure.save(flush: true)
        return measure
    }

    @PreAuthorize("hasPermission(#id,'bard.db.registration.Assay',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public void associateContext(Measure measure, AssayContext context, Long id) {
        AssayContextMeasure assayContextMeasure = new AssayContextMeasure();
        assayContextMeasure.measure = measure;
        assayContextMeasure.assayContext = context;
        measure.assayContextMeasures.add(assayContextMeasure)
        context.assayContextMeasures.add(assayContextMeasure)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public boolean disassociateContext(Measure measure, AssayContext context, Long id) {
        AssayContextMeasure found = null;
        for (assayContextMeasure in context.assayContextMeasures) {
            if (assayContextMeasure.measure == measure && assayContextMeasure.assayContext) {
                found = assayContextMeasure;
                break;
            }
        }

        if (found == null) {
            return false;
        } else {
            measure.removeFromAssayContextMeasures(found)
            context.removeFromAssayContextMeasures(found)
            found.delete(flush: true)
            return true;
        }
    }

    // Save an AssayContextItem with value type Fixed
    public saveItem(AssayContext assayContext, AttributeCommand attributeCmd, ValueTypeCommand valTypeCmd, FixedValueCommand fixedValCmd) {
        def isSaved = false
        if (valTypeCmd.valueTypeOption.equals(AttributeType.Fixed.toString())) {
            AssayContextItem newAssayContextItem = createItem(attributeCmd, valTypeCmd, fixedValCmd, null)
            assayContext.addToAssayContextItems(newAssayContextItem)
            isSaved = assayContext.save()
            println "Done saving item, isSaved=${isSaved}, errors=${assayContext.errors}"
        }
        return isSaved;
    }

    // Save an AssayContextItem with value type Range
    public saveRangeItem(AssayContext assayContext, AttributeCommand attributeCmd, ValueTypeCommand valTypeCmd, RangeValueCommand rangeValCmd) {
        def isSaved = false
        if (valTypeCmd.valueTypeOption.equals(AttributeType.Range.toString())) {
            AssayContextItem newAssayContextItem = new AssayContextItem()
            newAssayContextItem.setAttributeType(AttributeType.Range);
            Element attributeElement = Element.get(attributeCmd.attributeId)
            newAssayContextItem.attributeElement = attributeElement
            newAssayContextItem.valueMin = rangeValCmd.minValue.toFloat().floatValue()
            newAssayContextItem.valueMax = rangeValCmd.maxValue.toFloat().floatValue()
            newAssayContextItem.valueDisplay = newAssayContextItem.valueMin + " - " + newAssayContextItem.valueMax + " " + rangeValCmd.valueUnitLabel
            assayContext.addToAssayContextItems(newAssayContextItem)
            isSaved = assayContext.save()
            println "Done saving range item, isSaved=${isSaved}, errors=${assayContext.errors}"
        }
        return isSaved;
    }

    public saveFreeItem(AssayContext assayContext, AttributeCommand attributeCmd, ValueTypeCommand valTypeCmd) {
        def isSaved = false
        if (valTypeCmd.valueTypeOption.equals(AttributeType.Free.toString())) {

            AssayContextItem newAssayContextItem = new AssayContextItem()
            newAssayContextItem.setAttributeType(AttributeType.Free);
            Element attributeElement = Element.get(attributeCmd.attributeId)
            newAssayContextItem.attributeElement = attributeElement
            assayContext.addToAssayContextItems(newAssayContextItem)
            isSaved = assayContext.save()
            println "Done saving free item, isSaved=${isSaved}, errors=${assayContext.errors}"
        }
        return isSaved;
    }

    // Save an AssayContextItem with value type List
    public saveItems(AssayContext assayContext, AttributeCommand attributeCmd, ValueTypeCommand valTypeCmd, List<ListValueCommand> listOfValues) {
        def isSaved = false
        if (valTypeCmd.valueTypeOption.equals(AttributeType.List.toString())) {
            for (ListValueCommand value in listOfValues) {
                AssayContextItem newAssayContextItem = createItem(attributeCmd, valTypeCmd, null, value)
                assayContext.addToAssayContextItems(newAssayContextItem)
            }
            isSaved = assayContext.save()
            println "Done saving list of items, isSaved=${isSaved}, errors=${assayContext.errors}"
            return isSaved;
        }
    }

    private AssayContextItem createItem(AttributeCommand attributeCmd, ValueTypeCommand valTypeCmd, FixedValueCommand fixedValCmd, ListValueCommand listValCmd) {

        AssayContextItem newAssayContextItem = new AssayContextItem()
        Element attributeElement = Element.get(attributeCmd.attributeId)
        newAssayContextItem.attributeElement = attributeElement

        if (valTypeCmd.valueTypeOption.equals(AttributeType.Fixed.toString())) {
            newAssayContextItem.setAttributeType(AttributeType.Fixed);
            if (fixedValCmd.valueQualifier) {
                newAssayContextItem.qualifier = fixedValCmd.valueQualifier
            } else if (fixedValCmd.isNumericValue) {
                newAssayContextItem.qualifier = "= "
            }

            if (fixedValCmd.isNumericValue) {
                newAssayContextItem.valueNum = fixedValCmd.numericValue.toFloat().floatValue()
                if (fixedValCmd.valueUnitId) {
                    newAssayContextItem.valueDisplay = newAssayContextItem.valueNum + " " + fixedValCmd.valueUnitLabel
                }
            } else if (fixedValCmd.extValueId) {
                newAssayContextItem.extValueId = fixedValCmd.extValueId
                newAssayContextItem.valueDisplay = fixedValCmd.valueLabel
            } else if (!StringUtils.isBlank(fixedValCmd.textValue)) {
                newAssayContextItem.valueDisplay = fixedValCmd.textValue
            } else {
                Element valueElement = Element.get(fixedValCmd.valueId)
                newAssayContextItem.valueElement = valueElement
                newAssayContextItem.valueDisplay = valueElement.label
            }
        } else if (valTypeCmd.valueTypeOption.equals(AttributeType.List.toString())) {
            newAssayContextItem.setAttributeType(AttributeType.List);
            if (listValCmd.valueQualifier) {
                newAssayContextItem.qualifier = listValCmd.valueQualifier
            } else if (listValCmd.isNumericValue) {
                newAssayContextItem.qualifier = "= "
            }

            if (listValCmd.isNumericValue) {
                newAssayContextItem.valueNum = listValCmd.numericValue.toFloat().floatValue()
                if (listValCmd.valueUnitId) {
                    newAssayContextItem.valueDisplay = newAssayContextItem.valueNum + " " + listValCmd.valueUnitLabel
                }
            } else if (listValCmd.extValueId) {
                newAssayContextItem.extValueId = listValCmd.extValueId
                newAssayContextItem.valueDisplay = listValCmd.valueLabel
            } else if (!StringUtils.isBlank(listValCmd.textValue)) {
                newAssayContextItem.valueDisplay = listValCmd.textValue
            } else {
                Element valueElement = Element.get(listValCmd.valueId)
                newAssayContextItem.valueElement = valueElement
                newAssayContextItem.valueDisplay = valueElement.label
            }
        }
        return newAssayContextItem

    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public Measure addMeasure(Long id, Measure parentMeasure, Element resultType, Element statsModifier, Element entryUnit, HierarchyType hierarchyType) {
        Assay assay = Assay.findById(id)
        Measure measure = Measure.findByAssayAndResultTypeAndStatsModifierAndParentMeasure(assay, resultType, statsModifier, parentMeasure)
        if (measure) {
            //we need to throw an exception here
            throw new RuntimeException("Duplicate measures cannot be added to the same assay")
        }

        measure =
            new Measure(assay: assay, resultType: resultType, statsModifier: statsModifier,
                    entryUnit: entryUnit, parentMeasure: parentMeasure, parentChildRelationship: hierarchyType);
        assay.addToMeasures(measure)
        measure.save()

        return measure
    }
}