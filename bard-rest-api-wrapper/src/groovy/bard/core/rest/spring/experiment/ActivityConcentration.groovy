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

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnore
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA

public class ActivityConcentration extends ActivityData {

    @JsonProperty("concResponseSeries")
    private ConcentrationResponseSeries concentrationResponseSeries

    @JsonProperty("childElements")
    private List<ActivityData> childElements

    @JsonProperty("childElements")
    public List<ActivityData> getChildElements() {
        return childElements
    }

    @JsonProperty("childElements")
    public void setChildElements(List<ActivityData> childElements) {
        this.childElements = childElements
    }
    @JsonProperty("concResponseSeries")
    public ConcentrationResponseSeries getConcentrationResponseSeries() {
        return concentrationResponseSeries
    }
    @JsonProperty("concResponseSeries")
    public void setConcentrationResponseSeries(ConcentrationResponseSeries concentrationResponseSeries) {
        this.concentrationResponseSeries = concentrationResponseSeries
    }
    public boolean hasPlot(){
        if(this.concentrationResponseSeries){
            return true
        }
        return false
    }
    public Double getSlope(){
        //if it has a qualifier that is not zero set to null
        if(this.qualifier && this.qualifier != "="){
             return null
        }
        if(hasPlot() && this.value?.isDouble()){
            return new Double(this.value)
        }
        return null
    }
    public boolean hasChildElements(){
        if(this.childElements || this.concentrationResponseSeries?.hasChildElements()){
            return true
        }
        return false
    }
    public String toDisplay(){
        String display = ""
        if(this.qualifier && this.qualifier != "="){
            display = display + qualifier
        }
        if(this.value){
            return display +  " " + this.value
        }
        return ""
    }
}
