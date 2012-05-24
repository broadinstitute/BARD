package db.dictionary

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class UnitConversion implements Serializable {

	Float multiplier
	Float offset
	String formula
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	db.dictionary.Unit fromUnit
	db.dictionary.Unit toUnit

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append fromUnit
		builder.append toUnit
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append fromUnit, other.fromUnit
		builder.append toUnit, other.toUnit
		builder.isEquals()
	}

	static belongsTo = [db.dictionary.Unit]

	static mapping = {
		id composite: ["fromUnit", "toUnit"]
        fromUnit column: "from_unit"
        toUnit column:  "to_unit"
	}

	static constraints = {
		fromUnit maxSize: 100
		toUnit maxSize: 100
		multiplier nullable: true
		offset nullable: true
		formula nullable: true, maxSize: 256
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
