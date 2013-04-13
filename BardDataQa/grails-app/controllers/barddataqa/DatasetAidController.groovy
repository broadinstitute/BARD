package barddataqa

class DatasetAidController {

    def datasetAidService

    def index() {
        long datasetId = Long.valueOf(params.get("datasetId"))

        [datasetId: datasetId, datasetAids: datasetAidService.lookupAidForDataset(datasetId)]
    }
}
