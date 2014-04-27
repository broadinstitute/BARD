package barddataqa

class ProjectStatusJiraIssue {

    ProjectStatus projectStatus

    String jiraIssueId

    static constraints = {
        projectStatus(nullable: false)
        jiraIssueId(nullable: false)
    }

    static mapping = {
        id(generator: 'sequence', params: [sequence: 'proj_status_jira_issue_id_seq'])
        projectStatus(column: 'PROJECT_STATUS_ID')
    }
}
