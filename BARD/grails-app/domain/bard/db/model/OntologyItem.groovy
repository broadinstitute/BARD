package bard.db.model

class OntologyItem {

	String itemReference
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	Element element
	ResultType resultType
	Ontology ontology

	static belongsTo = [Element, Ontology, ResultType]

	static mapping = {
		id column: "Ontology_Item_ID"
        itemReference sqlType: "char", length: 10
	}

	static constraints = {
		itemReference nullable: true, maxSize: 10
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
