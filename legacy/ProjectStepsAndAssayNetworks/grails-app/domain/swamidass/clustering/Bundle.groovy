package swamidass.clustering

class Bundle {

    Integer bid
    Set<String> pubchemAIDs
    Cluster cluster

    static hasMany = [pubchemAIDs: String]

    static belongsTo = [cluster: Cluster]

    static constraints = {
    }
}
