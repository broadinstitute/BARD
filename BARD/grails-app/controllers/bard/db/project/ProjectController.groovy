package bard.db.project

class ProjectController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def show() {
        def projectInstance = Project.get(params.id)
        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            redirect(action: "list")
            return
        }
        [instance: projectInstance]
    }
}
