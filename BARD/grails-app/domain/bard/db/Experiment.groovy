package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Experiment implements Serializable {

	Integer experimentId
	String experimentName
	Integer assayId
	Integer projectId
	Integer experimentStatusId
	Date runDate
	Date holdUntilDate
	String description
	Integer sourceId

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append experimentId
		builder.append experimentName
		builder.append assayId
		builder.append projectId
		builder.append experimentStatusId
		builder.append runDate
		builder.append holdUntilDate
		builder.append description
		builder.append sourceId
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append experimentId, other.experimentId
		builder.append experimentName, other.experimentName
		builder.append assayId, other.assayId
		builder.append projectId, other.projectId
		builder.append experimentStatusId, other.experimentStatusId
		builder.append runDate, other.runDate
		builder.append holdUntilDate, other.holdUntilDate
		builder.append description, other.description
		builder.append sourceId, other.sourceId
		builder.isEquals()
	}

	static mapping = {
		id composite: ["experimentId", "experimentName", "assayId", "projectId", "experimentStatusId", "runDate", "holdUntilDate", "description", "sourceId"]
		version false
	}

	static constraints = {
		experimentName maxSize: 256
		projectId nullable: true
		runDate nullable: true, maxSize: 19
		holdUntilDate nullable: true, maxSize: 10
		description maxSize: 1000
	}
}
