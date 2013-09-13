package querycart

class CartProject extends QueryItem {

    CartProject() {
        this.queryItemType = QueryItemType.Project
    }

    //TODO: This should only be used for testing
    CartProject(String projectName, Long internalId, Long externalId) {
        this()
        this.name = projectName
        this.externalId = externalId
        this.internalId = internalId
        this.queryItemType = QueryItemType.Project
    }

    static transients = ['queryService']
}
