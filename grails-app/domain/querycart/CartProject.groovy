package querycart

import bardqueryapi.IQueryService
import bard.core.adapter.ProjectAdapter

class CartProject extends QueryItem {

    IQueryService queryService

    CartProject() {
        this.queryItemType = QueryItemType.Project
    }

    CartProject(Long pid) {
        this()
        List<ProjectAdapter> projectAdapters = queryService.findProjectsByPIDs([pid]).projectAdapters
        assert projectAdapters.size() <= 1, "ProjectAdapter must be unique given a PID"
        ProjectAdapter projectAdapter = projectAdapters ? projectAdapters.first() : null
        if (projectAdapter) {
            this.name = projectAdapter.name
            this.externalId = projectAdapter.id
            this.queryItemType = QueryItemType.Project
        }
    }

    CartProject(String projectName, Long projectIdStr) {
        this.name = projectName
        this.externalId = projectIdStr
        this.queryItemType = QueryItemType.Project
    }

    static transients = ['queryService']
}
