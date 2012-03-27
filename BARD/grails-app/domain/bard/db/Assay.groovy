package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Assay implements Serializable {

	Integer assayId
	String assayName
	Integer assayStatusId
	String version
	String description
	String designedBy

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append assayId
		builder.append assayName
		builder.append assayStatusId
		builder.append version
		builder.append description
		builder.append designedBy
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append assayId, other.assayId
		builder.append assayName, other.assayName
		builder.append assayStatusId, other.assayStatusId
		builder.append version, other.version
		builder.append description, other.description
		builder.append designedBy, other.designedBy
		builder.isEquals()
	}

	static mapping = {
		id composite: ["assayId", "assayName", "assayStatusId", "version", "description", "designedBy"]
		version false
	}

	static constraints = {
		assayName maxSize: 128
		version maxSize: 10
		description nullable: true, maxSize: 1000
		designedBy nullable: true, maxSize: 100
	}
}
