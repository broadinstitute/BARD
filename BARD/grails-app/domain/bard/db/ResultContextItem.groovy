package bard.db

class ResultContextItem {

	Integer groupNo
	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	ResultContext resultContext
	Element elementByValueId
	Experiment experiment
	Element elementByAttributeId
	Qualifier qualifier

	static belongsTo = [Element, Experiment, Qualifier, ResultContext]

	static mapping = {
		id column: "Result_Context_Item_ID"
		version false
	}

	static constraints = {
		groupNo nullable: true
		valueDisplay nullable: true, maxSize: 256, unique: ["Attribute_ID", "Group_No"]
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
	}
}
