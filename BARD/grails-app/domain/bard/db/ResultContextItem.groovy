package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ResultContextItem implements Serializable {

	Integer resultContextItemId
	Integer experimentId
	Integer resultContextId
	Integer groupNo
	Integer attributeId
	Integer valueId
	String qualifier
	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append resultContextItemId
		builder.append experimentId
		builder.append resultContextId
		builder.append groupNo
		builder.append attributeId
		builder.append valueId
		builder.append qualifier
		builder.append valueDisplay
		builder.append valueNum
		builder.append valueMin
		builder.append valueMax
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append resultContextItemId, other.resultContextItemId
		builder.append experimentId, other.experimentId
		builder.append resultContextId, other.resultContextId
		builder.append groupNo, other.groupNo
		builder.append attributeId, other.attributeId
		builder.append valueId, other.valueId
		builder.append qualifier, other.qualifier
		builder.append valueDisplay, other.valueDisplay
		builder.append valueNum, other.valueNum
		builder.append valueMin, other.valueMin
		builder.append valueMax, other.valueMax
		builder.isEquals()
	}

	static mapping = {
		id composite: ["resultContextItemId", "experimentId", "resultContextId", "groupNo", "attributeId", "valueId", "qualifier", "valueDisplay", "valueNum", "valueMin", "valueMax"]
		version false
	}

	static constraints = {
		groupNo nullable: true
		valueId nullable: true
		qualifier nullable: true, maxSize: 2
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
	}
}
