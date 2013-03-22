package bard.db.registration

import bard.db.dictionary.Element
import bard.db.registration.additemwizard.*
import org.apache.commons.lang.StringUtils

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextService {

    public AssayContext addItem(AssayContextItem sourceItem, AssayContext targetAssayContext) {
        if (sourceItem && sourceItem.assayContext != targetAssayContext) {
            return addItem(targetAssayContext.assayContextItems.size(), sourceItem, targetAssayContext)
        }
    }

    public AssayContext addItem(int index, AssayContextItem sourceItem, AssayContext targetAssayContext) {
        AssayContext sourceAssayContext = sourceItem.assayContext
        sourceAssayContext.removeFromAssayContextItems(sourceItem)
        sourceItem.assayContext = targetAssayContext
        targetAssayContext.assayContextItems.add(index, sourceItem)
        return targetAssayContext
    }

    public void updateContextName(AssayContext targetAssayContext, AssayContextItem sourceAssayContextItem) {
        if (targetAssayContext && targetAssayContext == sourceAssayContextItem.assayContext) {
            targetAssayContext.contextName = sourceAssayContextItem.valueDisplay
        }
    }

    public AssayContext deleteItem(AssayContextItem assayContextItem) {
        AssayContext assayContext = assayContextItem.assayContext
        assayContext.removeFromAssayContextItems(assayContextItem)
        assayContextItem.delete(flush: true)
        return assayContext
    }

    public AssayContext createCard(Long assayId, String name, String cardSection) {
        Assay assay = Assay.get(assayId)
        AssayContext assayContext = new AssayContext(contextName: name, contextGroup: cardSection)
        assay.addToAssayContexts(assayContext)
        assayContext.save(flush: true)
        return assayContext
    }

    public AssayContext updateCardName(Long assayContextId, String name) {
        AssayContext assayContext = AssayContext.get(assayContextId)
        if (assayContext && assayContext.contextName != name) {
            assayContext.contextName = name
        }
        return assayContext
    }

    public void associateContext(Measure measure, AssayContext context) {
        AssayContextMeasure assayContextMeasure = new AssayContextMeasure();
        assayContextMeasure.measure = measure;
        assayContextMeasure.assayContext = context;
        measure.assayContextMeasures.add(assayContextMeasure)
        context.assayContextMeasures.add(assayContextMeasure)
    }

    public boolean disassociateContext(Measure measure, AssayContext context) {
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
            assayContext.save()
            isSaved = true
            println "Done saving item."
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
            assayContext.save()
            isSaved = true
            println "Done saving range item."
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
            assayContext.save()
            isSaved = true
            println "Done saving free item."
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
            assayContext.save()
            isSaved = true
            println "Done saving list of items"
            return isSaved;
        }
    }

    public createItem(AttributeCommand attributeCmd, ValueTypeCommand valTypeCmd, FixedValueCommand fixedValCmd, ListValueCommand listValCmd) {

        AssayContextItem newAssayContextItem = new AssayContextItem()
        Element attributeElement = Element.get(attributeCmd.attributeId)
        newAssayContextItem.attributeElement = attributeElement

        if (valTypeCmd.valueTypeOption.equals(AttributeType.Fixed.toString())) {
            newAssayContextItem.setAttributeType(AttributeType.Fixed);
            if (fixedValCmd.valueQualifier) {
                newAssayContextItem.qualifier = fixedValCmd.valueQualifier
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

    public Measure addMeasure(Assay assayInstance, Measure parentMeasure, Element resultType, Element statsModifier, Element entryUnit) {
        Measure measure = new Measure(assay: assayInstance, resultType: resultType, statsModifier: statsModifier, entryUnit: entryUnit, parentMeasure: parentMeasure);
        assayInstance.addToMeasures(measure)
        measure.save()

        return measure
    }
}