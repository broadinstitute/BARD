package bard.db

class ElementStatus {

	String elementStatus
	String capability

	static hasMany = [elements: Element,
	                  resultTypes: ResultType]

	static mapping = {
		id column: "Element_Status_ID"
		version false
	}

	static constraints = {
		elementStatus maxSize: 20
		capability nullable: true, maxSize: 256
	}
}
