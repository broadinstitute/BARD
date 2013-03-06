package bard.db.experiment

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ResultHierarchy implements Serializable {

    private static final int HIERARCHY_TYPE_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40

    HierarchyType hierarchyType
    Date dateCreated
    Date lastUpdated
    String modifiedBy
    Result result
    Result parentResult

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append result
        builder.append parentResult
        builder.toHashCode()
    }

    boolean equals(other) {
        if (other == null) return false
        def builder = new EqualsBuilder()
        builder.append result, other.resultId
        builder.append parentResult, other.parentResultId
        builder.isEquals()
    }

    static belongsTo = [Result]

    static mapping = {
        id(column: "result_hierarchy_id", generator: "sequence", params: [sequence: 'result_hierarchy_id_seq'])
//        id composite: ["result", "parentResult"]
    }

    static constraints = {
        hierarchyType maxSize: HIERARCHY_TYPE_MAX_SIZE

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}
