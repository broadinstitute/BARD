package bardqueryapi

import molspreadsheet.MolSpreadSheetData
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    def index() {
         render (view:"molecularSpreadSheet")
    }

    def molecularSpreadSheet(){
        if (molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()) {
            MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
            render (template: "spreadSheet", model:  [ molSpreadSheetData: molSpreadSheetData ])
        }  else {
            render (template: "spreadSheet", model:  [ molSpreadSheetData: new MolSpreadSheetData() ])
        }

    }

    def list = {

        if (params.sort == null) {
            params.sort = "name"
        }
    }



}
