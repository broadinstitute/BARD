package bard.db.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ResultHierarchy implements Serializable {

	Integer resultId
	Integer parentResultId
	String hierarchyType
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	Result resultByParentResultId
	Result resultByResultId

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append resultId
		builder.append parentResultId
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append resultId, other.resultId
		builder.append parentResultId, other.parentResultId
		builder.isEquals()
	}

	static belongsTo = [Result]

	static mapping = {
		id composite: ["resultId", "parentResultId"]
	}

	static constraints = {
		hierarchyType maxSize: 10
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
