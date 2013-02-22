package bard.db.registration

import bard.db.dictionary.*;
import bard.db.registration.additemwizard.*;

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextService {

    public AssayContext addItem(AssayContextItem sourceItem, AssayContext targetAssayContext) {
        if(sourceItem && sourceItem.assayContext != targetAssayContext){
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

    public AssayContext updateCardName(Long assayContextId, String name){
        AssayContext assayContext = AssayContext.get(assayContextId)
        if(assayContext && assayContext.contextName != name){
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
        for(assayContextMeasure in context.assayContextMeasures) {
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
	
	public saveItem(AssayContext assayContext, AttributeCommand attributeCmd, ValueTypeCommand valTypeCmd, FixedValueCommand fixedValCmd){
		def isSaved = false
		String FixedAttributeType = AttributeType.Fixed
		println "AttributeType.Fixed:  " + AttributeType.Fixed
		if(valTypeCmd.valueTypeOption.equals(FixedAttributeType)){
			println "Saving item with AttributeType = Fixed ..."
			
			if(fixedValCmd.isNumericValue){
				AssayContextItem newAssayContextItem = new AssayContextItem()
				newAssayContextItem.setAttributeType(AttributeType.Fixed);
			}
			else{
				Element attributeElement = Element.get(attributeCmd.attributeId)
				Element valueElement = Element.get(fixedValCmd.valueId)
				
				AssayContextItem newAssayContextItem = new AssayContextItem()
				newAssayContextItem.setAttributeType(AttributeType.Fixed);
				newAssayContextItem.attributeElement = attributeElement
				newAssayContextItem.valueElement = valueElement
				newAssayContextItem.valueDisplay = valueElement.label
				assayContext.addToAssayContextItems(newAssayContextItem)
				assayContext.save()
				isSaved = true
			}
			println "Done saving item."
			
		}
		else{
			
		}
		return isSaved;
	}

	public saveItemInCard(AssayContext assayContext, Element attributeElement, String valueType, boolean isNumericValue, Element valueElement){
		def isSaved = false
		String FixedAttributeType = AttributeType.Fixed
		println "AttributeType.Fixed:  " + AttributeType.Fixed
		if(valueType.equals(FixedAttributeType)){
			println "Saving item with AttributeType = Fixed ..."
			
			if(isNumericValue){
				
			}
			else{
				AssayContextItem newAssayContextItem = new AssayContextItem()
				newAssayContextItem.setAttributeType(AttributeType.Fixed);
				newAssayContextItem.attributeElement = attributeElement
				newAssayContextItem.valueElement = valueElement
				newAssayContextItem.valueDisplay = valueElement.label
				assayContext.addToAssayContextItems(newAssayContextItem)
				assayContext.save()
				isSaved = true
			}			
			println "Done saving item."
			
		}
		return isSaved;
	}

    public Measure addMeasure(Assay assayInstance, Measure parentMeasure, Element resultType, Element statsModifier, Element entryUnit) {
        Measure measure = new Measure(assay: assayInstance, resultType: resultType, statsModifier: statsModifier, entryUnit: entryUnit, parentMeasure: parentMeasure);
        assayInstance.addToMeasures(measure)
        measure.save()

        return measure
    }
}