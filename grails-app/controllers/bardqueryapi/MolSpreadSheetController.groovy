package bardqueryapi

import molspreadsheet.MolSpreadSheetData

class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    def index() {
        molecularSpreadSheet()
    }

    def molecularSpreadSheet(){
        if (molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()) {
            MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
            render (view:"molecularSpreadSheet", model:  [ molSpreadSheetData: molSpreadSheetData ])
        }  else {
            flash.message = "Cannot display molecular spreadsheet without at least one assay and at least one compound"
            render (view:"../bardWebInterface/index")
        }

    }

    def list = {

        if (params.sort == null) {
            params.sort = "name"
        }
    }



}
