package molspreadsheet

class RetainSpreadsheetService {

    static scope = "session"
    MolSpreadSheetData molSpreadSheetData

    void addMolSpreadsheetData(MolSpreadSheetData molSpreadSheetData) {
        this.molSpreadSheetData  =  molSpreadSheetData
    }

    MolSpreadSheetData retrieveMolSpreadsheetData() {
        molSpreadSheetData
    }

    void clearMolSpreadsheetData() {
        this.molSpreadSheetData  =  null
    }



}
