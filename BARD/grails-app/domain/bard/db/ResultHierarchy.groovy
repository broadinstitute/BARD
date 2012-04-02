package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ResultHierarchy implements Serializable {

	Integer resultId
	Integer parentResultId
	String hierarchyType
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
		version false
	}

	static constraints = {
		hierarchyType maxSize: 10
	}
}
