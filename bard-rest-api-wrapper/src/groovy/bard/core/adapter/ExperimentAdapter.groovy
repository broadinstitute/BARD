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

package bard.core.adapter

import bard.core.interfaces.ExperimentAdapterInterface
import bard.core.rest.spring.experiment.ExperimentAbstract
import bard.core.rest.spring.util.NameDescription
import bard.core.util.MatchedTermsToHumanReadableLabelsMapper
import org.apache.commons.lang3.text.WordUtils

/**
 * Created with IntelliJ IDEA.
 * User: ycruz
 * Date: 3/19/14
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentAdapter implements ExperimentAdapterInterface{

    final ExperimentAbstract experiment
    final Double score
    final NameDescription matchingField
    List<Long>  projectIdList = []
    Long ncgcWarehouseId;
    String status

    Boolean experimentFiles = Boolean.FALSE

    public ExperimentAdapter(ExperimentAbstract experiment, Double score = 0, NameDescription nameDescription = null){
        this.experiment = experiment
        this.matchingField = nameDescription
        this.score = score
    }

    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if (matchFieldName) {
            matchFieldName = MatchedTermsToHumanReadableLabelsMapper.matchTermsToHumanReadableLabels(matchFieldName)
            return "Matched Field: " + matchFieldName
        }
        return ""
    }

    @Override
    Long getId() {
        return experiment.getBardExptId()
    }

    public Double getScore() {
        return score
    }

    @Override
    String getName() {
        return experiment.name
    }

    @Override
    String getDescription() {
        return experiment.description
    }

    public Long getCapExptId() {
        return experiment.getCapExptId()
    }

    public Long getCapAssayId(){
        return experiment.capAssayId
    }

    public Long getActiveCompounds(){
        return experiment.activeCompounds
    }

    public List<Long> getProjectIdList(){
        return projectIdList
    }

    public Long getCompoundsTested(){
        return experiment.compounds
    }

    public Long getNcgcWarehouseId(){
        return ncgcWarehouseId
    }

    Boolean getExperimentFiles() {
        return experimentFiles
    }

    public Long getSubstancesTested(){
        return experiment.substances
    }

    public String getStatus(){
        return status
    }

    public Boolean  getHasProbe(){
        return experiment.hasProbe
    }
}
