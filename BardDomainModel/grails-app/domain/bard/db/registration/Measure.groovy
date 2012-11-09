package bard.db.registration

import bard.db.dictionary.Element

class Measure {

    private static final int MODIFIED_BY_MAX_SIZE = 40

    Assay assay
    Element resultTypeElement
    Measure parentMeasure
    Element entryUnitElement

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    Set<Measure> childMeasures = [] as Set
    Set<AssayContextMeasure> assayContextMeasures = [] as Set

    static belongsTo = [assay: Assay]
    static hasMany = [childMeasures: Measure, assayContextMeasures: AssayContextMeasure]

    static mapping = {
        id(column: 'MEASURE_ID', generator: 'sequence', params: [sequence: 'MEASURE_ID_SEQ'])
        parentMeasure(column: "PARENT_MEASURE_ID")
        entryUnitElement(column: "entry_unit")
        resultTypeElement(column: 'result_type_id')
    }

    static constraints = {
        assay()
        resultTypeElement()
        parentMeasure(nullable: true)
        entryUnitElement(nullable: true)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}
