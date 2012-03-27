package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Measure implements Serializable {

	Integer measureId
	Integer assayId
	Integer resultTypeId
	String entryUnit
	Integer measureContextId

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append measureId
		builder.append assayId
		builder.append resultTypeId
		builder.append entryUnit
		builder.append measureContextId
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append measureId, other.measureId
		builder.append assayId, other.assayId
		builder.append resultTypeId, other.resultTypeId
		builder.append entryUnit, other.entryUnit
		builder.append measureContextId, other.measureContextId
		builder.isEquals()
	}

	static mapping = {
		id composite: ["measureId", "assayId", "resultTypeId", "entryUnit", "measureContextId"]
		version false
	}

	static constraints = {
		entryUnit nullable: true, maxSize: 100
		measureContextId nullable: true
	}
}
