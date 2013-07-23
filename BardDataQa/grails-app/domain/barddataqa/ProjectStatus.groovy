package barddataqa

class ProjectStatus {

    QaStatus qaStatus

    String projectName

    String laboratoryName

    ProjectStatus (Long id, QaStatus qaStatus) {
        this.id = id
        this.qaStatus = qaStatus
    }

    static constraints = {
    }

    static mapping = {
        id generator: 'assigned'
    }

    static transients = ['projectName', 'laboratoryName']
}


