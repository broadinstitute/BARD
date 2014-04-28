/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy
    static transients = ['aid']

    String getAid() {
        String aid = ""
        if (extAssayRef) {

            if (extAssayRef.startsWith("aid=")) { //anything after aid=
                aid = extAssayRef.substring(4, extAssayRef.length())
            }
        }
        return aid
    }
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
