package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Result implements Serializable {

	Integer resultId
	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax
	String qualifier
	Integer resultStatusId
	Integer experimentId
	Integer substanceId
	Integer resultContextId
	String entryUnit
	Integer resultTypeId

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append resultId
		builder.append valueDisplay
		builder.append valueNum
		builder.append valueMin
		builder.append valueMax
		builder.append qualifier
		builder.append resultStatusId
		builder.append experimentId
		builder.append substanceId
		builder.append resultContextId
		builder.append entryUnit
		builder.append resultTypeId
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append resultId, other.resultId
		builder.append valueDisplay, other.valueDisplay
		builder.append valueNum, other.valueNum
		builder.append valueMin, other.valueMin
		builder.append valueMax, other.valueMax
		builder.append qualifier, other.qualifier
		builder.append resultStatusId, other.resultStatusId
		builder.append experimentId, other.experimentId
		builder.append substanceId, other.substanceId
		builder.append resultContextId, other.resultContextId
		builder.append entryUnit, other.entryUnit
		builder.append resultTypeId, other.resultTypeId
		builder.isEquals()
	}

	static mapping = {
		id composite: ["resultId", "valueDisplay", "valueNum", "valueMin", "valueMax", "qualifier", "resultStatusId", "experimentId", "substanceId", "resultContextId", "entryUnit", "resultTypeId"]
		version false
	}

	static constraints = {
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
		qualifier nullable: true, maxSize: 2
		entryUnit nullable: true, maxSize: 100
	}
}
