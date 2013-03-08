package maas

import bard.db.project.Project
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem

class ProjectHandlerService {
    def contextHandlerService

    final int START_ROW = 2 //0-based
    final int MAX_ROWS = 131

    def handle(String loadedBy, List<String> dirs, List<Long> mustLoadedAids) {
        List<File> inputFiles = []
        ExcelHandler.constructInputFileList(dirs, inputFiles)
        loadProjectsContext(loadedBy, inputFiles, mustLoadedAids)
    }

    def loadProjectsContext(String loadedBy, List<File> inputFiles, List<Long> mustLoadedAids) {
        def contextGroups = ContextGroupsBuilder.buildProjectContextGroup()
        inputFiles.each{File file ->
            def dtos = ExcelHandler.buildDto(file, START_ROW, contextGroups, MAX_ROWS)
            AttributesContentsCleaner.cleanDtos(dtos)
            try{
                dtos.each{
                        loadProjectContext(loadedBy+"-maas-", it, mustLoadedAids)
                }
            } catch(Exception e){
                println("During loading " + file.absolutePath + " " + e.message)
                e.printStackTrace()
            }
        }
    }

    def loadProjectContext(String loadedBy, Dto dto, List<Long> mustLoadedAids) {
        if (!mustLoadedAids.contains(dto.aid)) // for 03/13 release, we don't care any aid not in this list
            return
        if (dto.aid == null)
            return
        Project project = contextHandlerService.getProjectFromAid(dto.aid)
        if (!project){
            println("Found none or more than one project associated with aid: " + dto.aid)
            return
        }
        println("loading " + dto.aid + " " + dto.sourceFile.absolutePath)
        deleteExistingContext(project)
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
        try{
        if (errorMessages.size() == 0) { // no error
            project.save(flush: true)
        }
        }catch(Exception e) {
            println("in save: " + dto.aid + " " + dto.sourceFile.absolutePath + " " + e.message)
            throw e
        }
        println("finishing " + dto.aid + " " + dto.sourceFile.absolutePath)
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

    void deleteExistingContext(Project project) {
        project.contexts.each{ProjectContext context->
            project.removeFromContexts(context)
        }
    }
}
