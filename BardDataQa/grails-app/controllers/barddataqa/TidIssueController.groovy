package barddataqa

class TidIssueController {
    TidIssueService tidIssueService

    private static final String aidKey = "aid"

    def duplicateResultTypes() {
        Integer aid = params.get(aidKey).toString().toInteger()
        assert aid != null
        return [rowList: tidIssueService.findTidsWithDuplicateResultTypes(aid), headerList: TidIssueService.columns]
    }

    def resultTypeContextConflict() {
        if (params.containsKey(aidKey)) {
            Integer aid = params.get(aidKey).toString().toInteger()
            return [rowList:  tidIssueService.findTidsWithResultTypeContextConflict(aid), headerList: TidIssueService.columns]
        } else {
            return [rowList:  tidIssueService.findAllTidsWithResultTypeContextConflict(), headerList: TidIssueService.columns]
        }
    }

    def relationshipProblem() {
        if (params.containsKey(aidKey)) {
            Integer aid = params.get(aidKey).toString().toInteger()
            return [rowList:  tidIssueService.findTidsWithRelationshipProblems(aid), headerList: TidIssueService.columns]
        } else {
            return [rowList: tidIssueService.findAllTidsWithRelationshipProblems(), headerList: TidIssueService.columns]
        }
    }
}
