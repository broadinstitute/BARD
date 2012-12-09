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

    static belongsTo = [project:Project, experiment:Experiment]

    static mapping = {
        id(column: 'EXTERNAL_REFERENCE_ID', generator: 'sequence', params: [sequence: 'EXTERNAL_REFERENCE_ID_SEQ'])
    }

    static constraints = {
        externalSystem()
        experiment(nullable: true, validator: validateExperimentOrProject)
        project(nullable: true, validator: validateExperimentOrProject)
        extAssayRef(blank:false, maxSize: EXT_ASSAY_REF_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    /**
     * ExternalReferences require an Experiment or a Project but can't have both
     */
    static def validateExperimentOrProject = {val, obj ->
        obj.experiment != null && obj.project == null || obj.experiment == null && obj.project != null
    }

}
