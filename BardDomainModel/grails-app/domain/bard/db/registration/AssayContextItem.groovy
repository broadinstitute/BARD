package bard.db.registration

import bard.db.dictionary.Element


class AssayContextItem {



    AttributeType attributeType
    String extValueId
    String valueDisplay
    Float valueNum
    Float valueMin
    Float valueMax
    Date dateCreated
    Date lastUpdated
    String modifiedBy
    Element attributeElement
    Element valueElement
    String qualifier

    AssayContext assayContext

    static belongsTo = [assayContext:AssayContext]

    static mapping = {
        id( column: 'ASSAY_CONTEXT_ITEM_ID', generator: 'sequence', params: [sequence: 'ASSAY_CONTEXT_ITEM_ID_SEQ'])
        valueElement column: "value_id"
        attributeElement column: "attribute_id"
        qualifier column: "qualifier", sqlType: "char", length: 2
    }

    static constraints = {
        attributeType maxSize: 20
        extValueId(nullable: true, maxSize: 100)
        valueDisplay(nullable: true, maxSize: 512)
        valueNum(nullable: true)
        valueMin(nullable: true)
        valueMax(nullable: true)
        dateCreated(maxSize: 19)
        lastUpdated(nullable: true, maxSize: 19)
        modifiedBy(nullable: true, maxSize: 40)
        attributeElement(nullable: false)
        valueElement(nullable: true)
        qualifier(nullable: true)
    }
}
