package barddataqa

class MarginalProductController {
    def marginalProductService

    def index() { }

    def show() {
        Long datasetId = params.get("datasetId").toString().toLong()
        Dataset dataset = Dataset.findById(datasetId)
        [marginalProductList: marginalProductService.runMarginalProductCalculationForDataset(dataset.id), dataset: dataset]
    }

    def showMaas() {
        Long projectUid = params.get("projectUid").toString().toLong()

        [aidList: marginalProductService.findAidsNeedMaasForProject(projectUid), projectUid: projectUid]
    }

    def showRta() {
        Long projectUid = params.get("projectUid").toString().toLong()

        [aidList: marginalProductService.findAidsNeedRtaForProject(projectUid), projectUid: projectUid]
    }
}
