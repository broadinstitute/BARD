package db.dictionary

class ResultType {

    static expose = 'result-type'

    ResultType parent
    db.dictionary.Element element
    String resultTypeName
    String description
    String abbreviation
    String synonyms
    db.dictionary.Unit baseUnit
    db.dictionary.ElementStatus resultTypeStatus

    static hasMany = [children: ResultType]

    static mapping = {
        id column: "node_id", generator: "assigned"
        parent column: "parent_node_id"
        baseUnit column: "base_unit"
        element column: "result_type_id"
        version false
    }

    static constraints = {
        parent nullable: true
        resultTypeName maxSize: 128, nullable: false
        element nullable: false
        description nullable: true, maxSize: 1000
        abbreviation nullable: true, maxSize: 20
        synonyms nullable: true, maxSize: 1000
        baseUnit nullable: true
        resultTypeStatus nullable: false
    }
}
