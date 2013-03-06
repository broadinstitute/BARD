package maas

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem

class ExperimentHandlerService {
    def contextHandlerService

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
        Experiment experiment = contextHandlerService.getExperimentFromAid(dto.aid)
        if (!experiment){
            println("Found none or more than one experiment associated with aid: " + dto.aid)
            return
        }
        List<String> errorMessages = []
        dto.contextDTOs.each{ContextDTO contextDTO ->
            ExperimentContext experimentContext = updateContextInExperiment(experiment, contextDTO, loadedBy)
            contextDTO.contextItemDtoList.each{ContextItemDto contextItemDto ->
                ExperimentContextItem item = contextHandlerService.updateContextItem(contextItemDto, loadedBy, errorMessages, "Experiment")
                item.experimentContext = experimentContext
                experimentContext.addToExperimentContextItems(item)
            }
            experimentContext.experiment = experiment
            experiment.addToExperimentContexts(experimentContext)
        }
        errorMessages.each{
            println(dto.aid + " " + dto.sourceFile.absolutePath + " " + it)
        }
        if (errorMessages.size() == 0) { // no errors
            experiment.save(flush: true)
        }
    }

    /**
     * If there is a context exist, return it, otherwise, create new one
     *
     * @param experiment
     * @param contextDTO
     * @param loadedBy
     * @return
     */
    ExperimentContext updateContextInExperiment(Experiment experiment, ContextDTO contextDTO, String loadedBy) {
        experiment.experimentContexts.each{
            if (it.contextName == contextDTO.name) {
                return it
            }
        }
        return new ExperimentContext(experiment: experiment, contextName: contextDTO.name, modifiedBy: loadedBy)
    }
}
