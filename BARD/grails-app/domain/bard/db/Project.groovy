package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Project implements Serializable {

	Integer projectId
	String projectName
	String groupType
	String description

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append projectId
		builder.append projectName
		builder.append groupType
		builder.append description
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append projectId, other.projectId
		builder.append projectName, other.projectName
		builder.append groupType, other.groupType
		builder.append description, other.description
		builder.isEquals()
	}

	static mapping = {
		id composite: ["projectId", "projectName", "groupType", "description"]
		version false
	}

	static constraints = {
		projectName maxSize: 256
		groupType maxSize: 20
		description maxSize: 1000
	}
}
