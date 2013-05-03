package barddataqa

class IndexController {

    CheckForMissingAidsService checkForMissingAidsService

    def index() {
        render(view: "/index", model: [missingAidCount: checkForMissingAidsService.countAidsThatAreMissingFromDatasets()])
    }
}
