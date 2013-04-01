package barddataqa

class ResultMapIssueController {
    def resultMapIssueService

    def index() {
        [rowList: resultMapIssueService.findAidsWithDuplicateResultTypesInResultMap(),
                headers: resultMapIssueService.columns]
    }
}
