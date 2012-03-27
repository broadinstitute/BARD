package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ElementStatus implements Serializable {

	Integer elementStatusId
	String elementStatus
	String capability

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append elementStatusId
		builder.append elementStatus
		builder.append capability
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append elementStatusId, other.elementStatusId
		builder.append elementStatus, other.elementStatus
		builder.append capability, other.capability
		builder.isEquals()
	}

	static mapping = {
		id composite: ["elementStatusId", "elementStatus", "capability"]
		version false
	}

	static constraints = {
		elementStatus maxSize: 20
		capability nullable: true, maxSize: 256
	}
}
