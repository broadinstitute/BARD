package bard.db.registration

import bard.db.dictionary.Element

import bard.db.util.Qualifier

class MeasureContextItem {

	Integer groupNo
	AttributeType attributeType
	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	Element elementByValueId
	MeasureContext measureContext
	Element elementByAttributeId
	Assay assay
	Qualifier qualifier

	static belongsTo = [Assay, Element, MeasureContext, Qualifier]

	static mapping = {
		id column: "measure_Context_Item_ID"
        elementByValueId column: "value_id"
        elementByAttributeId column: "attribute_id"
        qualifier column: "qualifier", sqlType: "char", length: 2
	}

	static constraints = {
		groupNo nullable: true
		attributeType maxSize: 20
//		valueDisplay nullable: true, maxSize: 256, unique: ["Attribute_ID", "Group_No", "Measure_Context_ID"]
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
