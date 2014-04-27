package barddataqa

class CheckForMissingAidsController {

    CheckForMissingAidsService checkForMissingAidsService

    def index() {
        [rowList: checkForMissingAidsService.findAidsThatAreMissingFromDatasets(), headerList: CheckForMissingAidsService.headerArray]
    }
}
