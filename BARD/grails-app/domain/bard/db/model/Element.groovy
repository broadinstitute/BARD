package bard.db.model

class Element {

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
	Element element

	static hasMany = [elements: Element,
	                  measureContextItemsForAttributeId: MeasureContextItem,
	                  measureContextItemsForValueId: MeasureContextItem,
	                  ontologyItems: OntologyItem,
	                  resultContextItemsForAttributeId: ResultContextItem,
	                  resultContextItemsForValueId: ResultContextItem]
	static belongsTo = [ElementStatus, Unit]

	// TODO you have multiple hasMany references for class(es) [ResultContextItem, MeasureContextItem] 
	//      so you'll need to disambiguate them with the 'mappedBy' property:
	static mappedBy = [measureContextItemsForAttributeId: "elementByAttributeId",
	                   measureContextItemsForValueId: "elementByValueId",
	                   resultContextItemsForAttributeId: "elementByAttributeId",
	                   resultContextItemsForValueId: "elementByValueId"]

	static mapping = {
		id column: "Element_ID"
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
	}
}
