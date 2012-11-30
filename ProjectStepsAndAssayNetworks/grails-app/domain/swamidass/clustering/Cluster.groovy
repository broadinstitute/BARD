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
        bundles cascade: "all-delete-orphan"
        bundleRelations cascade: "all-delete-orphan"
    }
}
