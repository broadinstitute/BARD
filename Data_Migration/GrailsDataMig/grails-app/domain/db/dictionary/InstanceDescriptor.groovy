package db.dictionary

class InstanceDescriptor {

    static expose = 'instance-descriptor'

    InstanceDescriptor parent
    db.dictionary.Element element
    String label
    String description
    String abbreviation
    String acronym
    String synonyms
    String externalURL
    db.dictionary.Unit unit
    db.dictionary.ElementStatus elementStatus

    static hasMany = [children: InstanceDescriptor]

    static mapping = {
        id column: "node_id", generator: "assigned"
        parent column: "parent_node_id"
        unit column: "unit"
        externalURL column: "external_url"
        version false
    }

    static constraints = {
        parent nullable: true
        label maxSize: 128
        description nullable: true, maxSize: 1000
        abbreviation nullable: true, maxSize: 20
        acronym nullable: true, maxSize: 20
        synonyms nullable: true, maxSize: 1000
        externalURL nullable: true, maxSize: 1000
        unit nullable: true
        elementStatus nullable: false
    }
}
