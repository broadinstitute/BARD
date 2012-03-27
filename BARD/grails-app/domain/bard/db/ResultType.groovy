package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ResultType implements Serializable {

	Integer resultTypeId
	Integer parentResultTypeId
	String resultTypeName
	String description
	Integer resultTypeStatusId
	String baseUnit

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append resultTypeId
		builder.append parentResultTypeId
		builder.append resultTypeName
		builder.append description
		builder.append resultTypeStatusId
		builder.append baseUnit
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append resultTypeId, other.resultTypeId
		builder.append parentResultTypeId, other.parentResultTypeId
		builder.append resultTypeName, other.resultTypeName
		builder.append description, other.description
		builder.append resultTypeStatusId, other.resultTypeStatusId
		builder.append baseUnit, other.baseUnit
		builder.isEquals()
	}

	static mapping = {
		id composite: ["resultTypeId", "parentResultTypeId", "resultTypeName", "description", "resultTypeStatusId", "baseUnit"]
		version false
	}

	static constraints = {
		parentResultTypeId nullable: true
		resultTypeName maxSize: 128
		description nullable: true, maxSize: 1000
		baseUnit nullable: true, maxSize: 100
	}
}
