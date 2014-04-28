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

package bard.core.rest.spring.experiment
import bard.core.interfaces.ExperimentRole
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/26/12
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ExperimentAbstract extends JsonUtil {

    long bardExptId;
    long capExptId

    long bardAssayId
    long capAssayId

    long pubchemAid;
    long category;
    long type;
    long summary;
    long classification;

    long substances;
    long compounds;
    long activeCompounds;

    long confidenceLevel
    String name;
    String description;
    String source;
    String grantNo;
    String deposited;
    String updated;
    boolean hasProbe;
    List<Long> projectIdList = new ArrayList<Long>();
    String resourcePath;

    public ExperimentRole getRole() {
        return ExperimentRole.valueOf(this.getClassification().intValue())
    }

    public int getProjectCount() {
        return projectIdList.size()
    }

    public static Map<Long,Set<ExperimentAbstract>> groupByProjectIds(List<ExperimentAbstract> experiments) {
        Map<Long,Set<ExperimentAbstract>> grouping = [:]
        experiments.each { ExperimentAbstract experiment ->
            experiment.projectIdList.each { Long id ->
                if (!grouping.containsKey(id)) {
                    grouping[id] = []
                }
                grouping[id] << experiment
            }
        }
        return grouping
    }
}
