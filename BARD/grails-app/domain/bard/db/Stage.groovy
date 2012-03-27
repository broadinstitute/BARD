package bard.db

class Stage {

	String stage

	static mapping = {
		id name: "stage", generator: "assigned"
		version false
	}

	static constraints = {
		stage maxSize: 20
	}
}
