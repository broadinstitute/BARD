package barddataqa

class ProjectStatusController {

    ProjectStatusService projectStatusService

    def index() {
        List<ProjectStatus> projectStatusList = projectStatusService.retrieveAllProjectStatusWithMetaInfoSorted()
        [projectStatusList: projectStatusList, qaStatusList:  QaStatus.withCriteria({order("id")})]
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
        if (projectStatusCommand.projectStatusNotes != null
                && projectStatusCommand.projectStatusNotes.length() > 4000) {

            render("notes section must be 400 characters or less, it currently is:  ${projectStatusCommand.projectStatusNotes.length()}")
        } else {
            projectStatusService.updateExisting(projectStatusCommand)
            redirect(action: 'index')
        }
    }

    def updateJiraIssues(JiraIssueCommand jiraIssueCommand) {
        if (jiraIssueCommand.addJiraIssue && jiraIssueCommand.addJiraIssue.length() > 20) {
            render("new jira issue must be 20 characters or less, it is currently:  ${jiraIssueCommand.addJiraIssue.length()}")
        } else {
            projectStatusService.updateJiraIssues(jiraIssueCommand)
            redirect(action: 'index')
        }
    }
}


class ProjectStatusCommand {
    Long projectId

    Long qaStatusId

    String projectStatusNotes
}


class JiraIssueCommand {
    Long projectId

    List<Long> deleteJiraIssueList

    String addJiraIssue
}
