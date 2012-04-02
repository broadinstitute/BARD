package bard.db

class Qualifier {

	String qualifier
	String description

	static hasMany = [measureContextItems: MeasureContextItem,
	                  resultContextItems: ResultContextItem,
	                  results: Result]

	static mapping = {
		id name: "qualifier", generator: "assigned"
		version false
	}

	static constraints = {
		qualifier maxSize: 2
		description maxSize: 1000
	}
}
