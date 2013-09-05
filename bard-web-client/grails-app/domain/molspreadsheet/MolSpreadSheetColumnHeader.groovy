package molspreadsheet

class MolSpreadSheetColumnHeader {

    List<MolSpreadSheetColSubHeader> molSpreadSheetColSubHeaderList  = []

    static constraints = {
    }
    static mapping = {
        molSpreadSheetColSubHeaderList column: "molSSColSubHeaderList"
    }
}
