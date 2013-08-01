package barddataqa

class ProjectStatusController {

    ProjectStatusService projectStatusService

    def index() {
        List<ProjectStatus> projectStatusList = projectStatusService.retrieveAllProjectStatusWithMetaInfoSorted()
        [projectStatusList: projectStatusList, qaStatusList:  QaStatus.withCriteria({order("id")})]
    }

    def create(Long projectId, Long qaStatusId) {
        String errorMessage = projectStatusService.createNew(projectId, qaStatusId)

        if (errorMessage != null) {
            render(errorMessage)
        } else {
            redirect(action: 'index')
        }
    }

    def updateStatus(Long projectId, Long qaStatusId) {
        projectStatusService.updateQaStatus(projectId, qaStatusId)
        redirect(action: 'index')
    }

    def updateNotes(Long projectId, String notes) {
        if (notes.length() > 4000) {
            render("notes section must be 400 characters or less, it currently is:  ${notes.length()}")
        } else {
            projectStatusService.updateNotes(projectId, notes)
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

    def updateQueueOrder(Long projectId, String queueOrderString) {
        projectStatusService.updateQueueOrder(projectId, queueOrderString)
        redirect(action: 'index')
    }
}

class JiraIssueCommand {
    Long projectId

    List<Long> deleteJiraIssueList

    String addJiraIssue
}
