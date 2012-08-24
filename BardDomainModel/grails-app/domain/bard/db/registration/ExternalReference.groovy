package bard.db.registration

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import bard.db.experiment.Experiment
import bard.db.experiment.Project

class ExternalReference implements Serializable {

	String extAssayRef
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	ExternalSystem externalSystem
    Experiment experiment
    Project project

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append externalSystem
		builder.append extAssayRef
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append externalSystem, other.externalSystem
		builder.append extAssayRef, other.extAssayRef
		builder.isEquals()
	}

	static belongsTo = [Assay, ExternalSystem]

	static mapping = {
		id( column: 'EXTERNAL_REFERENCE_ID', generator: 'sequence', params: [sequence: 'EXTERNAL_REFERENCE_ID_SEQ'])
	}

	static constraints = {
        extAssayRef maxSize: 128
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
