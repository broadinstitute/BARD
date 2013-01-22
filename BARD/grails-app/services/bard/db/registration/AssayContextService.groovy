package bard.db.registration

import bard.db.registration.additemwizard.AttributeCommand;
import bard.db.registration.additemwizard.FixedValueCommand;
import bard.db.registration.additemwizard.ValueTypeCommand;
import bard.db.dictionary.*;

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

    public AssayContext createOrEditCardName(Long assayId, Long assayContextId, String name){
        AssayContext assayContext = AssayContext.get(assayContextId)
        Assay assay = Assay.get(assayId)
        if(assayContext && assayContext.contextName != name){
            assayContext.contextName = name
        }
        else{
            assayContext = new AssayContext(contextName: name)
            assay.addToAssayContexts(assayContext)
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

	public saveItemInCard(AttributeCommand attributeCmd, ValueTypeCommand valueTypeCmd, FixedValueCommand fixedValueCmd){		
		def isSaved = false
		String valueType = valueTypeCmd?.valueTypeOption
		String attributeType = AttributeType.Fixed
		println "ValueTypeOption:  " + valueTypeCmd?.valueTypeOption
		println "AttributeType.Fixed:  " + AttributeType.Fixed
		if(valueType.equals(attributeType)){
			println "Saving item with AttributeType = Fixed ..."
			AssayContext assayContext = AssayContext.get(attributeCmd.assayContextIdValue)
			Element attributeElement = Element.get(attributeCmd.elementId)
			if(assayContext && attributeElement){				
				AssayContextItem newAssayContextItem = new AssayContextItem()
				newAssayContextItem.setAttributeType(AttributeType.Fixed);
				if(fixedValueCmd?.valueId){
					Element valueElement = Element.get(fixedValueCmd.valueId)
					newAssayContextItem.attributeElement = attributeElement
					newAssayContextItem.valueElement = valueElement
					newAssayContextItem.valueDisplay = valueElement.label
					assayContext.addToAssayContextItems(newAssayContextItem)
					assayContext.save()
					println "Done saving item."
				}
				else{
					
				}
				isSaved = true
			}
			
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