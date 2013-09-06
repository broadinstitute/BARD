package querycart

class CartProject extends QueryItem {

    CartProject() {
        this.queryItemType = QueryItemType.Project
    }
    //TODO: This should only be used for testing
    CartProject(String projectName, Long projectIdStr) {
        this()
        this.name = projectName
        this.externalId = projectIdStr
        this.queryItemType = QueryItemType.Project
    }

    static transients = ['queryService']
}
