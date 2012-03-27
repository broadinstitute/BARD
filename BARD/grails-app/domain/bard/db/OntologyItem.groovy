package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class OntologyItem implements Serializable {

	Integer ontologyItemId
	Integer ontologyId
	Integer elementId
	String itemReference
	Integer resultTypeId

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append ontologyItemId
		builder.append ontologyId
		builder.append elementId
		builder.append itemReference
		builder.append resultTypeId
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append ontologyItemId, other.ontologyItemId
		builder.append ontologyId, other.ontologyId
		builder.append elementId, other.elementId
		builder.append itemReference, other.itemReference
		builder.append resultTypeId, other.resultTypeId
		builder.isEquals()
	}

	static mapping = {
		id composite: ["ontologyItemId", "ontologyId", "elementId", "itemReference", "resultTypeId"]
		version false
	}

	static constraints = {
		elementId nullable: true
		itemReference nullable: true, maxSize: 10
		resultTypeId nullable: true
	}
}
