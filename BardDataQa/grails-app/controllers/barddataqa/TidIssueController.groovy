package barddataqa

class TidIssueController {
    def tidIssueService


    def duplicateResultTypes(Long id) {
        def aid = id
        assert aid != null
        return [rowList: tidIssueService.findTidsWithDuplicateResultTypes(aid), headerList: tidIssueService.columns]
    }

    def resultTypeContextConflict() {
        return [rowList:  tidIssueService.findTidsWithResultTypeContextConflict(), headerList: tidIssueService.columns]
    }
}
