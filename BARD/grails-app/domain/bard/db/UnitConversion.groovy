package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class UnitConversion implements Serializable {

	String fromUnit
	String toUnit
	Float multiplier
	Float offset
	String formula

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append fromUnit
		builder.append toUnit
		builder.append multiplier
		builder.append offset
		builder.append formula
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append fromUnit, other.fromUnit
		builder.append toUnit, other.toUnit
		builder.append multiplier, other.multiplier
		builder.append offset, other.offset
		builder.append formula, other.formula
		builder.isEquals()
	}

	static mapping = {
		id composite: ["fromUnit", "toUnit", "multiplier", "offset", "formula"]
		version false
	}

	static constraints = {
		fromUnit maxSize: 100
		toUnit maxSize: 100
		multiplier nullable: true
		offset nullable: true
		formula nullable: true, maxSize: 256
	}
}
