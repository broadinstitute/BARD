package bard.db.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ExternalAssay implements Serializable {

	Integer externalSystemId
	Integer assayId
	String extAssayId
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	ExternalSystem externalSystem
	Assay assay

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append externalSystemId
		builder.append assayId
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append externalSystemId, other.externalSystemId
		builder.append assayId, other.assayId
		builder.isEquals()
	}

	static belongsTo = [Assay, ExternalSystem]

	static mapping = {
		id composite: ["externalSystemId", "assayId"]
	}

	static constraints = {
		extAssayId maxSize: 128
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
