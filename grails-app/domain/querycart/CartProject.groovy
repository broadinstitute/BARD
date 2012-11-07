package querycart

import bardqueryapi.IQueryService
import bard.core.adapter.ProjectAdapter

class CartProject extends QueryItem {

    CartProject() {
        this.queryItemType = QueryItemType.Project
    }

    CartProject(String projectName, Long projectIdStr) {
        this()
        this.name = projectName
        this.externalId = projectIdStr
        this.queryItemType = QueryItemType.Project
    }

    static transients = ['queryService']
}
