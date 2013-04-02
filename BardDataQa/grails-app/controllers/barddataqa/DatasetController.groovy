package barddataqa

class DatasetController {
    def datasetService

    def index() {
        [datasetList: datasetService.findAllDatasetsOrderedByName()]
    }
}
