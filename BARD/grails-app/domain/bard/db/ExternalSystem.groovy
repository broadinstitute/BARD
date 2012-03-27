package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ExternalSystem implements Serializable {

	Integer externalSystemId
	String systemName
	String owner
	String systemUrl

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append externalSystemId
		builder.append systemName
		builder.append owner
		builder.append systemUrl
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append externalSystemId, other.externalSystemId
		builder.append systemName, other.systemName
		builder.append owner, other.owner
		builder.append systemUrl, other.systemUrl
		builder.isEquals()
	}

	static mapping = {
		id composite: ["externalSystemId", "systemName", "owner", "systemUrl"]
		version false
	}

	static constraints = {
		systemName maxSize: 128
		owner nullable: true, maxSize: 128
		systemUrl nullable: true, maxSize: 1000
	}
}
