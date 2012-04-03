package bard.db.model

class ResultContextItem {

	Integer groupNo
	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	ResultContext resultContext
	Element elementByValueId
	Experiment experiment
	Element elementByAttributeId
	Qualifier qualifier

	static belongsTo = [Element, Experiment, Qualifier, ResultContext]

	static mapping = {
		id column: "Result_Context_Item_ID"
	}

	static constraints = {
		groupNo nullable: true
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
