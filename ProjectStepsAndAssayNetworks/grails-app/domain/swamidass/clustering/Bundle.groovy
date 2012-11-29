package swamidass.clustering

import bard.db.registration.Assay

class Bundle {

    Integer bid
    Set<Assay> assays
    Cluster cluster

    static belongsTo = [cluster: Cluster]

    static constraints = {
    }
}
