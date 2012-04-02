package bard.db

class MeasureContext {

	String contextName

	static hasMany = [measureContextItems: MeasureContextItem,
	                  measures: Measure]

	static mapping = {
		id column: "Measure_Context_ID"
		version false
	}

	static constraints = {
		contextName maxSize: 128
	}
}
