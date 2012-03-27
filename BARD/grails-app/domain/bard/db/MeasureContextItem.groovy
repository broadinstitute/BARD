package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class MeasureContextItem implements Serializable {

	Integer measureContextItemId
	Integer assayId
	Integer measureContextId
	Integer groupNo
	String attributeType
	Integer attributeId
	Integer valueId
	String qualifier
	String valueDisplay
	Float valueNum
	Float valueMin
	Float valueMax

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append measureContextItemId
		builder.append assayId
		builder.append measureContextId
		builder.append groupNo
		builder.append attributeType
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
		builder.append measureContextItemId, other.measureContextItemId
		builder.append assayId, other.assayId
		builder.append measureContextId, other.measureContextId
		builder.append groupNo, other.groupNo
		builder.append attributeType, other.attributeType
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
		id composite: ["measureContextItemId", "assayId", "measureContextId", "groupNo", "attributeType", "attributeId", "valueId", "qualifier", "valueDisplay", "valueNum", "valueMin", "valueMax"]
		version false
	}

	static constraints = {
		groupNo nullable: true
		attributeType nullable: true, maxSize: 10
		valueId nullable: true
		qualifier nullable: true, maxSize: 2
		valueDisplay nullable: true, maxSize: 256
		valueNum nullable: true
		valueMin nullable: true
		valueMax nullable: true
	}
}
