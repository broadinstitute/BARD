package bard.db.experiment

import bard.db.registration.Measure
import bard.db.enums.HierarchyType

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/30/12
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentMeasure {

    private static final int MODIFIED_BY_MAX_SIZE = 40
    public static final int PARENT_CHILD_RELATIONSHIP_MAX_SIZE = 20

    ExperimentMeasure parent
    HierarchyType parentChildRelationship

    Experiment experiment
    Measure measure

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    Set<ExperimentMeasure> childMeasures = [] as Set

    static belongsTo = [parent: ExperimentMeasure]
    static hasMany = [childMeasures: ExperimentMeasure]

    static constraints = {
        parent(nullable: true)
        parentChildRelationship(nullable: true)
        experiment()
        measure()

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        table('EXPRMT_MEASURE')
        id(column: "EXPRMT_MEASURE_ID", generator: "sequence", params: [sequence: 'EXPRMT_MEASURE_ID_SEQ'])
        parent(column: 'PARENT_EXPRMT_MEASURE_ID')
    }
}