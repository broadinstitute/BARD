package maas

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.dictionary.Element

class ExperimentHandlerService {
    def contextHandlerService

    final int START_ROW = 2 //0-based
    final int MAX_ROWS = 131

    def handle(String loadedBy, List<String> dirs, List<Long> mustLoadedAids) {
        List<File> inputFiles = []
        ExcelHandler.constructInputFileList(dirs, inputFiles)
        loadExperimentsContext(loadedBy, inputFiles)
    }

    def loadExperimentsContext(String loadedBy, List<File> inputFiles,  List<Long> mustLoadedAids) {
        def contextGroups = ContextGroupsBuilder.buildExperimentContextGroup()
        inputFiles.each{File file ->
            def dtos = ExcelHandler.buildDto(file, START_ROW, contextGroups, MAX_ROWS)
            AttributesContentsCleaner.cleanDtos(dtos)
            try{
                dtos.each{
                    loadExperimentContext(loadedBy+"-maas-", it, mustLoadedAids)
                }
            } catch(Exception e){}
        }
    }

    def loadExperimentContext(String loadedBy, Dto dto,  List<Long> mustLoadedAids) {
        if (!mustLoadedAids.contains(dto.aid)) // for 03/13 release, we don't care any aid not in this list
            return
        if (dto.aid == null)
            return
        Experiment experiment = contextHandlerService.getExperimentFromAid(dto.aid)
        if (!experiment){
            println("Found none or more than one experiment associated with aid: " + dto.aid)
            return
        }
        List<String> errorMessages = []
        /* a way to handle creating elements during load */
        List<Element> elementCreatedDuringLoad = []
        dto.contextDTOs.each{ContextDTO contextDTO ->
            ExperimentContext experimentContext = updateContextInExperiment(experiment, contextDTO, loadedBy)
            contextDTO.contextItemDtoList.each{ContextItemDto contextItemDto ->
                ExperimentContextItem item = contextHandlerService.updateContextItem(contextItemDto, loadedBy, errorMessages, "Experiment", elementCreatedDuringLoad)
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

    void deleteExistingExperimentContext(Experiment experiment) {
        experiment.experimentContexts.each{
            experiment.removeFromExperimentContexts(it)
        }
    }
}
