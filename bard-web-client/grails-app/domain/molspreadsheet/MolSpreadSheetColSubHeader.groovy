package molspreadsheet

class MolSpreadSheetColSubHeader {

    String columnTitle
    Boolean unitsInColumnAreUniform = true
    String unitsInColumn = null

    static constraints = {
    }
    static mapping = {
        table 'MolSSColSubHeader'
        unitsInColumnAreUniform column: "unitsInColumnAreUniform"
    }
}
