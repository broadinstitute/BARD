package bard.db.project

import bard.db.enums.AssayStatus
import bard.db.enums.DocumentType
import bard.db.enums.ExperimentStatus
import bard.db.enums.ProjectGroupType
import bard.db.enums.ProjectStatus
import bard.db.enums.ReadyForExtraction
import bard.db.enums.hibernate.ProjectGroupTypeEnumUserType
import bard.db.enums.hibernate.ReadyForExtractionEnumUserType
import bard.db.experiment.Experiment
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceAware
import bard.db.guidance.owner.MinimumOfOneBiologyGuidanceRule
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.people.Role
import bard.db.registration.ExternalReference
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class Project extends AbstractContextOwner implements GuidanceAware {
    public static final int PROJECT_NAME_MAX_SIZE = 256
    public static final int MODIFIED_BY_MAX_SIZE = 40
    public static final int DESCRIPTION_MAX_SIZE = 1000
    def capPermissionService

    String name
    ProjectGroupType groupType = ProjectGroupType.PROJECT
    String description
    ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY
    ProjectStatus projectStatus = ProjectStatus.DRAFT
    Long ncgcWarehouseId;

    Date dateCreated
    Date lastUpdated = new Date()
    String modifiedBy

    List<ProjectContext> contexts = [] as List
    Set<ProjectExperiment> projectExperiments = [] as Set

    Set<ExternalReference> externalReferences = [] as Set
    Set<ProjectDocument> documents = [] as Set

    // if this is set, then don't automatically update readyForExtraction when this entity is dirty
    // this is needed to change the value to anything except "Ready"
    boolean disableUpdateReadyForExtraction = false
    Role ownerRole //The team that owns this object. This is used by the ACL to allow edits etc
    static belongsTo = [ownerRole: Role]


    static transients = ['disableUpdateReadyForExtraction', 'associatedExperiments', 'findUnApprovedExperiments']

    Set<Experiment> findUnApprovedExperiments() {
        Set<Experiment> unApprovedExperiments = new HashSet<Experiment>()
        projectExperiments.each { ProjectSingleExperiment projectExperiment ->
            final Experiment experiment = projectExperiment.experiment
            if (experiment.experimentStatus != ExperimentStatus.APPROVED && experiment.experimentStatus != ExperimentStatus.RETIRED) {
                unApprovedExperiments.add(experiment)
            }
        }
        return unApprovedExperiments
    }

    Set<Experiment> getAssociatedExperiments() {
        Set<Experiment> experiments = new HashSet<Experiment>()
        //We assume that everything is a single experiment
        for (ProjectSingleExperiment projectExperiment : projectExperiments) {
            experiments.add(projectExperiment.experiment)
        }
        return experiments
    }

    static hasMany = [projectExperiments: ProjectExperiment,
            externalReferences: ExternalReference,
            contexts: ProjectContext,
            documents: ProjectDocument]

    static mapping = {
        id(column: "PROJECT_ID", generator: "sequence", params: [sequence: 'PROJECT_ID_SEQ'])
        name(column: "PROJECT_NAME")
        readyForExtraction(type: ReadyForExtractionEnumUserType)
        groupType(type: ProjectGroupTypeEnumUserType)
        contexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    static constraints = {
        name(maxSize: PROJECT_NAME_MAX_SIZE, blank: false)
        groupType(nullable: false)
        description(nullable: true, blank: false, maxSize: DESCRIPTION_MAX_SIZE)
        readyForExtraction(nullable: false)
        lastUpdated(nullable: false)
        ncgcWarehouseId(nullable: true)
        dateCreated(nullable: false)
        ownerRole(nullable: false)
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

    List<ProjectDocument> getPublications() {
        final List<ProjectDocument> documents = documents.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_PUBLICATION } as List<ProjectDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ProjectDocument> getExternalURLs() {
        final List<ProjectDocument> documents = documents.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_EXTERNAL_URL } as List<ProjectDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ProjectDocument> getComments() {
        final List<ProjectDocument> documents = documents.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_COMMENTS } as List<ProjectDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ProjectDocument> getDescriptions() {
        final List<ProjectDocument> documents = documents.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_DESCRIPTION } as List<ProjectDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ProjectDocument> getProtocols() {
        final List<ProjectDocument> documents = documents.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_PROTOCOL } as List<ProjectDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ProjectDocument> getOtherDocuments() {
        final List<ProjectDocument> documents = documents.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_OTHER } as List<ProjectDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    @Override
    void removeContext(AbstractContext context) {
        this.removeFromContexts(context)
    }

    @Override
    AbstractContext createContext(Map properties) {
        ProjectContext context = new ProjectContext(properties)
        addToContexts(context)
        return context
    }

    def addToProjectExperiments(ProjectExperiment projectExperiment) {
        this.projectExperiments.add(projectExperiment)
        projectExperiment.project = this
    }

    def afterInsert() {
        Project.withNewSession {
            capPermissionService?.addPermission(this)
        }
    }

    String getOwner() {
        final String objectOwner = this.ownerRole?.displayName
        return objectOwner
    }

    @Override
    List<Guidance> getGuidance() {
        final List<Guidance> guidanceList = []
        guidanceList.addAll(new MinimumOfOneBiologyGuidanceRule(this).getGuidance())
        guidanceList
    }
    public boolean permittedToSeeEntity() {
        if ((projectStatus == ProjectStatus.DRAFT) &&
                (!SpringSecurityUtils.ifAnyGranted('ROLE_BARD_ADMINISTRATOR') &&
                        !SpringSecurityUtils.principalAuthorities.contains(this.ownerRole))) {
            return false
        }
        return true
    }
}
