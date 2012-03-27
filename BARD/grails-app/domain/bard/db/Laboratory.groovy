package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Laboratory implements Serializable {

	Integer labId
	String labName
	String abbreviation
	String description
	String location

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append labId
		builder.append labName
		builder.append abbreviation
		builder.append description
		builder.append location
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append labId, other.labId
		builder.append labName, other.labName
		builder.append abbreviation, other.abbreviation
		builder.append description, other.description
		builder.append location, other.location
		builder.isEquals()
	}

	static mapping = {
		id composite: ["labId", "labName", "abbreviation", "description", "location"]
		version false
	}

	static constraints = {
		labName maxSize: 125
		abbreviation nullable: true, maxSize: 20
		description maxSize: 1000
		location nullable: true, maxSize: 250
	}
}
