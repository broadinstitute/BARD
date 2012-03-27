package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class MeasureContext implements Serializable {

	Integer measureContextId
	String contextName

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append measureContextId
		builder.append contextName
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append measureContextId, other.measureContextId
		builder.append contextName, other.contextName
		builder.isEquals()
	}

	static mapping = {
		id composite: ["measureContextId", "contextName"]
		version false
	}

	static constraints = {
		contextName maxSize: 128
	}
}
