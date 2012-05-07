package bard.db.dictionary

class AssayDescriptor extends Element {

    AssayDescriptor parent

    static hasMany = [children: AssayDescriptor]

    static mapping = {
        id column: "node_id"
    }

    static constraints = {
        parent nullable: true
    }
}
