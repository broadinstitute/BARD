package maas

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.dictionary.Element
import org.springframework.transaction.support.DefaultTransactionStatus

class ExperimentHandlerService {
    def contextHandlerService = new ContextHandlerService()

    final int START_ROW = 2 //0-based
    final int MAX_ROWS = 1000

    def handle(String loadedBy, List<String> dirs, List<Long> mustLoadedAids) {
        List<File> inputFiles = []
        ExcelHandler.constructInputFileList(dirs, inputFiles)
        loadExperimentsContext(loadedBy, inputFiles, mustLoadedAids)
    }

    def loadExperimentsContext(String loadedBy, List<File> inputFiles, List<Long> mustLoadedAids) {
        def contextGroups = ContextGroupsBuilder.buildExperimentContextGroup()
        inputFiles.each {File file ->
            def dtos = ExcelHandler.buildDto(file, START_ROW, contextGroups, MAX_ROWS)
            String currentModifiedBy = "${loadedBy}_${file.name}"
            if (currentModifiedBy.length() >= 40) {
                currentModifiedBy = currentModifiedBy.substring(0, 40)
            }
            AttributesContentsCleaner.cleanDtos(dtos)
            try {
                dtos.each {
                    loadExperimentContext(currentModifiedBy, it, mustLoadedAids)
                }
            } catch (Exception e) {
                println("Exception Happened during loading " + file.absolutePath + " " + e.message)
            }
        }
    }

    def loadExperimentContext(String loadedBy, Dto dto, List<Long> mustLoadedAids) {
        if (!mustLoadedAids.contains(dto.aid)) // for 03/13 release, we don't care any aid not in this list
            return
        if (dto.aid == null)
            return
        Experiment experiment = contextHandlerService.getExperimentFromAid(dto.aid)

        if (!experiment) {
            println("No experiment associated with aid: " + dto.aid)
            return
        }
        List<String> errorMessages = []  // keep errors during loading

        int newContextCnt = 0
        int newItemsCnt = 0
        int dupItemCnt = 0
        dto.contextDTOs.each {ContextDTO contextDTO ->
            ExperimentContext experimentContext = updateContextInExperiment(experiment, contextDTO, loadedBy)
            contextDTO.contextItemDtoList.each {ContextItemDto contextItemDto ->
                ExperimentContextItem item = contextHandlerService.updateContextItem(contextItemDto, loadedBy, errorMessages, "Experiment")
                if (!contextHandlerService.isContextItemExist(experimentContext, item)) { // only add item if there is no one same with existing one
                    item.experimentContext = experimentContext
                    experimentContext.addToExperimentContextItems(item)
                    newItemsCnt++
                } else {
                    dupItemCnt++
                }
            }
            if (experimentContext.contextItems.size() > 0 && !experimentContext.id) { // only add context if it is not associated with the experiment
                experimentContext.experiment = experiment
                experiment.addToExperimentContexts(experimentContext)
                newContextCnt++
            }
        }

        if (errorMessages.size() == 0) {
            if (!experiment.save(flush: true)) {
                println("Error Save experiment ${experiment.id} with aid ${dto.aid}: ${experiment.errors.toString()}")
            }
            else {
                println("Success Saved expriment ${experiment.id} with aid ${dto.aid}, # new contexts ${newContextCnt}, # new ContextItem ${newItemsCnt}, # duplicate ContextItem ${dupItemCnt}")
            }
            experiment = Experiment.findById(experiment.id)
        }
        else {
            println("Error Parse ${dto.aid} : ${errorMessages.size()} errors for aid: ${dto.aid}")
            errorMessages.each {
                println("Error details: ${dto.aid}, ${dto.rowNum}, ${dto.sourceFile.name}, ${it}")
            }
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
        def newExp = experiment.experimentContexts.find {it?.contextName == contextDTO.name}
        if (!newExp) {
            newExp = new ExperimentContext(experiment: experiment, contextName: contextDTO.name, modifiedBy: loadedBy)
        }
        return newExp
    }
}
