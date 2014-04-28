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

package bard.db.project

class ProjectStep {
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int EDGE_NAME_MAX_SIZE = 128

    ProjectExperiment nextProjectExperiment
    ProjectExperiment previousProjectExperiment

    String edgeName

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    List<StepContext> stepContexts = [] as List

    static hasMany = [stepContexts: StepContext]

    static constraints = {
        nextProjectExperiment()
        previousProjectExperiment()
        edgeName(nullable: true, blank: false, maxSize: EDGE_NAME_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        id(column: "PROJECT_STEP_ID", generator: "sequence", params: [sequence: 'PROJECT_STEP_ID_SEQ'])
        stepContexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
        nextProjectExperiment(column: 'NEXT_PROJECT_EXPERIMENT_ID')
        previousProjectExperiment(column: 'PREV_PROJECT_EXPERIMENT_ID')
    }
}
