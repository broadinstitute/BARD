package querycart

class CartAssay extends QueryItem {

    CartAssay() {
        this.queryItemType = QueryItemType.AssayDefinition
    }

    CartAssay(String assayTitle, String assayIdStr) {
        this(assayTitle, Long.parseLong(assayIdStr))
    }

    CartAssay(String assayTitle, int assayId) {
        this(assayTitle, assayId as Long)
    }

    CartAssay(String assayTitle, Long assayId) {
        this.name = assayTitle
        this.externalId = assayId
        this.queryItemType = QueryItemType.AssayDefinition
    }

}
