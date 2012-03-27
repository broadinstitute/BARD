package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ResultStatus implements Serializable {

	Integer resultStatusId
	String status

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append resultStatusId
		builder.append status
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append resultStatusId, other.resultStatusId
		builder.append status, other.status
		builder.isEquals()
	}

	static mapping = {
		id composite: ["resultStatusId", "status"]
		version false
	}

	static constraints = {
		status maxSize: 20
	}
}
