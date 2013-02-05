package bard.db.dictionary

class UnitConversion implements Serializable {

    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int FORMULA_MAX_SIZE = 256

    Element fromUnit
    Element toUnit

    Float multiplier
    Float offset
    String formula

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static constraints = {
        fromUnit()
        toUnit()
        multiplier( nullable: true)
        offset( nullable: true)
        formula( nullable: true, blank: false, maxSize: FORMULA_MAX_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    static mapping = {
        id(column: 'UNIT_CONVERSION_ID', generator: 'sequence', params: [sequence: 'UNIT_CONVERSION_ID_SEQ'])
    }
}