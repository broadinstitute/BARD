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

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import bard.rest.api.wrapper.Dummy
import org.apache.log4j.Logger
import org.apache.log4j.Level
import bard.core.rest.spring.util.Node

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcentrationResponseSeries extends JsonUtil {

    @JsonIgnore
    Dummy dummy = new Dummy()
    @JsonIgnore
    static Logger log
    static {
        this.log = Logger.getLogger(ConcentrationResponseSeries.class)
        log.setLevel(Level.INFO)
    }


    @JsonProperty("responseUnit")
    private String responseUnit;
    @JsonProperty("testConcUnit")
    private String testConcentrationUnit;
    @JsonProperty("crSeriesDictId")
    private long dictElemId;
    @JsonProperty("concRespParams")
    private CurveFitParameters curveFitParameters;
    @JsonProperty("concRespPoints")
    private List<ConcentrationResponsePoint> concentrationResponsePoints = new ArrayList<ConcentrationResponsePoint>();
    @JsonProperty("miscData")
    private List<ActivityData> miscData = new ArrayList<ActivityData>();

    public boolean hasChildElements() {
        for (ConcentrationResponsePoint concentrationResponsePoint : concentrationResponsePoints) {
            if (concentrationResponsePoint.childElements) {
                return true
            }
        }
        return false
    }

    public String getDictionaryLabel() {
        if (dictElemId) {

            final Node dictionaryElement = dummy.dictionaryRestService.findDictionaryElementById(this.dictElemId)
            if (dictionaryElement) {
                return dictionaryElement.label
            }
        }
        return ""
    }

    public String getDictionaryDescription() {
        if (dictElemId) {
            final Node dictionaryElement = dummy.dictionaryRestService.findDictionaryElementById(this.dictElemId)
            if (dictionaryElement) {
                return dictionaryElement.description
            }
        }
        return ""
    }

    public String getYAxisLabel() {

        return getDictionaryLabel()
    }

    @JsonProperty("responseUnit")
    public String getResponseUnit() {
        return responseUnit;
    }

    @JsonProperty("responseUnit")
    public void setResponseUnit(String responseUnit) {
        this.responseUnit = responseUnit;
    }

    @JsonProperty("testConcUnit")
    public String getTestConcentrationUnit() {
        return testConcentrationUnit;
    }

    @JsonProperty("testConcUnit")
    public void setTestConcentrationUnit(String testConcUnit) {
        this.testConcentrationUnit = testConcUnit;
    }

    @JsonProperty("crSeriesDictId")
    public long getDictElemId() {
        return dictElemId;
    }

    @JsonProperty("crSeriesDictId")
    public void setDictElemId(long dictElemId) {
        this.dictElemId = dictElemId;
    }

    @JsonProperty("concRespParams")
    public CurveFitParameters getCurveFitParameters() {
        return curveFitParameters;
    }

    @JsonProperty("concRespParams")
    public void setCurveFitParameters(CurveFitParameters curveFitParameters) {
        this.curveFitParameters = curveFitParameters;
    }

    @JsonProperty("concRespPoints")
    public List<ConcentrationResponsePoint> getConcentrationResponsePoints() {
        return concentrationResponsePoints;
    }

    @JsonProperty("concRespPoints")
    public void setConcentrationResponsePoints(List<ConcentrationResponsePoint> concentrationResponsePoints) {
        this.concentrationResponsePoints = concentrationResponsePoints;
    }

    @JsonProperty("miscData")
    public List<ActivityData> getMiscData() {
        return miscData;
    }

    @JsonProperty("miscData")
    public void setMiscData(List<ActivityData> miscData) {
        this.miscData = miscData;
    }

    public List<Double> sorterdActivities() {
        List<Double> activities = []

        for (ConcentrationResponsePoint concentrationResponsePoint : this.concentrationResponsePoints) {
            if (concentrationResponsePoint.value != null) {
                if (concentrationResponsePoint.value.isNumber()) {
                    activities.add(new Double(concentrationResponsePoint.value))
                }
            }
        }
        return activities.sort()
    }

    public static ActivityConcentrationMap toDoseResponsePoints(List<ConcentrationResponsePoint> concentrationResponsePoints) {
        List<Double> concentrations = []
        List<Double> activities = []

        for (ConcentrationResponsePoint concentrationResponsePoint : concentrationResponsePoints) {
            if (concentrationResponsePoint.testConcentration != null && concentrationResponsePoint.value != null) {
                final String concentrationResponseValue = concentrationResponsePoint.value
                if (concentrationResponseValue.isNumber() && !concentrationResponsePoint.testConcentration.isNaN()) {
                    final Double currentPoint = new Double(concentrationResponsePoint.value)
                    if(currentPoint != 0){ //take out zero concentrations
                        concentrations.add(concentrationResponsePoint.testConcentration)
                        activities.add(currentPoint)
                    }

                }

            }
            else {
                log.warn("Concentration point/value can not be empty: '${concentrationResponsePoint.testConcentration}/${concentrationResponsePoint.value}'")
            }
        }

        ActivityConcentrationMap activityConcentrationMap = new ActivityConcentrationMap(activities: activities, concentrations: concentrations)
        return activityConcentrationMap
    }
}
public class ActivityConcentrationMap {
    List<Double> activities
    List<Double> concentrations
}
