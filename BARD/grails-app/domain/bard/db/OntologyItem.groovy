package bard.db

class OntologyItem {

	String itemReference
	Element element
	ResultType resultType
	Ontology ontology

	static belongsTo = [Element, Ontology, ResultType]

	static mapping = {
		id column: "Ontology_Item_ID"
		version false
	}

	static constraints = {
		itemReference nullable: true, maxSize: 10
	}
}
