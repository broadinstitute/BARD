package bard.db

class Measure {

	MeasureContext measureContext
	Assay assay
	Unit unit
	ResultType resultType

	static belongsTo = [Assay, MeasureContext, ResultType, Unit]

	static mapping = {
		id column: "Measure_ID"
		version false
	}
}
