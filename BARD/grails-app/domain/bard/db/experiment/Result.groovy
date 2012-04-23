package bard.db.experiment

import bard.db.util.Unit
import bard.db.dictionary.ResultType
import bard.db.util.Qualifier

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

	static hasMany = [resultHierarchiesForParentResult: ResultHierarchy,
	                  resultHierarchiesForResult: ResultHierarchy]
	static belongsTo = [Experiment, ResultContext ]

	static mappedBy = [resultHierarchiesForParentResult: "parentResult",
	                   resultHierarchiesForResult: "result"]

	static mapping = {
		id column: "Result_ID"
        unit column: "entry_unit"
        qualifier column: "qualifier", sqlType: "char", length: 2
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
