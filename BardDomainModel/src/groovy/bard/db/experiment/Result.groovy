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

package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.enums.ReadyForExtraction

class Result {
    private static final int VALUE_DISPLAY_MAX_SIZE = 256
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int RESULT_STATUS_MAX_SIZE = 20
    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20

    String resultStatus
    ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY

    Experiment experiment
    Element resultType
    Long substanceId
    Element statsModifier
    Integer replicateNumber

    String qualifier
    Float valueNum
    Float valueMin
    Float valueMax
    String valueDisplay

    Date dateCreated
    Date lastUpdated
    String modifiedBy
    Long id;
    transient ExperimentMeasure measure

    Set<ResultContextItem> resultContextItems = [] as Set<ResultContextItem>
    Set<ResultHierarchy> resultHierarchiesForResult = [] as Set<ResultHierarchy>
    Set<ResultHierarchy> resultHierarchiesForParentResult = [] as Set<ResultHierarchy>

    static QUALIFIER_VALUES = ['= ', '< ', '<=', '> ', '>=', '<<', '>>', '~ ']
    static hasMany = [resultHierarchiesForParentResult: ResultHierarchy,
            resultHierarchiesForResult: ResultHierarchy,
            resultContextItems: ResultContextItem]

    static belongsTo = [Experiment, ResultContextItem]

    static mappedBy = [resultHierarchiesForParentResult: "parentResult",
            resultHierarchiesForResult: "result"]

    static mapping = {
        id(column: "RESULT_ID", generator: "sequence", params: [sequence: 'RESULT_ID_SEQ'])

        resultType(column: "RESULT_TYPE_ID")
        qualifier(column: "qualifier", sqlType: "char", length: 2)
        replicateNumber(column: 'REPLICATE_NO')
    }

    static constraints = {
        resultStatus(maxSize: RESULT_STATUS_MAX_SIZE, nullable: false, inList: ["Pending", "Approved", "Rejected", "Mark for Deletion"])
        readyForExtraction(nullable: false)

        experiment()
        resultType()
        substanceId(nullable: false)
        statsModifier(nullable: true)
        replicateNumber(nullable: true)

        qualifier(nullable: true, blank: false, inList: QUALIFIER_VALUES)
        valueNum(nullable: true)
        valueMin(nullable: true)
        valueMax(nullable: true)
        valueDisplay(nullable: true, blank: false, maxSize: VALUE_DISPLAY_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true,)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    public String getDisplayLabel() {
        String label = resultType.label
        if(statsModifier != null) {
            label += " (${statsModifier.label})"
        }
        return label
    }
}
