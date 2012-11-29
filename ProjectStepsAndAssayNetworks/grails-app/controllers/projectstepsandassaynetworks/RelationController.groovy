package projectstepsandassaynetworks

import depositor.neighbor.Relation

class RelationController {

    def index() {
        Relation relation = Relation.findById(1920217)
        println(relation.dump())
    }
}
