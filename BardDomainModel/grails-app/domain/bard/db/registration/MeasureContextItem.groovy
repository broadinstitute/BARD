package bard.db.registration

import bard.db.dictionary.Element


class MeasureContextItem {

    MeasureContextItem parentGroup
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
    MeasureContext measureContext
    Assay assay
    String qualifier

    static hasMany = [children: MeasureContextItem]

    static belongsTo = [Assay, MeasureContext]

    static mapping = {
        id( column: 'MEASURE_CONTEXT_ITEM_ID', generator: 'sequence', params: [sequence: 'MEASURE_CONTEXT_ITEM_ID_SEQ'])
        parentGroup column: "GROUP_MEASURE_CONTEXT_ITEM_ID"
        valueElement column: "value_id"
        attributeElement column: "attribute_id"
        qualifier column: "qualifier", sqlType: "char", length: 2
    }

    static constraints = {
        parentGroup nullable: true
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
        measureContext(nullable: true)
        assay(nullable: false)
        qualifier(nullable: true)
    }
}
