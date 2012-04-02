package bard.db

class Element {

	String label
	String description
	String abbreviation
	String acronym
	String synonyms
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
		version false
	}

	static constraints = {
		label maxSize: 128
		description maxSize: 1000
		abbreviation nullable: true, maxSize: 20
		acronym nullable: true, maxSize: 20
		synonyms nullable: true, maxSize: 1000
	}
}
