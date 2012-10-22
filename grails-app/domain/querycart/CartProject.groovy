package querycart

class CartProject extends QueryItem {

    CartProject() {
        this.queryItemType = QueryItemType.Project
    }

    CartProject(String projectName, String projectIdStr) {
        this(projectName, Long.parseLong(projectIdStr))
    }

    CartProject(String projectName, Long projectIdStr) {
        this.name = projectName
        this.externalId = projectIdStr
        this.queryItemType = QueryItemType.Project
    }

}
