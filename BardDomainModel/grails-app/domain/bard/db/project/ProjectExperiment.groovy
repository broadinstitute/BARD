package bard.db.project

import bard.db.dictionary.Element
import bard.db.experiment.Experiment

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/29/12
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectExperiment {

    private static final int MODIFIED_BY_MAX_SIZE = 40

    Experiment experiment
    Project project
    Element stage

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    List<ProjectExperimentContext> projectExperimentContexts = []

    static hasMany = [projectExperimentContexts: ProjectExperimentContext]

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
}
