package maas

import bard.db.project.Project
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem

class ProjectHandlerService {
    def contextHandlerService

    final int START_ROW = 2 //0-based
    final int MAX_ROWS = 80
    def handle(String loadedBy, List<String> dirs) {
        List<File> inputFiles = []
        ExcelHandler.constructInputFileList(dirs, inputFiles)
        loadProjectsContext(loadedBy, inputFiles)
    }

    def loadProjectsContext(String loadedBy, List<File> inputFiles) {
        def contextGroups = ContextGroupsBuilder.buildProjectContextGroup()
        inputFiles.each{File file ->
            def dtos = ExcelHandler.buildDto(file, START_ROW, contextGroups, MAX_ROWS)
            AttributesContentsCleaner.cleanDtos(dtos)
            try{
                dtos.each{
                    loadProjectContext(loadedBy, it)
                }
            } catch(Exception e){}
        }
    }

    def loadProjectContext(String loadedBy, Dto dto) {
        Project project = contextHandlerService.getProjectFromAid(dto.aid)
        if (!project){
            println("Found none or more than one project associated with aid: " + dto.aid)
            return
        }
        List<String> errorMessages = []
        dto.contextDTOs.each{ContextDTO contextDTO ->
            ProjectContext projectContext = updateContextInProject(project, contextDTO, loadedBy)
            contextDTO.contextItemDtoList.each{ContextItemDto contextItemDto ->
                ProjectContextItem item = contextHandlerService.updateContextItem(contextItemDto, loadedBy, errorMessages, "Project")
                item.context = projectContext
                projectContext.addToContextItems(item)
            }
            projectContext.project = project
            project.addToContexts(projectContext)
        }
        errorMessages.each{
            println(dto.aid + " " + dto.sourceFile.absolutePath + " " + it)
        }
        if (errorMessages.size() == 0) { // no errors
            project.save(flush: true)
        }
    }

    /**
     * If there is a context exist, return it, otherwise, create new one
     */
    ProjectContext updateContextInProject(Project project, ContextDTO contextDTO, String loadedBy) {
        project.contexts.each{
            if (it.contextName == contextDTO.name) {
                return it
            }
        }
        return new ProjectContext(Project: project, contextName: contextDTO.name, modifiedBy: loadedBy)
    }
}
