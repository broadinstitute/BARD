package db.registration

import db.dictionary.Element
import db.util.Qualifier

class MeasureContextItem {

    static expose = 'measure-context-item'

	MeasureContextItem parentGroup
	db.registration.AttributeType attributeType
	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	Date dateCreated
	Date lastUpdated
	String modifiedBy
    Element attributeElement
    Element valueElement
	db.registration.MeasureContext measureContext
	db.registration.Assay assay
	Qualifier qualifier

    static hasMany = [children: MeasureContextItem]

	static belongsTo = [db.registration.Assay, db.registration.MeasureContext]

	static mapping = {
		id column: "measure_Context_Item_ID"
        parentGroup column: "GROUP_MEASURE_CONTEXT_ITEM_ID"
        valueElement column: "value_id"
        attributeElement column: "attribute_id"
        qualifier column: "qualifier", sqlType: "char", length: 2
	}

	static constraints = {
		parentGroup nullable: true
		attributeType maxSize: 20
//		valueDisplay nullable: true, maxSize: 256, unique: ["Attribute_ID", "Group_No", "Measure_Context_ID"]
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
        attributeElement nullable: false
        valueElement nullable: true
        measureContext nullable: true
        assay nullable: false
        qualifier nullable: true
	}
}
