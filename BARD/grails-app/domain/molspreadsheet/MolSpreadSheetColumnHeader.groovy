package molspreadsheet

class MolSpreadSheetColumnHeader {

    List<MolSpreadSheetColSubHeader> molSpreadSheetColSubHeaderList  = []

    static constraints = {
    }
    static mapping = {
        table(name:'MOL_SS_COL_HEADER')
        id(generator: 'sequence', params: [sequence: 'MOL_SS_COL_HEADER_ID_SEQ'])
        molSpreadSheetColSubHeaderList column: "MOL_SS_COL_SUB_HEADER_LIST"
    }
}
