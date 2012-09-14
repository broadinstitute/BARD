package bardqueryapi

class MolSpreadSheetController {

    def index() {
        molecularSpreadSheet()
    }
     def molecularSpreadSheet(){
        render (view:"molecularSpreadSheet" )
    }

}
