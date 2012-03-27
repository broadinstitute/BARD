package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class AssayStatus implements Serializable {

	Integer assayStatusId
	String status

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append assayStatusId
		builder.append status
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append assayStatusId, other.assayStatusId
		builder.append status, other.status
		builder.isEquals()
	}

	static mapping = {
		id composite: ["assayStatusId", "status"]
		version false
	}

	static constraints = {
		status maxSize: 20
	}
}
