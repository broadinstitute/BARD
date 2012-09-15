package bardqueryapi

class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    def index() {
        molecularSpreadSheet()
    }
     def molecularSpreadSheet(){
         MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
        render (view:"molecularSpreadSheet", MolSpreadSheetData:molSpreadSheetData )
    }

}
