package maas

import bard.db.project.Project
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem


class ProjectHandlerService {
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
        FileWriter logWriter = new FileWriter(dirs[0] + "/output/loadProjectContext.txt")
        loadProjectsContext(loadedBy, inputFiles, mustLoadedAids, logWriter)
    }

    def loadProjectsContext(String loadedBy, List<File> inputFiles, List<Long> mustLoadedAids, FileWriter logWriter) {
        def contextGroups = ContextGroupsBuilder.buildProjectContextGroup()
        Map attributeNameMapping = ElementIdMapping.build()
        inputFiles.each{File file ->
            def dtos = ExcelHandler.buildDto(file, START_ROW, contextGroups, MAX_ROWS)
            String currentModifiedBy = "${loadedBy}_${file.name}"
            if (currentModifiedBy.length() >= 40){
                currentModifiedBy = currentModifiedBy.substring(0,40)
            }
            AttributesContentsCleaner.cleanDtos(dtos, attributeNameMapping)
            try{
                dtos.each{
                        loadProjectContext(currentModifiedBy, it, mustLoadedAids, logWriter)
                }
            } catch(Exception e){
                writeToLog(logWriter, "Exception Happened during loading " + file.absolutePath + " " + e.message)
            }
        }
    }

    def loadProjectContext(String loadedBy, Dto dto, List<Long> mustLoadedAids, FileWriter logWriter) {
        if (!mustLoadedAids.contains(dto.aid)) // for 03/13 release, we don't care any aid not in this list
            return
        if (dto.aid == null)
            return
        Project project = contextHandlerService.getProjectFromAid(dto.aid)
        if (!project){
            writeToLog(logWriter,"No project associated with aid: " + dto.aid)
            return
        }

        List<String> errorMessages = []

        int newContextCnt = 0
        int newItemsCnt = 0
        int dupItemCnt = 0
        int dupContextCnt = 0
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
            boolean dupContext = isContextExist(project, projectContext)
            if (dupContext)
                dupContextCnt++
            if (projectContext.contextItems.size() > 0 && !projectContext.id && !dupContext) { // only add context if it is not associated with the project
                projectContext.project = project
                project.addToContexts(projectContext)
                newContextCnt++
            }
        }

        if (errorMessages.size() == 0) {
            if (!project.save(flush: true)) {
                writeToLog(logWriter, "Error Save project ${project.id} with aid ${dto.aid} in ${dto.sourceFile.name}: ${project.errors.toString()}")
                StringBuilder builder = new StringBuilder()
                for (ProjectContext context : project.contexts) {
                    for (ProjectContextItem item : context.contextItems) {
                        builder.append("""attributeElement label: ${item.attributeElement.label}, attributeElementId: ${item.attributeElement},
                               valueELement: ${item.valueElement}, externalValueId: ${item.extValueId}\n""")
                    }
                }
                writeToLog(logWriter,builder.toString())
            }
            else {
                writeToLog(logWriter, "Success Saved project ${project.id} with aid ${dto.aid}, #dup context ${dupContextCnt}, # new contexts ${newContextCnt}, # new ContextItem ${newItemsCnt}, # duplicate ContextItem ${dupItemCnt}")
            }
            project = Project.findById(project.id)
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
     */
    ProjectContext updateContextInProject(Project project, ContextDTO contextDTO, String loadedBy) {
//        def newProject = project.contexts.find {it?.contextName == contextDTO.name}
//        if (!newProject) {
           def newProject = new ProjectContext(project: project, contextName: contextDTO.name, modifiedBy: loadedBy)
//        }
        return newProject
    }

    /**
     * Check if this experiment already have same context existing based on experiment item inside of each context
     * @param experiment
     * @param experimentContext
     * @return
     */
    boolean isContextExist(Project project, ProjectContext projectContext){
        def contexWithSameName = project.contexts.find {it?.contextName == projectContext.contextName}
        if (!contexWithSameName)
            return false
        for (ProjectContext ec : project.contexts.findAll{it?.contextName == projectContext.contextName}) {
            if (contextHandlerService.isContextSame(ec, projectContext) == 0)
                return true
        }

        return false
    }
}
