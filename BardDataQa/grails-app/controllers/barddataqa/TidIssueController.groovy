package barddataqa

class TidIssueController {
    def tidIssueService

    def issues(Long id) {
        def aid = id
        assert aid != null
        return [tidIssueList: tidIssueService.findTidIssues(aid), headers: tidIssueService.columns]
    }

    def index() {
    }
}
