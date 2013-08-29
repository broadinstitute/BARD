package querycart

class CartAssay extends QueryItem {

    CartAssay() {
        this.queryItemType = QueryItemType.AssayDefinition
    }
    //TODO: This should only be used for testing
    CartAssay(String assayTitle, int assayId) {
        this(assayTitle, assayId as Long)
    }
    //TODO: This should only be used for testing
    CartAssay(String assayTitle, Long assayId) {
        this.name = assayTitle
        this.externalId = assayId
        this.queryItemType = QueryItemType.AssayDefinition
    }

}
