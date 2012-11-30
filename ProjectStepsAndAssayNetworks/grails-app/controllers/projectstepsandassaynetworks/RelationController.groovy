package projectstepsandassaynetworks

import depositor.neighbor.Relation

class RelationController {

    def index() {
        Relation relation = Relation.findById(23916710L)
        println(relation.dump())
    }
}
