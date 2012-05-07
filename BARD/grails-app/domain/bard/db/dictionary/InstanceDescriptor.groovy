package bard.db.dictionary

class InstanceDescriptor extends Element {

    InstanceDescriptor parent

    static hasMany = [children: InstanceDescriptor]

    static mapping = {
        id column: "node_id"
    }

    static constraints = {
        parent nullable: true
    }
}
