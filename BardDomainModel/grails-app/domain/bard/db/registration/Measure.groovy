package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.ExperimentMeasure
import bard.db.enums.HierarchyType

//TODO: Mark for deletion
//TODO:First remove all constraint
class Measure {

    private static final int MODIFIED_BY_MAX_SIZE = 40

    Assay assay
    Element resultType
    Element statsModifier
    Measure parentMeasure
    HierarchyType parentChildRelationship
    Element entryUnit


    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    Set<Measure> childMeasures = [] as Set
    Set<ExperimentMeasure> experimentMeasures = [] as Set

    static belongsTo = [assay: Assay, parentMeasure: Measure]
    static hasMany = [childMeasures: Measure, experimentMeasures: ExperimentMeasure]

    static mapping = {
        id(column: 'MEASURE_ID', generator: 'sequence', params: [sequence: 'MEASURE_ID_SEQ'])
        resultType(column: 'RESULT_TYPE_ID')
        parentMeasure(column: "PARENT_MEASURE_ID")
        entryUnit(column: "ENTRY_UNIT_ID")
        statsModifier(column: 'STATS_MODIFIER_ID')
    }

    static constraints = {
        assay()
        resultType()
        parentMeasure(nullable: true)
        entryUnit(nullable: true)
        statsModifier(nullable: true)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
        parentChildRelationship(nullable: true,  maxSize: ExperimentMeasure.PARENT_CHILD_RELATIONSHIP_MAX_SIZE)

    }

    /**
     * @return concatenation of resultType.label and statsModifier.label if statsModifier is defined otherwise just the resultType.label
     */
    String getDisplayLabel() {
        String statsModifierLabel = statsModifier ? "(${statsModifier.label})" : null
        if(resultType==null && statsModifier==null){
            return ""
        }
        return [resultType.label, statsModifierLabel].findAll().join(' ')
    }
    /**
     * @return childMeasures sorted by displayLabel case insensitive
     */
    List<Measure> getChildrenMeasuresSorted() {
        childMeasures.sort(new MeasureCaseInsensitiveDisplayLabelComparator())
    }

    public Measure clone() {
        Measure newMeasure = new Measure(
                resultType: resultType,
                parentChildRelationship: parentChildRelationship,
                entryUnit: entryUnit,
                statsModifier: statsModifier,
                dateCreated: new Date())
        return newMeasure
    }
}
