package bard.db.project

import bard.db.enums.ReadyForExtraction
import bard.db.model.AbstractContextOwner
import bard.db.registration.ExternalReference

class Project extends AbstractContextOwner {
    private static final int PROJECT_NAME_MAX_SIZE = 256
    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int DESCRIPTION_MAX_SIZE = 1000
    private static final int GROUP_TYPE_MAX_SIZE = 20

    String name
    String groupType
    String description
    ReadyForExtraction readyForExtraction = ReadyForExtraction.Pending

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    List<ProjectContext> contexts = [] as List
    Set<ProjectExperiment> projectExperiments = [] as Set

    Set<ExternalReference> externalReferences = [] as Set
    Set<ProjectDocument> documents = [] as Set

    static hasMany = [projectExperiments: ProjectExperiment,
            externalReferences: ExternalReference,
            contexts: ProjectContext,
            documents: ProjectDocument]

    static mapping = {
        id(column: "PROJECT_ID", generator: "sequence", params: [sequence: 'PROJECT_ID_SEQ'])
        name(column: "PROJECT_NAME")
        contexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    static constraints = {
        name(maxSize: PROJECT_NAME_MAX_SIZE, blank: false)
        // TODO make enum
        groupType(maxSize: GROUP_TYPE_MAX_SIZE, nullable: false, blank: false, inList: ['Project', 'Probe Report', 'Campaign', 'Panel', 'Study', 'Template'])
        description(nullable: true, blank: false, maxSize: DESCRIPTION_MAX_SIZE)
        readyForExtraction(maxSize: READY_FOR_EXTRACTION_MAX_SIZE, nullable: false)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    /**
     * for all the associated projectExperiments, using the spread dot operator
     * to get the following and preceding Sets of ProjectSteps.
     *
     * this results in a set of sets, flatten collapses this to 1 level, as we're
     * using a Set<ProjectStep> we should get just the distinct ProjectSteps
     * @return
     */
    Set<ProjectStep> getProjectSteps() {
        Set<ProjectStep> projectSteps = [] as Set
        projectSteps.addAll(this.projectExperiments*.followingProjectSteps)
        projectSteps.addAll(this.projectExperiments*.precedingProjectSteps)
        projectSteps.flatten()
    }

}
