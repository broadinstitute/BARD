package maas

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.dictionary.Element
import org.springframework.transaction.support.DefaultTransactionStatus
import org.apache.commons.lang.StringUtils

class ExperimentHandlerService {
    def contextHandlerService = new ContextHandlerService()

    final int START_ROW = 2 //0-based
    final int MAX_ROWS = 4000


    def writeToLog(FileWriter fileWriter, String message) {
        println(message)
        fileWriter.write(message + "\n")
        fileWriter.flush()
    }
    def handle(String loadedBy, List<String> dirs, List<Long> mustLoadedAids) {
        List<File> inputFiles = []
        ExcelHandler.constructInputFileList(dirs, inputFiles)
        FileWriter logWriter = new FileWriter(dirs[0] + "/output/loadExperiementContext.txt")
        loadExperimentsContext(loadedBy, inputFiles, mustLoadedAids, logWriter)
    }

    def loadExperimentsContext(String loadedBy, List<File> inputFiles, List<Long> mustLoadedAids, FileWriter logWriter) {
        def contextGroups = ContextGroupsBuilder.buildExperimentContextGroup()
        Map attributeNameMapping = ElementIdMapping.build()
        inputFiles.each {File file ->
            def dtos = ExcelHandler.buildDto(file, START_ROW, contextGroups, MAX_ROWS)
            String currentModifiedBy = "${loadedBy}_${file.name}"
            if (currentModifiedBy.length() >= 40) {
                currentModifiedBy = currentModifiedBy.substring(0, 40)
            }
            AttributesContentsCleaner.cleanDtos(dtos, attributeNameMapping)
            try {
                dtos.each {
                    loadExperimentContext(currentModifiedBy, it, mustLoadedAids, logWriter)
                }
            } catch (Exception e) {
                writeToLog(logWriter, "Exception Happened during loading " + file.absolutePath + " " + e.message)
            }
        }
    }

    def loadExperimentContext(String loadedBy, Dto dto, List<Long> mustLoadedAids, FileWriter logWriter) {
        if (!mustLoadedAids.contains(dto.aid)) // for 03/13 release, we don't care any aid not in this list
            return
        if (dto.aid == null)
            return
        Experiment experiment = contextHandlerService.getExperimentFromAid(dto.aid)

        if (!experiment) {
            writeToLog(logWriter, "No experiment associated with aid: " + dto.aid)
            return
        }
        List<String> errorMessages = []  // keep errors during loading

        int newContextCnt = 0
        int newItemsCnt = 0
        int dupItemCnt = 0
        int dupContextCnt = 0
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
            boolean dupContext = isContextExist(experiment, experimentContext)
            if (dupContext)
                dupContextCnt++
            if (experimentContext.contextItems.size() > 0 && !experimentContext.id && !dupContext) { // only add context if it is not associated with the experiment
                experimentContext.experiment = experiment
                experiment.addToExperimentContexts(experimentContext)
                newContextCnt++
            }
        }

        if (errorMessages.size() == 0) {
            if (!experiment.save(flush: true)) {
                writeToLog(logWriter, "Error Save experiment ${experiment.id} with aid ${dto.aid}: ${experiment.errors.toString()}")
            }
            else {
                writeToLog(logWriter, "Success Saved expriment ${experiment.id} with aid ${dto.aid}, #dup context ${dupContextCnt}, # new contexts ${newContextCnt}, # new ContextItem ${newItemsCnt}, # duplicate ContextItem ${dupItemCnt}")
            }
            experiment = Experiment.findById(experiment.id)
        }
        else {
            writeToLog(logWriter, "Error Parse ${dto.aid} : ${errorMessages.size()} errors for aid: ${dto.aid}")
            errorMessages.each {
                writeToLog(logWriter, "Error details: ${dto.aid}, ${dto.rowNum}, ${dto.sourceFile.name}, ${it}")
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
//        def newExp = experiment.experimentContexts.find {it?.contextName == contextDTO.name}
//        if (!newExp) {
            def newExp = new ExperimentContext(experiment: experiment, contextName: contextDTO.name, modifiedBy: loadedBy)
//        }
        return newExp
    }

    /**
     * Check if this experiment already have same context existing based on experiment item inside of each context
     * @param experiment
     * @param experimentContext
     * @return
     */
    boolean isContextExist(Experiment experiment, ExperimentContext experimentContext){
        for (ExperimentContext ec : experiment.experimentContexts) {
            if (contextHandlerService.isContextSame(ec, experimentContext) == 0)
                return true
        }
        return false
    }
}
