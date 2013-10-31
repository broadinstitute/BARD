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
abstract class ProjectExperiment {

    private static final int MODIFIED_BY_MAX_SIZE = 40

    Project project
    Element stage

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    Set<ProjectStep> precedingProjectSteps = [] as Set
    Set<ProjectStep> followingProjectSteps = [] as Set

    static hasMany = [
            precedingProjectSteps: ProjectStep,
            followingProjectSteps: ProjectStep]

    static mappedBy = [precedingProjectSteps: 'nextProjectExperiment',
            followingProjectSteps: 'previousProjectExperiment']

    static constraints = {
        project()
        stage(nullable: true)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    static mapping = {
        id(column: "PROJECT_EXPERIMENT_ID", generator: "sequence", params: [sequence: 'PROJECT_EXPERIMENT_ID_SEQ'])
        discriminator column: "type"
    }
}
