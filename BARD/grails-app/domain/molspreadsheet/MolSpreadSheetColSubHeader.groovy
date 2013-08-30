package molspreadsheet

class MolSpreadSheetColSubHeader {

    String columnTitle
    Boolean unitsInColumnAreUniform = true
    String unitsInColumn = null
    double minimumResponse = Double.NaN
    double maximumResponse = Double.NaN

    static constraints = {
    }
    static mapping = {
        table('MOL_SS_COL_SUB_HEADER')
        id(generator: 'sequence', params: [sequence: 'MOL_SS_COL_SUB_HEADER_ID_SEQ'])
        maximumResponse(column: "MAX_RESPONSE")
        minimumResponse(column: "MIN_RESPONSE")
        unitsInColumnAreUniform(column: "UNITS_IN_COLUMN_ARE_UNIFORM")

    }
}
