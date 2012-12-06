package bard.db.project

import bard.db.enums.ReadyForExtraction
import bard.db.registration.ExternalReference

class ProjectController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def show() {
        def projectInstance = Project.get(params.id)
        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            return
        }
        [instance: projectInstance]
    }
}

