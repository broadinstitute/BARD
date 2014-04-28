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
import bard.db.enums.HierarchyType
import bard.db.registration.MeasureCaseInsensitiveDisplayLabelComparator

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/30/12
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentMeasure {

    private static final int MODIFIED_BY_MAX_SIZE = 40

    Element resultType
    Element statsModifier

    ExperimentMeasure parent
    HierarchyType parentChildRelationship

    Experiment experiment

    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy
    boolean priorityElement = false
    Set<ExperimentMeasure> childMeasures = [] as Set
    Set<AssayContextExperimentMeasure> assayContextExperimentMeasures = [] as Set


    static belongsTo = [experiment: Experiment,parent: ExperimentMeasure]
    static hasMany = [childMeasures: ExperimentMeasure, assayContextExperimentMeasures: AssayContextExperimentMeasure]

    static constraints = {
        parent(nullable: true)
        parentChildRelationship(nullable: true)
        experiment()
        resultType(nullable: false)
        statsModifier(nullable: true)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        table('EXPRMT_MEASURE')
        id(column: "EXPRMT_MEASURE_ID", generator: "sequence", params: [sequence: 'EXPRMT_MEASURE_ID_SEQ'])
        parent(column: 'PARENT_EXPRMT_MEASURE_ID')
        resultType(column: 'RESULT_TYPE_ID')
        statsModifier(column: 'STATS_MODIFIER_ID')
        priorityElement(column:'PRIORITY_ELEMENT')

    }
    /**
     * @return childMeasures sorted by displayLabel case insensitive
     */
    List<ExperimentMeasure> getChildrenMeasuresSorted() {
        childMeasures.sort(new MeasureCaseInsensitiveDisplayLabelComparator())
    }
    /**
     * @return concatenation of resultType.label and statsModifier.label if statsModifier is defined otherwise just the resultType.label
     */
    String getDisplayLabel() {
        String statsModifierLabel = statsModifier ? "(${statsModifier.label})" : null
        if (resultType == null && statsModifier == null) {
            return ""
        }
        return [resultType.label, statsModifierLabel].findAll().join(' ')
    }
}
