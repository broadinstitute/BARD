package barddataqa

class DatasetAidController {

    def datasetAidService

    def index() {
        long datasetId = Long.valueOf(params.get("datasetId"))

        [datasetId: datasetId, aidList: datasetAidService.lookupAidForDataset(datasetId)]
    }
}
