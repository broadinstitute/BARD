package barddataqa

class ProjectStatusController {

    ProjectStatusService projectStatusService

    def index() {
        [projectStatusList: ProjectStatus.withCriteria({order("id")}), qaStatusList:  QaStatus.withCriteria({order("id")})]
    }

    def create(ProjectStatusCommand projectStatusCommand) {
        String errorMessage = projectStatusService.createNew(projectStatusCommand)

        if (errorMessage != null) {
            render(errorMessage)
        } else {
            redirect(action: 'index')
        }
    }

    def update(ProjectStatusCommand projectStatusCommand) {
        projectStatusService.updateExisting(projectStatusCommand)

        redirect(action: 'index')
    }
}


class ProjectStatusCommand {
    Long projectId

    Long qaStatusId
}
