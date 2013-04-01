package barddataqa

class TidIssueController {
    def tidIssueService

    def index() {
        return [tidIssueList: tidIssueService.findTidIssues(), headers: tidIssueService.columns]
    }
}
