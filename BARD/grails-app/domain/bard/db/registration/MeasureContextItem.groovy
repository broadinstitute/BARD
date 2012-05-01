package bard.db.registration

import bard.db.dictionary.Element

import bard.db.util.Qualifier

class MeasureContextItem {

    static expose = 'measure-context-item'

	Integer groupNo
	AttributeType attributeType
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
	Qualifier qualifier

	static belongsTo = [Assay, MeasureContext]

	static mapping = {
		id column: "measure_Context_Item_ID"
        valueElement column: "value_id"
        attributeElement column: "attribute_id"
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
        attributeElement nullable: false
        valueElement nullable: true
        measureContext nullable: true
        assay nullable: false
        qualifier nullable: true
	}
}
