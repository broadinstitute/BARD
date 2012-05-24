package db.experiment

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ResultHierarchy implements Serializable {

	db.experiment.HierarchyType hierarchyType
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	db.experiment.Result result
	db.experiment.Result parentResult

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

	static belongsTo = [db.experiment.Result]

	static mapping = {
		id composite: ["result", "parentResult"]
	}

	static constraints = {
		hierarchyType maxSize: 10
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
