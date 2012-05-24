package db.registration

class MeasureContext {

    static expose = 'measure-context'

	String contextName
	Date dateCreated
	Date lastUpdated
	String modifiedBy
    db.registration.Assay assay

	static hasMany = [measureContextItems: db.registration.MeasureContextItem,
	                  measures: db.registration.Measure]

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
