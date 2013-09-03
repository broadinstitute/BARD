package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.registration.MeasureCaseInsensitiveDisplayLabelComparator

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


    Element resultType
    Element statsModifier

    ExperimentMeasure parent
    HierarchyType parentChildRelationship

    Experiment experiment

    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy

    Set<ExperimentMeasure> childMeasures = [] as Set
    Set<AssayContextExperimentMeasure> assayContextExperimentMeasures = [] as Set


    static belongsTo = [parent: ExperimentMeasure]
    static hasMany = [childMeasures: ExperimentMeasure, assayContextExperimentMeasures: AssayContextExperimentMeasure]

    static constraints = {
        parent(nullable: true)
        parentChildRelationship(nullable: true, maxSize: PARENT_CHILD_RELATIONSHIP_MAX_SIZE)
        experiment()
        resultType(nullable: true)
        statsModifier(nullable: true)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        table('EXPRMT_MEASURE')
        id(column: "EXPRMT_MEASURE_ID", generator: "sequence", params: [sequence: 'EXPRMT_MEASURE_ID_SEQ'])
        parent(column: 'PARENT_EXPRMT_MEASURE_ID')
        resultType(column: 'RESULT_TYPE_ID')
        statsModifier(column: 'STATS_MODIFIER_ID')

    }
    /**
     * @return childMeasures sorted by displayLabel case insensitive
     */
    List<ExperimentMeasure> getChildrenMeasuresSorted() {
        childMeasures.sort(new MeasureCaseInsensitiveDisplayLabelComparator())
    }
    /**
     * @return concatenation of resultType.label and statsModifier.label if statsModifier is defined otherwise just the resultType.label
     */
    String getDisplayLabel() {
        String statsModifierLabel = statsModifier ? "(${statsModifier.label})" : null
        if (resultType == null && statsModifier == null) {
            return ""
        }
        return [resultType.label, statsModifierLabel].findAll().join(' ')
    }
}