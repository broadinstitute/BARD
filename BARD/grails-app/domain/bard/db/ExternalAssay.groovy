package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ExternalAssay implements Serializable {

	Integer externalSystemId
	Integer assayId
	String extAssayId

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append externalSystemId
		builder.append assayId
		builder.append extAssayId
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append externalSystemId, other.externalSystemId
		builder.append assayId, other.assayId
		builder.append extAssayId, other.extAssayId
		builder.isEquals()
	}

	static mapping = {
		id composite: ["externalSystemId", "assayId", "extAssayId"]
		version false
	}

	static constraints = {
		extAssayId maxSize: 128
	}
}
