package bard.db.project

import bard.db.registration.ExternalReference
import bard.db.enums.ReadyForExtraction

class Project {
    private static final int PROJECT_NAME_MAX_SIZE = 256
    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int DESCRIPTION_MAX_SIZE = 1000
    private static final int GROUP_TYPE_MAX_SIZE = 20

    String projectName
    String groupType
    String description
    ReadyForExtraction readyForExtraction = ReadyForExtraction.Pending

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    List<ProjectContext> projectContexts = [] as List
    Set<ProjectStep> projectSteps = [] as Set
    Set<ExternalReference> externalReferences = [] as Set
    Set<ProjectDocument> projectDocuments = [] as Set

    static hasMany = [projectSteps: ProjectStep,
            externalReferences: ExternalReference,
            projectContexts:ProjectContext,
            projectDocuments: ProjectDocument]

    static mapping = {
        id(column: "PROJECT_ID", generator: "sequence", params: [sequence: 'PROJECT_ID_SEQ'])
        projectContexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    static constraints = {
        projectName( maxSize: PROJECT_NAME_MAX_SIZE, blank: false)
        // TODO make enum
        groupType( maxSize: GROUP_TYPE_MAX_SIZE, nullable:false, blank: false, inList: ['Project', 'Probe Report', 'Campaign', 'Panel', 'Study', 'Template'])
        description(nullable: true, blank: false , maxSize: DESCRIPTION_MAX_SIZE)
        readyForExtraction(maxSize: READY_FOR_EXTRACTION_MAX_SIZE, nullable: false)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}