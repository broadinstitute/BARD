package bard.db.registration

class MeasureContext {

	String contextName
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [measureContextItems: MeasureContextItem,
	                  measures: Measure]

	static mapping = {
		id column: "Measure_Context_ID"
	}

	static constraints = {
		contextName maxSize: 128
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
