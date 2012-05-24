package db.registration

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ExternalAssay implements Serializable {

	String extAssayId
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	db.registration.ExternalSystem externalSystem
	db.registration.Assay assay

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append externalSystem
		builder.append assay
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append externalSystem, other.externalSystem
		builder.append assay, other.assay
		builder.isEquals()
	}

	static belongsTo = [db.registration.Assay, db.registration.ExternalSystem]

	static mapping = {
		id composite: ["externalSystem", "assay"]
	}

	static constraints = {
		extAssayId maxSize: 128
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
