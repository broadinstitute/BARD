package bard.db

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Protocol implements Serializable {

	Integer protocolId
	String protocolName
	byte[] protocolDocument
	Integer assayId

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append protocolId
		builder.append protocolName
		builder.append protocolDocument
		builder.append assayId
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append protocolId, other.protocolId
		builder.append protocolName, other.protocolName
		builder.append protocolDocument, other.protocolDocument
		builder.append assayId, other.assayId
		builder.isEquals()
	}

	static mapping = {
		id composite: ["protocolId", "protocolName", "protocolDocument", "assayId"]
		version false
	}

	static constraints = {
		protocolName maxSize: 500
		protocolDocument nullable: true
	}
}
