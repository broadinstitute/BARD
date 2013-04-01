package maas

import bard.db.project.Project
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem

class ProjectHandlerService {
    def contextHandlerService = new ContextHandlerService()

    final int START_ROW = 2 //0-based
    final int MAX_ROWS = 4000

    def handle(String loadedBy, List<String> dirs, List<Long> mustLoadedAids) {
        List<File> inputFiles = []
        ExcelHandler.constructInputFileList(dirs, inputFiles)
        loadProjectsContext(loadedBy, inputFiles, mustLoadedAids)
    }

    def loadProjectsContext(String loadedBy, List<File> inputFiles, List<Long> mustLoadedAids) {
        def contextGroups = ContextGroupsBuilder.buildProjectContextGroup()
        inputFiles.each{File file ->
            def dtos = ExcelHandler.buildDto(file, START_ROW, contextGroups, MAX_ROWS)
            String currentModifiedBy = "${loadedBy}_${file.name}"
            if (currentModifiedBy.length() >= 40){
                currentModifiedBy = currentModifiedBy.substring(0,40)
            }
            AttributesContentsCleaner.cleanDtos(dtos)
            try{
                dtos.each{
                        loadProjectContext(currentModifiedBy, it, mustLoadedAids)
                }
            } catch(Exception e){
                println("Exception Happened during loading " + file.absolutePath + " " + e.message)
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
            println("No project associated with aid: " + dto.aid)
            return
        }

        List<String> errorMessages = []

        int newContextCnt = 0
        int newItemsCnt = 0
        int dupItemCnt = 0
        dto.contextDTOs.each{ContextDTO contextDTO ->
            ProjectContext projectContext = updateContextInProject(project, contextDTO, loadedBy)
            contextDTO.contextItemDtoList.each{ContextItemDto contextItemDto ->
                ProjectContextItem item = contextHandlerService.updateContextItem(contextItemDto, loadedBy, errorMessages, "Project")
                if (!contextHandlerService.isContextItemExist(projectContext, item))  { // only add item if there is no one same with existing one
                    item.context = projectContext
                    projectContext.addToContextItems(item)
                    newItemsCnt++
                } else {
                    dupItemCnt++
                }
            }
            if (projectContext.contextItems.size() > 0 && !projectContext.id) { // only add context if it is not associated with the project
                projectContext.project = project
                project.addToContexts(projectContext)
                newContextCnt++
            }
        }

        if (errorMessages.size() == 0) {
            if (!project.save(flush: true)) {
                println("Error Save project ${project.id} with aid ${dto.aid}: ${project.errors.toString()}")
            }
            else {
                println("Success Saved project ${project.id} with aid ${dto.aid}, # new contexts ${newContextCnt}, # new ContextItem ${newItemsCnt}, # duplicate ContextItem ${dupItemCnt}")
            }
            project = Project.findById(project.id)
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
     */
    ProjectContext updateContextInProject(Project project, ContextDTO contextDTO, String loadedBy) {
        def newProject = project.contexts.find {it?.contextName == contextDTO.name}
        if (!newProject) {
            newProject = new ProjectContext(project: project, contextName: contextDTO.name, modifiedBy: loadedBy)
        }
        return newProject
    }
}
