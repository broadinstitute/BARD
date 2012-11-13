package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.project.Project
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ExternalReference implements Serializable {
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int EXT_ASSAY_REF_MAX_SIZE = 128

    ExternalSystem externalSystem
    Experiment experiment
    Project project
    String extAssayRef

    Date dateCreated
    Date lastUpdated
    String modifiedBy

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

    static belongsTo = [project:Project, experiment:Experiment]

    static mapping = {
        id(column: 'EXTERNAL_REFERENCE_ID', generator: 'sequence', params: [sequence: 'EXTERNAL_REFERENCE_ID_SEQ'])
    }

    static constraints = {
        externalSystem()
        experiment(nullable: true)
        project(nullable: true)
        extAssayRef(blank:false, maxSize: EXT_ASSAY_REF_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}
