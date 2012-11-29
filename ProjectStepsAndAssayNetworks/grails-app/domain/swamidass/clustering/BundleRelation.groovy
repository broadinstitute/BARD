package swamidass.clustering

class BundleRelation {

    Bundle parentBundle
    Bundle childBundle
    Integer parentTestedCompounds
    Integer parentChildOverlappingCompounds
    Cluster cluster

    static belongsTo = [cluster: Cluster]

    static constraints = {
    }
}
