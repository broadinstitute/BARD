package bard.db

class Result {

	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
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
	static mappedBy = [resultHierarchiesForParentResultId: "resultByParentResultId",
	                   resultHierarchiesForResultId: "resultByResultId"]

	static mapping = {
		id column: "Result_ID"
		version false
	}

	static constraints = {
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
	}
}
