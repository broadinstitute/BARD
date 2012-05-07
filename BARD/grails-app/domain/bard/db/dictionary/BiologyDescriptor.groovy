package bard.db.dictionary

class BiologyDescriptor extends Element {

    BiologyDescriptor parent

    static hasMany = [children: BiologyDescriptor]

    static mapping = {
        id column: "node_id"
    }

    static constraints = {
        parent nullable: true
    }
}
