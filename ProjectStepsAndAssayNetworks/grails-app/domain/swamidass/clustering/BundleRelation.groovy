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

    static mapping = {
        parentChildOverlappingCompounds column: 'Prnt_Chld_Ovrlp_Cmpds'
    }
}
