package db.dictionary

class OntologyItem {

	String itemReference
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	db.dictionary.Element element
	db.dictionary.Ontology ontology

	static belongsTo = [db.dictionary.Ontology]

	static mapping = {
		id column: "Ontology_Item_ID"
        itemReference sqlType: "char", length: 10
	}

	static constraints = {
		itemReference nullable: true, maxSize: 10
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
        ontology nullable: false
        element nullable: true
	}
}
