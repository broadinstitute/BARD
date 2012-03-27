package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ProjectAssay implements Serializable {

	Integer assayId
	Integer projectId
	String stage
	Integer sequenceNo
	Float promotionThreshold
	String promotionCriteria

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append assayId
		builder.append projectId
		builder.append stage
		builder.append sequenceNo
		builder.append promotionThreshold
		builder.append promotionCriteria
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append assayId, other.assayId
		builder.append projectId, other.projectId
		builder.append stage, other.stage
		builder.append sequenceNo, other.sequenceNo
		builder.append promotionThreshold, other.promotionThreshold
		builder.append promotionCriteria, other.promotionCriteria
		builder.isEquals()
	}

	static mapping = {
		id composite: ["assayId", "projectId", "stage", "sequenceNo", "promotionThreshold", "promotionCriteria"]
		version false
	}

	static constraints = {
		stage maxSize: 20
		sequenceNo nullable: true
		promotionThreshold nullable: true
		promotionCriteria nullable: true, maxSize: 1000
	}
}
