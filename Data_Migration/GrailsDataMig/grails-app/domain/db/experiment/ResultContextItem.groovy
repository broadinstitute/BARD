package db.experiment

import db.dictionary.Element
import db.util.Qualifier

class ResultContextItem {

    db.experiment.Experiment experiment
    ResultContextItem parentGroup
    db.experiment.Result result
    Element attribute
    Qualifier qualifier
    String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	Element valueControlled
    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static belongsTo = [db.experiment.Experiment]

	static mapping = {
		id column: "Result_Context_Item_ID"
        attribute column: "attribute_id"
        valueControlled column: "value_id"
        qualifier column: "qualifier", sqlType: "char", length: 2
        parentGroup column: "group_result_context_id"
	}

	static constraints = {
		parentGroup nullable: true
        attribute nullable: false
        result nullable: true
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
        valueControlled nullable: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
