package bard.db

class MeasureContextItem {

	Integer groupNo
	String attributeType
	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	Element elementByValueId
	MeasureContext measureContext
	Element elementByAttributeId
	Assay assay
	Qualifier qualifier

	static belongsTo = [Assay, Element, MeasureContext, Qualifier]

	static mapping = {
		id column: "measure_Context_Item_ID"
		version false
	}

	static constraints = {
		groupNo nullable: true
		attributeType maxSize: 20
		valueDisplay nullable: true, maxSize: 256, unique: ["Attribute_ID", "Group_No", "Measure_Context_ID"]
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
	}
}
