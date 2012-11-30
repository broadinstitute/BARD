package swamidass.clustering

class BundleRelation {

    Bundle parentBundle
    Bundle childBundle
    Integer parentTestedCompounds
    Integer parentChildOverlappingCompounds
    Cluster cluster

    static belongsTo = [cluster: Cluster]

    static constraints = {
        childBundle(nullable: true)
        parentBundle(nullable: true) //This is a hack to overcome GORM throwing an exception saying a the parent bundle hasn't been persisted yet.
        parentChildOverlappingCompounds(nullable: true)
    }

    static mapping = {
        parentChildOverlappingCompounds column: 'Prnt_Chld_Ovrlp_Cmpds'
    }
}
