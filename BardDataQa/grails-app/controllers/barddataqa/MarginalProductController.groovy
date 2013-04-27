package barddataqa

class MarginalProductController {
    def marginalProductService

    def index() { }

    def show() {
        Long datasetId = Long.valueOf(params.get("datasetId"))
        Dataset dataset = Dataset.findById(datasetId)
        [headerList: marginalProductService.columns, rowList: marginalProductService.runMarginalProductCalculationForDataset(dataset.id), dataset: dataset]
    }
}
