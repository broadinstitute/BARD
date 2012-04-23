package bard.db.dictionary

import bard.db.util.Unit

class Element {

    static expose = 'element'

	String label
	String description
	String abbreviation
	String acronym
	String synonyms
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	ElementStatus elementStatus
	Unit unit
	Element parentElement

	static hasMany = [elements: Element,
	                  ontologyItems: OntologyItem]

	static mapping = {
		id column: "Element_ID"
        unit column: "unit"
	}

	static constraints = {
		label maxSize: 128
		description nullable: true, maxSize: 1000
		abbreviation nullable: true, maxSize: 20
		acronym nullable: true, maxSize: 20
		synonyms nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
        unit nullable: true
        parentElement nullable: true
        elementStatus nullable: false
	}
}
