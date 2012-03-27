package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Substance implements Serializable {

	Integer substanceId
	Integer compoundId
	String smiles
	BigDecimal molecularWeight
	String substanceType

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append substanceId
		builder.append compoundId
		builder.append smiles
		builder.append molecularWeight
		builder.append substanceType
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append substanceId, other.substanceId
		builder.append compoundId, other.compoundId
		builder.append smiles, other.smiles
		builder.append molecularWeight, other.molecularWeight
		builder.append substanceType, other.substanceType
		builder.isEquals()
	}

	static mapping = {
		id composite: ["substanceId", "compoundId", "smiles", "molecularWeight", "substanceType"]
		version false
	}

	static constraints = {
		compoundId nullable: true
		smiles nullable: true, maxSize: 4000
		molecularWeight nullable: true, scale: 3
		substanceType maxSize: 20
	}
}
