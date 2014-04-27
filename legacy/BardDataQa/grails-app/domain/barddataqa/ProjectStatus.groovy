package barddataqa

class ProjectStatus {

    QaStatus qaStatus

    String projectName

    String laboratoryName

    String notes

    Set<ProjectStatusJiraIssue> jiraIssueSet

    Double queueOrder

    ProjectStatus (Long id, QaStatus qaStatus) {
        this.id = id
        this.qaStatus = qaStatus
    }

    static constraints = {
        notes(nullable: true)
        queueOrder(nullable: true)
    }

    static mapping = {
        id generator: 'assigned'
    }

    static transients = ['projectName', 'laboratoryName']

    static hasMany = [
            jiraIssueSet: ProjectStatusJiraIssue
    ]

    static mappedBy = [
            jiraIssueSet: "projectStatus"
    ]
}


