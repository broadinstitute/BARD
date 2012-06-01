package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.util.Qualifier

class Result {

	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	Substance substance
	Experiment experiment
	String resultStatus
	Element resultType
	Qualifier qualifier

	static hasMany = [resultHierarchiesForParentResult: ResultHierarchy,
	                  resultHierarchiesForResult: ResultHierarchy,
                      resultContextItems: ResultContextItem]

	static belongsTo = [Experiment, ResultContextItem ]

	static mappedBy = [resultHierarchiesForParentResult: "parentResult",
	                   resultHierarchiesForResult: "result"]

	static mapping = {
		id column: "Result_ID"
        qualifier column: "qualifier", sqlType: "char", length: 2
        resultType column: "result_type_id"
	}

	static constraints = {
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
        substance nullable: false
        resultStatus maxSize: 20, nullable: false, inList: ["Pending", "Approved", "Rejected", "Mark for Deletion"]
        resultType nullable: false
	}
}
