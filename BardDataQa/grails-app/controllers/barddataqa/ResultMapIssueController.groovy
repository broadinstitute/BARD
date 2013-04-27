package barddataqa

class ResultMapIssueController {
    def resultMapIssueService

    def index() {
        Map<String, Integer> nameCountMap = resultMapIssueService.calculateIssueTypeCounts()

        List<IssueCountCommand> issueCountCommandList = new LinkedList<IssueCountCommand>()
        issueCountCommandList.add(new IssueCountCommand(
                description: "Duplicate result types in AID",
                count: nameCountMap.get("duplicates"),
                controller: "resultMapIssue", action: "duplicateResultTypes"))

        issueCountCommandList.add(new IssueCountCommand(
                description: "Collision between result type and context item",
                count: nameCountMap.get("collisions"),
                controller: "tidIssue", action: "resultTypeContextConflict"))

        return [issueCountCommandList: issueCountCommandList]
    }

    def duplicateResultTypes() {
        [rowList: resultMapIssueService.findAidsWithDuplicateResultTypesInResultMap(),
                        headers: resultMapIssueService.duplicateResultTypeColumns]
    }
}

class IssueCountCommand {
    String description
    int count
    String controller
    String action
}