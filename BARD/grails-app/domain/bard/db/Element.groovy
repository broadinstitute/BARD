package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Element implements Serializable {

	Integer elementId
	Integer parentElementId
	String label
	String description
	String abbreviation
	String acronym
	String synonyms
	Integer elementStatusId
	String unit

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append elementId
		builder.append parentElementId
		builder.append label
		builder.append description
		builder.append abbreviation
		builder.append acronym
		builder.append synonyms
		builder.append elementStatusId
		builder.append unit
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append elementId, other.elementId
		builder.append parentElementId, other.parentElementId
		builder.append label, other.label
		builder.append description, other.description
		builder.append abbreviation, other.abbreviation
		builder.append acronym, other.acronym
		builder.append synonyms, other.synonyms
		builder.append elementStatusId, other.elementStatusId
		builder.append unit, other.unit
		builder.isEquals()
	}

	static mapping = {
		id composite: ["elementId", "parentElementId", "label", "description", "abbreviation", "acronym", "synonyms", "elementStatusId", "unit"]
		version false
	}

	static constraints = {
		parentElementId nullable: true
		label maxSize: 128
		description maxSize: 1000
		abbreviation nullable: true, maxSize: 20
		acronym nullable: true, maxSize: 20
		synonyms nullable: true, maxSize: 1000
		unit nullable: true, maxSize: 100
	}
}
