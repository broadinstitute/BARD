package maas

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContextItem
import bard.db.dictionary.Element
import bard.db.registration.AttributeType
import bard.db.project.Project
import bard.db.project.ProjectExperiment
import bard.db.model.AbstractContextItem
import bard.db.project.ProjectContextItem

class ContextHandlerService {
    AbstractContextItem updateContextItem(ContextItemDto contextItemDto, String loadedBy, List<String> errorMessages, String contextType) {
        Element attributeElement = Element.findByLabelIlike(contextItemDto.key)
        if (!attributeElement) {
            final String message = "Attribute element not exist: (${contextItemDto.key})"
            errorMessages << message
        }
        AbstractContextItem contextItem = null
        if (contextType == "Experiment")
            contextItem = new ExperimentContextItem(attributeElement: attributeElement, modifiedBy: loadedBy)
        else if (contextType == "Project")
            contextItem = new ProjectContextItem(attributeElement: attributeElement, modifiedBy: loadedBy)

        Element concentrationUnitsElement = contextItemDto.concentrationUnits ? Element.findByLabelIlike(contextItemDto.concentrationUnits) : null
        String concentrationUnitsAbbreviation = concentrationUnitsElement ? " ${concentrationUnitsElement.abbreviation}" : ""
        //populate attribute-value type and value
        Element valueElement = contextItemDto.value ? Element.findByLabelIlike(contextItemDto.value) : null

        /* NOTE: Special handling: check if it has been created, for the first round there is no name for these two fields, we add them */
        if (!valueElement && contextItemDto.value &&
                (contextItemDto.key == 'project lead name' || contextItemDto.key == 'science officer' || contextItemDto.key == 'assay provider name')) {
            errorMessages("value element missing for names: " + contextItemDto.value)
        }

        //if the value string could be matched against an element then add it to the valueElement
        if (valueElement) {
            contextItem.valueElement = valueElement
            contextItem.valueDisplay = valueElement.label
        }
        //else, if the attribute's value is a number value, store it in the valueNum field
        else if (contextItemDto.value && (!(contextItemDto.value instanceof String) || contextItemDto.value.isNumber())) {
            Float val = new Float(contextItemDto.value)
            contextItem.valueNum = val
            //If the value is a number and also has concentration-units, we need to find the units element ID and update the valueDisplay accrdingly
            contextItem.valueDisplay = val.toString() + concentrationUnitsAbbreviation
        }
        //else, if the attribute is a numeric range (e.g., 440-460nm -> 440-460), then store it in valueMin, valueMax and make AttributeType=range.
        else if (contextItemDto.value && (contextItemDto.value instanceof String) && contextItemDto.value.matches(/^\d+\-\d+$/)) {
            final String[] rangeStringArray = contextItemDto.value.split("-")
            contextItem.valueMin = new Float(rangeStringArray[0])
            contextItem.valueMax = new Float(rangeStringArray[1])
            contextItem.valueDisplay = contextItemDto.value + concentrationUnitsAbbreviation //range-units are reported separately.
        }
        //else, if the attribute's is a type-in or attribute-type is Free, then simply store it the valueDisplay field
        else if (contextItemDto.typeIn || (contextItemDto.attributeType == AttributeType.Free)) {
            contextItem.valueDisplay = contextItemDto.value
        }
        else {
            final String message = "Can not handle Key: ${contextItemDto.key}, Value: ${contextItemDto.value}"
            errorMessages << message
        }

        //populate the qualifier field, if exists, and prefix the valueDisplay with it
        if (contextItemDto.qualifier) {
            contextItem.qualifier = String.format('%-2s', contextItemDto.qualifier)
            contextItem.valueDisplay = "${contextItemDto.qualifier}${contextItem.valueDisplay}"
        }
        return contextItem
    }

    Experiment getExperimentFromAid(long aid) {
        def criteria = Experiment.createCriteria()
        List<Experiment> results = criteria.list {
            externalReferences {
                eq('extAssayRef', "aid=${aid.toString()}")
            }
        }
        return (results && (results.size() == 1)) ? results.first() : null
    }

    Project getProjectFromAid(long aid) {
        Experiment ex = getExperimentFromAid(aid)
        Project project = null
        if (ex) {
            project = ProjectExperiment.findByExperiment(ex).project
        }
        return project
    }

}
