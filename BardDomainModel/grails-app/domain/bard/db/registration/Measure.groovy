package bard.db.registration

import bard.db.dictionary.Element
import bard.db.dictionary.Unit

class Measure {

    static expose = 'measure'

    Date dateCreated
    Date lastUpdated
    String modifiedBy
    MeasureContext measureContext
    Unit entryUnit
    Element element
    Assay assay
    Measure parentMeasure

    static belongsTo = [Assay]
    static hasMany = [children: Measure]

    static mapping = {
        id column: "Measure_ID", generator: "assigned"
        parentMeasure column: "PARENT_MEASURE_ID"
        entryUnit column: "entry_unit"
        element column: 'result_type_id'
    }

    static constraints = {
        parentMeasure nullable: true
        dateCreated maxSize: 19
        lastUpdated nullable: true, maxSize: 19
        modifiedBy nullable: true, maxSize: 40
        element nullable: false
        measureContext nullable: false
        entryUnit nullable: true
    }
}
