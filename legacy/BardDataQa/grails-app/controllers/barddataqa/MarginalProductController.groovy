package barddataqa

import grails.validation.Validateable

class MarginalProductController {

    private static final String controllerKey = "extraController"
    private static final String actionKey = "extraAction"
    private static final String descriptionKey = "extraDescription"

    MarginalProductService marginalProductService

    def index() {
        render("Hello, world!")
    }

    def show() {
        Long datasetId = params.get("datasetId").toString().toLong()
        Dataset dataset = Dataset.findById(datasetId)
        [marginalProductList: marginalProductService.calculate(datasetId, new Date()), dataset: dataset]
    }

    def showNeedMaas() {
        Closure<List<Integer>> findNeedMaasClosure = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatNeedMaas(datasetId, projectUid)
        }

        render(view: "showAids", model: buildMap(params, findNeedMaasClosure, "AID's that need Minimum Assay Annotation"))
    }

    def showNeedRta() {
        Closure<List<Integer>> findNeedRtaClosure = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatNeedRta(datasetId, projectUid)
        }

        render(view: "showAids", model: buildMap(params, findNeedRtaClosure, "AID's that need Result Type Annotation"))
    }

    def showMissingAid() {
        Closure<List<Integer>> findMissingClosure = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatAreMissing(datasetId, projectUid)
        }

        render(view: "showAids", model: buildMap(params, findMissingClosure, "AID's that are not in the external_reference table"))
    }

    def showResultMapProblems() {
        Integer projectUid = params.get("projectUid").toString().toInteger()

        return [problemAidMap: marginalProductService.findAidsWithResultMapProblem(projectUid)]
    }

    private Map buildMap(Map params, Closure<List<Integer>> findAidsClosure, String title) {
        Long datasetId = params.get("datasetId").toString().toLong()
        Integer projectUid = params.get("projectUid").toString().toInteger()

        List<Integer> aidList = findAidsClosure(datasetId, projectUid)

        return [title: title, aidList: aidList]
    }
}