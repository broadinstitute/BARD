package bard.db.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ProjectAssay implements Serializable {

	Integer assayId
	Integer projectId
	String stage
	Integer sequenceNo
	Float promotionThreshold
	String promotionCriteria
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	Project project
	Assay assay

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append assayId
		builder.append projectId
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append assayId, other.assayId
		builder.append projectId, other.projectId
		builder.isEquals()
	}

	static belongsTo = [Assay, Project]

	static mapping = {
		id composite: ["assayId", "projectId"]
	}

	static constraints = {
		stage maxSize: 20
		sequenceNo nullable: true
		promotionThreshold nullable: true
		promotionCriteria nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
