package bard.db.model

class Result {

	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	ResultContext resultContext
	Substance substance
	Experiment experiment
	ResultStatus resultStatus
	Unit unit
	ResultType resultType
	Qualifier qualifier

	static hasMany = [resultHierarchiesForParentResultId: ResultHierarchy,
	                  resultHierarchiesForResultId: ResultHierarchy]
	static belongsTo = [Experiment, Qualifier, ResultContext, ResultStatus, ResultType, Substance, Unit]

	// TODO you have multiple hasMany references for class(es) [ResultHierarchy] 
	//      so you'll need to disambiguate them with the 'mappedBy' property:
	static mappedBy = [resultHierarchiesForParentResultId: "parentResultId",
	                   resultHierarchiesForResultId: "resultId"]

	static mapping = {
		id column: "Result_ID"
	}

	static constraints = {
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
