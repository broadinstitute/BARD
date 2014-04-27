package barddataqa

class DatasetProjectUidController {

    def datasetProjectUidService

    def index() {
        long datasetId = Long.valueOf(params.get("datasetId"))

        [datasetId: datasetId, projectUidList: datasetProjectUidService.lookupProjectUidForDataset(datasetId)]
    }
}
