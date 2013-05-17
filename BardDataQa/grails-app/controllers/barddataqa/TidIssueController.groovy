package barddataqa

class TidIssueController {
    TidIssueService tidIssueService

    private static final String aidKey = "aid"
    private static final String problemKey = "problem"

    def show() {
        Integer aid = params.get(aidKey).toString().toInteger()
        ResultMapProblemEnum problem = (ResultMapProblemEnum)params.get(problemKey)

        return [rowList: tidIssueService.findTidsWithResultMapProblem(problem, aid), headerList: TidIssueService.columns]
    }
}
