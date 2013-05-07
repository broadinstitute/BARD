package barddataqa

import grails.validation.Validateable

class MarginalProductController {

    MarginalProductService marginalProductService

    def index() { }

    def show() {
        Long datasetId = params.get("datasetId").toString().toLong()
        Dataset dataset = Dataset.findById(datasetId)
        [marginalProductList: marginalProductService.calculate(datasetId, new Date()), dataset: dataset]
    }

    def showNeedMaas() {
        Closure<List<Integer>> findNeedMaasClosure = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatNeedMaas(datasetId, projectUid)
        }

        redirect(action: "showAids", params: buildMap(params, findNeedMaasClosure, "AID's that need Minimum Assay Annotation"))
    }

    def showNeedRta() {
        Closure<List<Integer>> findNeedRtaClosure = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatNeedRta(datasetId, projectUid)
        }

        redirect(action: "showAids", params: buildMap(params, findNeedRtaClosure, "AID's that need Result Type Annotation"))
    }

    def showResultMapConflictBetweenResultTypeAndContextItem() {
        Closure<List<Integer>> findHaveConflictBetweenResultTypeAndContextItem = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatHaveRmConflict(datasetId, projectUid)
        }

        redirect(action: "showAids", params: buildMap(params, findHaveConflictBetweenResultTypeAndContextItem,
                "AID's that have Result Map conflict between Result Type and Context Item"))
    }

    def showResultMapDuplicateResult() {
        Closure<List<Integer>> findDuplicateResult = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatHaveDuplicateResult(datasetId, projectUid)
        }

        redirect(action: "showAids", params: buildMap(params, findDuplicateResult,
                "AID's in Result Map that will generate duplicate results"))
    }

    def showResultMapRelationshipProblem() {
        Closure<List<Integer>> findRelationshipProblems = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatHaveRmRelationshipProblem(datasetId, projectUid)
        }

        redirect(action: "showAids", params: buildMap(params, findRelationshipProblems,
                "AID's in Result Map that have relationship problems (one but not both of parentTid or relationship are present)"))
    }

    def showAids() {
        params
    }

    private Map buildMap(Map params, Closure<List<Integer>> findAidsClosure, String title) {
        Long datasetId = params.get("datasetId").toString().toLong()
        Integer projectUid = params.get("projectUid").toString().toInteger()

        List<Integer> aidList = findAidsClosure(datasetId, projectUid)
        if (aidList.size() == 1) {
            aidList.add(null)
        }

        [title: title, aidList: aidList]
    }


}
