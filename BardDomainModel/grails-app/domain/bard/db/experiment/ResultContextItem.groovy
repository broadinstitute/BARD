package bard.db.experiment

import bard.db.dictionary.Element

class ResultContextItem {

    Experiment experiment
    ResultContextItem parentGroup
    Result result
    Element attribute
    String qualifier
    String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	Element valueControlled
    String extValueId

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static belongsTo = [Experiment]

	static mapping = {
		id column: "Result_Context_Item_ID", generator: "assigned"
        attribute column: "attribute_id"
        valueControlled column: "value_id"
        qualifier column: "qualifier", sqlType: "char", length: 2
        parentGroup column: "group_result_context_id"
	}

	static constraints = {
		parentGroup nullable: true
        attribute nullable: false
        qualifier( nullable: true )
        result nullable: true
		valueDisplay nullable: true, maxSize: 512
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
        valueControlled nullable: true
        extValueId(nullable: true,maxSize: 100)
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40

	}
}
