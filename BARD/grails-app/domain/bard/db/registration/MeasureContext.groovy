package bard.db.registration

/**
 * Defines the context for a Measure or group of Measures by grouping a set of
 * measureContextItems.  A group of measures could share the same context if
 * the measurements are taken at the same time {TODO doublcheck statement with Simon}
 */
class MeasureContext {

    static expose = 'measure-context'

	String contextName
	Date dateCreated
	Date lastUpdated
	String modifiedBy
    Assay assay

	static hasMany = [measureContextItems: MeasureContextItem,
	                  measures: Measure]

	static mapping = {
		id column: "Measure_Context_ID"
	}

	static constraints = {
		contextName maxSize: 128
        assay nullable: false
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
