package barddataqa

class AidIssueController {
    def aidIssueService

    def index() {
        return [rowList: aidIssueService.serviceMethod(), headers: aidIssueService.getColumns()]
    }
}
