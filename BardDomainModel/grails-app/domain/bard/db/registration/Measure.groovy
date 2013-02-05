package bard.db.registration

import bard.db.dictionary.Element

class Measure {

    private static final int MODIFIED_BY_MAX_SIZE = 40

    Assay assay
    Element resultType
    Measure parentMeasure
    Element entryUnit
    Element statsModifier

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    Set<Measure> childMeasures = [] as Set
    Set<AssayContextMeasure> assayContextMeasures = [] as Set

    static belongsTo = [assay: Assay, parentMeasure: Measure]
    static hasMany = [childMeasures: Measure, assayContextMeasures: AssayContextMeasure]

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
    }

    /**
     * @return concatenation of resultType.label and statsModifier.label if statsModifier is defined otherwise just the resultType.label
     */
    String getDisplayLabel(){
        String statsModifierLabel = statsModifier? "(${statsModifier.label})" : null
        return [resultType.label, statsModifierLabel].findAll().join(' ')
    }

    /**
     * @return childMeasures sorted by displayLabel case insensitive
     */
    List<Measure> getChildrenMeasuresSorted(){
        childMeasures.sort(new MeasureCaseInsensitiveDisplayLabelComparator())
    }
}
