package swamidass.clustering

class Cluster {

    String name
    Set<Bundle> bundles = []
    Set<BundleRelation> bundleRelations = []

    static hasMany = [bundles: Bundle, bundleRelations: BundleRelation]

    static constraints = {
    }

    static mapping = {
        table 'cluster_tbl'
    }
}
