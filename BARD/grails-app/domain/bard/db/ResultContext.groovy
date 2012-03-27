package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ResultContext implements Serializable {

	Integer resultContextId
	String contextName

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append resultContextId
		builder.append contextName
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append resultContextId, other.resultContextId
		builder.append contextName, other.contextName
		builder.isEquals()
	}

	static mapping = {
		id composite: ["resultContextId", "contextName"]
		version false
	}

	static constraints = {
		contextName nullable: true, maxSize: 125
	}
}
