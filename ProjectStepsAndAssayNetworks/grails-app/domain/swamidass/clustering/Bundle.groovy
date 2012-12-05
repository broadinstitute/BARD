package swamidass.clustering

import bard.db.registration.Assay

class Bundle {

    Integer bid
    Set<String> assays
    Cluster cluster

    static hasMany = [assays: String]

    static belongsTo = [cluster: Cluster]

    static constraints = {
    }
}
