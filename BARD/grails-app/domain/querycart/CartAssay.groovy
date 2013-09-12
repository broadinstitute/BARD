package querycart

class CartAssay extends QueryItem {

    CartAssay() {
        this.queryItemType = QueryItemType.AssayDefinition
    }
    //TODO: This should only be used for testing
    CartAssay(String assayTitle, int internalId, int assayId) {
        this(assayTitle, internalId as Long, assayId as Long)
    }
    //TODO: This should only be used for testing
    CartAssay(String assayTitle, Long internalId, Long assayId) {
        this.name = assayTitle
        this.internalId = internalId
        this.externalId = assayId
        this.queryItemType = QueryItemType.AssayDefinition
    }

}
