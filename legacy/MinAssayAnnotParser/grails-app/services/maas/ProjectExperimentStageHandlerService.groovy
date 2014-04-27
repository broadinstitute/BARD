package maas

import bard.db.project.Project
import bard.db.experiment.Experiment
import bard.db.project.ProjectExperiment
import bard.db.dictionary.Element
import org.apache.commons.lang3.StringUtils

class ProjectExperimentStageHandlerService {
    def contextHandlerService = new ContextHandlerService()
    final int START_ROW = 2 //0-based
    final int MAX_ROWS = 4000

    def handle(String loadedBy, List<String> dirs, List<Long> mustLoadedAids) {
        List<File> inputFiles = []
        ExcelHandler.constructInputFileList(dirs, inputFiles)
        loadExperimentsContext(loadedBy, inputFiles, mustLoadedAids)
    }

    def loadExperimentsContext(String loadedBy, List<File> inputFiles, List<Long> mustLoadedAids) {
        def contextGroups = ContextGroupsBuilder.buildProjectExperimentStage()
        Map attributeNameMapping = ElementIdMapping.build()
        inputFiles.each {File file ->
            println("Processing file ${file.name}")
            def dtos = ExcelHandler.buildDto(file, START_ROW, contextGroups, MAX_ROWS)
            String currentModifiedBy = "${loadedBy}_${file.name}"
            if (currentModifiedBy.length() >= 40) {
                currentModifiedBy = currentModifiedBy.substring(0, 40)
            }
            AttributesContentsCleaner.cleanDtos(dtos, attributeNameMapping)
            try {
                dtos.each {
                    loadProjectExprimentStage(currentModifiedBy, it, mustLoadedAids)
                }
            } catch (Exception e) {
                println("Exception Happened during loading " + file.absolutePath + " " + e.message)
            }
        }
    }

    def loadProjectExprimentStage(String loadedBy, Dto dto, List<Long> mustLoadedAids) {
        if (!mustLoadedAids.contains(dto.aid)) // for 03/13 release, we don't care any aid not in this list
            return
        if (dto.aid == null)
            return
        Experiment experiment = contextHandlerService.getExperimentFromAid(dto.aid)
//        Project project = contextHandlerService.getProjectFromAid(dto.aid)
        if (!experiment){
            println("No experiment associated with aid: " + dto.aid)
            return
        }
        // find a project using experiment project association. there may have multiple projects associated with one experiment, use the first one

        ProjectExperiment pe = experiment.projectExperiments.size() == 0 ? null:experiment.projectExperiments?.iterator().next()
        if (!pe) {
            println("No project experiment associated with aid: " + dto.aid)
            return
        }

        dto.contextDTOs.each{ContextDTO contextDTO ->
            contextDTO.contextItemDtoList.each{ContextItemDto contextItemDto ->      // we only care about the value which is the stage
               String value = StringUtils.trim(contextItemDto.value)
               Element element = Element.findByLabelIlike(value)
                if (element){
                    pe.stage = element
                    pe.modifiedBy = loadedBy
                    println("Overide stage in aid: ${dto.aid}, projectexperiment: ${pe.id} with Element: ${element.id}")
                }
                else{
                    println("Not override stage in aid: ${dto.aid}, projectexperiment: ${pe.id}, due to element: ${value} not found in Elment table")
                }
            }
        }
    }
}
