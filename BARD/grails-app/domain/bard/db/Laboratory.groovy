package bard.db

class Laboratory {

	String labName
	String abbreviation
	String description
	String location

	static hasMany = [experiments: Experiment]

	static mapping = {
		id column: "Lab_ID"
		version false
	}

	static constraints = {
		labName maxSize: 125
		abbreviation nullable: true, maxSize: 20
		description maxSize: 1000
		location nullable: true, maxSize: 250
	}
}
