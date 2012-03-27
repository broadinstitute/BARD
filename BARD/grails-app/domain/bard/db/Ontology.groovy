package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Ontology implements Serializable {

	Integer ontologyId
	String ontologyName
	String abbreviation
	String systemUrl

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append ontologyId
		builder.append ontologyName
		builder.append abbreviation
		builder.append systemUrl
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append ontologyId, other.ontologyId
		builder.append ontologyName, other.ontologyName
		builder.append abbreviation, other.abbreviation
		builder.append systemUrl, other.systemUrl
		builder.isEquals()
	}

	static mapping = {
		id composite: ["ontologyId", "ontologyName", "abbreviation", "systemUrl"]
		version false
	}

	static constraints = {
		ontologyName maxSize: 256
		abbreviation nullable: true, maxSize: 20
		systemUrl nullable: true, maxSize: 1000
	}
}
