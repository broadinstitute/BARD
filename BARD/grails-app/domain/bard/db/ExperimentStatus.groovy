package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ExperimentStatus implements Serializable {

	Integer experimentStatusId
	String status
	String capability

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append experimentStatusId
		builder.append status
		builder.append capability
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append experimentStatusId, other.experimentStatusId
		builder.append status, other.status
		builder.append capability, other.capability
		builder.isEquals()
	}

	static mapping = {
		id composite: ["experimentStatusId", "status", "capability"]
		version false
	}

	static constraints = {
		status maxSize: 20
		capability nullable: true, maxSize: 1000
	}
}
