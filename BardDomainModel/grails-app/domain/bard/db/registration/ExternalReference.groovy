package bard.db.registration

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import bard.db.experiment.Experiment
import bard.db.experiment.Project

class ExternalReference implements Serializable {
    private static final int MODIFIED_BY_MAX_SIZE = 40

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

	static belongsTo = [Experiment, ExternalSystem]

	static mapping = {
		id( column: 'EXTERNAL_REFERENCE_ID', generator: 'sequence', params: [sequence: 'EXTERNAL_REFERENCE_ID_SEQ'])
	}

	static constraints = {
        experiment(nullable: true)
        project(nullable: true)
        extAssayRef(maxSize: 128)
        externalSystem()

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}
