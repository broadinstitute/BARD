package bardqueryapi

class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    def index() {
        molecularSpreadSheet()
    }

    def demo(){
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData("demo!")
        render (view:"molecularSpreadSheet", model:  [ molSpreadSheetData: molSpreadSheetData ])
    }

    def molecularSpreadSheet(){
         MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
        render (view:"molecularSpreadSheet", model:  [ molSpreadSheetData: molSpreadSheetData ])
    }

}
