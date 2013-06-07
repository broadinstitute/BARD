package barddataqa

class AidSpreadsheetController {

    AidSpreadsheetService aidSpreadsheetService
    def index() {
        return [rowList:aidSpreadsheetService.findAidSpreadsheetInfo(), headerList:AidSpreadsheetService.headerArray]
    }
}
