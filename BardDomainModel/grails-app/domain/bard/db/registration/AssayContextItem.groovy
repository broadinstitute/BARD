package bard.db.registration

import bard.db.dictionary.Element


class AssayContextItem {

    private static final int EXT_VALUE_ID_MAX_SIZE = 60
    private static final int VALUE_DISPLAY_MAX_SIZE = 500
    private static final int MODIFIED_BY_MAX_SIZE = 40
    AssayContext assayContext

    AttributeType attributeType
    Element attributeElement
    String qualifier
    Element valueElement

    String extValueId
    String valueDisplay
    Float valueNum
    Float valueMin
    Float valueMax

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static belongsTo = [assayContext: AssayContext]

    static mapping = {
        id(column: 'ASSAY_CONTEXT_ITEM_ID', generator: 'sequence', params: [sequence: 'ASSAY_CONTEXT_ITEM_ID_SEQ'])
        valueElement column: "value_id"
        attributeElement column: "attribute_id"
        qualifier column: "qualifier", sqlType: "char", length: 2
    }

    static constraints = {
        attributeType(maxSize: 20)
        attributeElement()
        //TODO look at making this an enum
        qualifier(nullable: true, blank: false, inList: ['= ', '< ', '<=', '> ', '>=', '<<', '>>', '~ '])
        valueElement(nullable: true)
        extValueId(nullable: true, blank: false, maxSize: EXT_VALUE_ID_MAX_SIZE)
        valueDisplay(nullable: true, blank: false, maxSize: VALUE_DISPLAY_MAX_SIZE)
        valueNum(nullable: true)
        valueMin(nullable: true)
        valueMax(nullable: true)

        dateCreated(nullable: false)
        lastUpdated(nullable: true,)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}
