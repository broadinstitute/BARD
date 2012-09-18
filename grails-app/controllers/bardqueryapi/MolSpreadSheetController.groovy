package bardqueryapi

class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    def index() {
        molecularSpreadSheet()
    }

    def demo(){        // fake data for now.
                       //  TODO: remove this option!
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData("demo!")
        render (view:"molecularSpreadSheet", model:  [ molSpreadSheetData: molSpreadSheetData ])
    }

    def molecularSpreadSheet(){
        if (molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()) {
            MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
            render (view:"molecularSpreadSheet", model:  [ molSpreadSheetData: molSpreadSheetData ])
        }  else
            render (view:"../bardWebInterface/index")
    }

    def sortMe(){
        println 'foo'
        render (view:"molecularSpreadSheet", model:  [ molSpreadSheetData: molSpreadSheetData ])
    }

    def list = {

        if (params.sort == null) {
            params.sort = "name"
        }
    }



}
