package maas

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.dictionary.Element
import bard.db.registration.AttributeType


class ExperimentHandlerService {

    final int START_ROW = 2 //0-based
    final int MAX_ROWS = 80
    def handle(String loadedBy, List<String> dirs) {
        List<File> inputFiles = []
        ExcelHandler.constructInputFileList(dirs, inputFiles)
        loadExperimentsContext(loadedBy, inputFiles)
    }

    def loadExperimentsContext(String loadedBy, List<File> inputFiles) {
        def contextGroups = ContextGroupsBuilder.buildExperimentContextGroup()
        inputFiles.each{File file ->
            def dtos = ExcelHandler.buildDto(file, START_ROW, contextGroups, MAX_ROWS)
            AttributesContentsCleaner.cleanDtos(dtos)
            try{
                dtos.each{
                    loadExperimentContext(loadedBy, it)
                }
            } catch(Exception e){}
        }
    }

    def loadExperimentContext(String loadedBy, Dto dto) {
        Experiment experiment = getExperimentFromAid(dto.aid)
        if (!experiment){
            println("Found none or more than one experiment associated with aid: " + dto.aid)
            return
        }
        List<String> errorMessages = []
        dto.contextDTOs.each{ContextDTO contextDTO ->
            ExperimentContext experimentContext = updateContextInExperiment(experiment, contextDTO, loadedBy)
            contextDTO.contextItemDtoList.each{ContextItemDto contextItemDto ->
                ExperimentContextItem item = updateExperimentContextItem(experimentContext, contextItemDto, loadedBy, errorMessages)
                item.experimentContext = experimentContext
                experimentContext.addToExperimentContextItems(item)
            }
            experimentContext.experiment = experiment
            experiment.addToExperimentContexts(experimentContext)
        }
    }

    ExperimentContextItem updateExperimentContextItem(ExperimentContext experimentContext, ContextItemDto contextItemDto, String loadedBy, List<String> errorMessages) {
        Element attributeElement = Element.findByLabelIlike(contextItemDto.key)
        if (! attributeElement) {
            final String message = "Element not exist: (${contextItemDto.key})"
            errorMessages << message
        }
        ExperimentContextItem experimentContextItem = new ExperimentContextItem(attributeElement: attributeElement, modifiedBy: loadedBy)
        Element concentrationUnitsElement = contextItemDto.concentrationUnits ? Element.findByLabelIlike(contextItemDto.concentrationUnits) : null
        String concentrationUnitsAbbreviation = concentrationUnitsElement ? " ${concentrationUnitsElement.abbreviation}" : ""
        //populate attribute-value type and value
        Element valueElement = contextItemDto.value ? Element.findByLabelIlike(contextItemDto.value) : null

        //if the value string could be matched against an element then add it to the valueElement
        if (valueElement) {
            experimentContextItem.valueElement = valueElement
            experimentContextItem.valueDisplay = valueElement.label
        }
        //else, if the attribute's value is a number value, store it in the valueNum field
        else if (contextItemDto.value && (!(contextItemDto.value instanceof String) || contextItemDto.value.isNumber())) {
            Float val = new Float(contextItemDto.value)
            experimentContextItem.valueNum = val
            //If the value is a number and also has concentration-units, we need to find the units element ID and update the valueDisplay accrdingly
            experimentContextItem.valueDisplay = val.toString() + concentrationUnitsAbbreviation
        }
        //else, if the attribute is a numeric range (e.g., 440-460nm -> 440-460), then store it in valueMin, valueMax and make AttributeType=range.
        else if (contextItemDto.value && (contextItemDto.value instanceof String) && contextItemDto.value.matches(/^\d+\-\d+$/)) {
            final String[] rangeStringArray = contextItemDto.value.split("-")
            experimentContextItem.valueMin = new Float(rangeStringArray[0])
            experimentContextItem.valueMax = new Float(rangeStringArray[1])
            experimentContextItem.valueDisplay = contextItemDto.value + concentrationUnitsAbbreviation //range-units are reported separately.
        }
        //else, if the attribute's is a type-in or attribute-type is Free, then simply store it the valueDisplay field
        else if (contextItemDto.typeIn || (contextItemDto.attributeType == AttributeType.Free)) {
            experimentContextItem.valueDisplay = contextItemDto.value
        }
        else {
            final String message = "Value of context item not recognized as element or numerical value: '${contextItemDto.key}'/'${contextItemDto.value}'"
            errorMessages << message
        }

        //populate the qualifier field, if exists, and prefix the valueDisplay with it
        if (contextItemDto.qualifier) {
            experimentContextItem.qualifier = String.format('%-2s', contextItemDto.qualifier)
            experimentContextItem.valueDisplay = "${contextItemDto.qualifier}${experimentContextItem.valueDisplay}"
        }
        return experimentContextItem
    }

    ExperimentContext updateContextInExperiment(Experiment experiment, ContextDTO contextDTO, String loadedBy) {
        experiment.experimentContexts.each{
            if (it.contextName == contextDTO.name) {
                return it
            }
        }
        return new ExperimentContext(experiment: experiment, contextName: contextDTO.name, modifiedBy: loadedBy)
    }
    /**
     * One AID match to one Experiment
     * @param AID
     * @return
     */
    Experiment getExperimentFromAid(long AID) {
        def criteria = Experiment.createCriteria()
        List<Experiment> results = criteria.list {
            externalReferences {
                eq('extAssayRef', "aid=${AID.toString()}")
            }
        }
        return (results && (results.size() == 1)) ? results.first() : null
    }
}
