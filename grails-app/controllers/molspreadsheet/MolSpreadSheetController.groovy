package molspreadsheet

import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    def index() {
         render (view:'molecularSpreadSheet')
    }
    def showExperimentDetails(Long pid,Long cid){
        //TODO: do implementation here. This is just a stub
        render (view:'molecularSpreadSheet')
    }
    def molecularSpreadSheet(){
        if (molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()) {
            MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
            render (template: 'spreadSheet', model:  [ molSpreadSheetData: molSpreadSheetData ])
        }  else {
            render (template: 'spreadSheet', model:  [ molSpreadSheetData: new MolSpreadSheetData() ])
        }

    }

    def list = {
        // eventually we will perform a sort here and then return something useful, but for now we redirect
        redirect(action: 'index')
    }



}
