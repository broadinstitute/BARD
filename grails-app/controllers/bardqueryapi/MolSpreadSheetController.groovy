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
    def sortMe(){
        println 'foo'

        render (view:"molecularSpreadSheet", model:  [ molSpreadSheetData: molSpreadSheetData ])
    }

    def list = {

        if (params.sort == null) {
            params.sort = "name"
        }
//        def owner = User.findBySessionId( session.getId() )
//        def groupList = ApGroup.findAllByUser( owner ).sort{
//            a, b ->
//            if (params.order == 'desc') {
//                b."${params.sort}" <=> a."${params.sort}"
//            } else {
//                a."${params.sort}" <=> b."${params.sort}"
//            }
//        }
//        [groups: groupList ]
    }



}
