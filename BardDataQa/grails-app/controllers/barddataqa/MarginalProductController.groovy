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

    def showResultMapConflictBetweenResultTypeAndContextItem() {
        Closure<List<Integer>> findHaveConflictBetweenResultTypeAndContextItem = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatHaveRmConflict(datasetId, projectUid)
        }

        Map newParams = buildMap(params, findHaveConflictBetweenResultTypeAndContextItem,
                        "AID's that have Result Map conflict between Result Type and Context Item")

        newParams.put(controllerKey, "tidIssue")
        newParams.put(actionKey, "resultTypeContextConflict")
        newParams.put(descriptionKey, "show result map entries")

        render(view: "showAids", model: newParams)
    }

    def showResultMapDuplicateResult() {
        Closure<List<Integer>> findDuplicateResult = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatHaveDuplicateResult(datasetId, projectUid)
        }

        Map newParams = buildMap(params, findDuplicateResult, "AID's in Result Map that will generate duplicate results")

        newParams.put(controllerKey, "tidIssue")
        newParams.put(actionKey, "duplicateResultTypes")
        newParams.put(descriptionKey, "show result map entries")

        render(view: "showAids", model: newParams)
    }

    def showResultMapRelationshipProblem() {
        Closure<List<Integer>> findRelationshipProblems = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatHaveRmRelationshipProblem(datasetId, projectUid)
        }

        Map newParams = buildMap(params, findRelationshipProblems,
                "AID's in Result Map that have relationship problems (one but not both of parentTid or relationship are present)")

        newParams.put(controllerKey, "tidIssue")
        newParams.put(actionKey, "relationshipProblem")
        newParams.put(descriptionKey, "show result map entries")

        render(view: "showAids", model: newParams)
    }

    private Map buildMap(Map params, Closure<List<Integer>> findAidsClosure, String title) {
        Long datasetId = params.get("datasetId").toString().toLong()
        Integer projectUid = params.get("projectUid").toString().toInteger()

        List<Integer> aidList = findAidsClosure(datasetId, projectUid)

        return [title: title, aidList: aidList]
    }
}