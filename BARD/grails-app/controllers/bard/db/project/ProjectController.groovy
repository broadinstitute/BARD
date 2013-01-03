package bard.db.project

class ProjectController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    ProjectExperimentRenderService projectExperimentRenderService

    def show() {
        def projectInstance = Project.get(params.id)
        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            return
        }
        [instance: projectInstance, pexperiment:projectExperimentRenderService.contructGraph(projectInstance)]
    }

    def edit() {
        def projectInstance = Project.get(params.id)

        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            return
        }
        else
            flash.message = null

        [instance: projectInstance, pexperiment:projectExperimentRenderService.contructGraph(projectInstance)]
    }

    // TODO: use ajax call to get graph
    def ajaxPexpriments() {
        def projectid = params['projectid']
        def projectInstance = Project.get(params.id)
        projectExperimentRenderService.contructGraph(projectInstance)
    }
}

