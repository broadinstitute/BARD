package bard.db.model

class Measure {

	Date dateCreated
	Date lastUpdated
	String modifiedBy
	MeasureContext measureContext
	Assay assay
	Unit entryUnit
	ResultType resultType

	static belongsTo = [Assay, MeasureContext, ResultType, Unit]

	static mapping = {
		id column: "Measure_ID"
        entryUnit column: "entry_unit"
	}

	static constraints = {
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
