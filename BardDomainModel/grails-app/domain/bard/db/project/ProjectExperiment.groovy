package bard.db.project

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/29/12
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectExperiment extends AbstractContextOwner{

    private static final int MODIFIED_BY_MAX_SIZE = 40

    Experiment experiment
    Project project
    Element stage

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    List<ProjectExperimentContext> projectExperimentContexts = []
    Set<ProjectStep> precedingProjectSteps = [] as Set
    Set<ProjectStep> followingProjectSteps = [] as Set

    static hasMany = [projectExperimentContexts: ProjectExperimentContext,
            precedingProjectSteps: ProjectStep,
            followingProjectSteps: ProjectStep]

    static mappedBy = [precedingProjectSteps: 'nextProjectExperiment',
            followingProjectSteps: 'previousProjectExperiment']

    static constraints = {
        experiment()
        project()
        stage(nullable: true)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        id(column: "PROJECT_EXPERIMENT_ID", generator: "sequence", params: [sequence: 'PROJECT_EXPERIMENT_ID_SEQ'])
        projectExperimentContexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    @Override
    List getContexts() {
        return projectExperimentContexts;
    }

    @Override
    void removeContext(AbstractContext context) {
        this.removeFromProjectExperimentContexts(context)
    }
}
