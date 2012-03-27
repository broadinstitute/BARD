package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Unit implements Serializable {

	String unit
	String description

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append unit
		builder.append description
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append unit, other.unit
		builder.append description, other.description
		builder.isEquals()
	}

	static mapping = {
		id composite: ["unit", "description"]
		version false
	}

	static constraints = {
		unit maxSize: 100
		description maxSize: 1000
	}
}
