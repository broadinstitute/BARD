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
        table 'MolSSColSubHeader'
        maximumResponse column: "maxResponse"
        minimumResponse column: "minResponse"
        unitsInColumnAreUniform column: "unitsInColumnAreUniform"

    }
}
