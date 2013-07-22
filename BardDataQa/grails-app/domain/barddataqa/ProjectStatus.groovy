package barddataqa

class ProjectStatus {

    QaStatus qaStatus

    ProjectStatus (Long id, QaStatus qaStatus) {
        this.id = id
        this.qaStatus = qaStatus
    }

    static constraints = {
    }

    static mapping = {
        id generator: 'assigned'
    }
}
